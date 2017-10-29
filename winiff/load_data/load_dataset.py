from __future__ import division, print_function

from os import path, listdir
import pandas as pd
import numpy as np

SUPPORTED_GESTURES = ['swipe', 'push', 'pull']
DATA_PATH = path.expanduser(
    '~/ws/data/wisture/raw_exclude_no_induction'
)
OUT_PATH = path.expanduser(
    '~/ws/data/wisture/raw_exclude_no_induction_processed'
)


def parse_and_dump(ds_path, out_path, freq='10L'):
    # load the data
    data, labels = load_dataset(ds_path, freq)
    # data.shape -> n_samples X sample_size
    # labels.shape -> n_samples

    # print summary
    print('freq: {}'.format(freq))
    print('data shape: {}'.format(data.shape))
    print('labels shape: {}'.format(labels.shape))
    print('number of samples: {}'.format(data.shape[0]))
    print('sample size: {}'.format(data.shape[1]))

    # dump it compressed
    dump_path = path.join(out_path, 'wisture_{}.npz'.format(freq))
    np.savez_compressed(file=dump_path, data=data, labels=labels)

    # to load it
    # loaded = np.load(dump_path)
    # data = loaded['data']
    # labels = loaded['labels']


def load_dataset(ds_path, freq='10L'):
    """
    Args:
        ds_path (str): root directory containing the raw data files
        freq (pandas.freqstr): returned data resolution. (10L is 10ms)

    Returns:
        (np.array, np.array): (data, labels)
            data.shape -> n_samples X sample_size
            labels.shape -> n_samples

    """
    def _is_data_file(file_name):
        parts = file_name.split('_')
        ext = parts[-1].split('.')[1]
        return parts[0] in SUPPORTED_GESTURES and ext == 'txt'

    #
    i = 0
    samples = []
    labels = []
    for ds_file in listdir(ds_path):
        if not _is_data_file(ds_file):
            continue
        print('processing: {}'.format(ds_file))

        # load the data into DataFrame
        file_path = path.join(ds_path, ds_file)
        data_raw, start, gap, gesture = load_measurement_data(file_path)

        # resample: irregular -> regular
        data_regular = data_raw.resample(freq).mean().interpolate()

        # chunk it
        data_windowed = get_windows(
            data_regular,
            offset=start + 's',
            size=gap + 's',
            step=gap + 's'
        )

        samples.extend([window.values.ravel() for window in data_windowed])
        label = SUPPORTED_GESTURES.index(gesture)
        labels.extend([label] * len(data_windowed))

    samples = np.vstack(samples)
    labels = np.array(labels)

    return samples, labels


def load_measurement_data(file_path):
    """
    Args:
        file_path (str):

    Returns:
        pd.DataFrame
    """
    # load the file
    data = pd.read_csv(file_path, sep='\t')

    # time column to datatime type
    data['time'] -= data['time'][0]
    data['time'] = pd.to_datetime(data['time'], unit='ns')
    data = data.set_index('time')

    # get the metadata from the filepath
    file_name_parts = path.basename(file_path).split('_')
    gesture = file_name_parts[0]
    first_gesture_start = file_name_parts[1]
    gesture_gap = file_name_parts[2]

    return data, first_gesture_start, gesture_gap, gesture


def get_windows(data, offset=0, size='10s', step='10s'):
    """
    Args:
        data (pd.DataFrame):
        offset (freqstr): seconds
        size (freqstr): seconds
        step (freqstr): seconds

    Returns:
        list(pd.DataFrame)
    """
    offset = pd.to_timedelta(offset)
    size = pd.to_timedelta(size)
    step = pd.to_timedelta(step)

    win_start = data.index[0] + offset
    win_end = win_start + size

    windows = []
    while win_end <= data.index[-1]:
        windows.append(data[win_start: win_end])

        win_start += step
        win_end = win_start + size

    return windows


if __name__ == '__main__':
    parse_and_dump(DATA_PATH, OUT_PATH, '5L')
    parse_and_dump(DATA_PATH, OUT_PATH, '10L')
    parse_and_dump(DATA_PATH, OUT_PATH, '50L')
    parse_and_dump(DATA_PATH, OUT_PATH, '100L')
