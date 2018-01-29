from __future__ import division, print_function

from os import path, listdir
import pandas as pd
import numpy as np

LABELS = ['swipe', 'push', 'pull']


def parse_and_dump(input_dir, output_dir, resolution='10ms'):
    """
    Args:
        input_dir (str): the dirctory that contains the RSSI measurements files
        output_dir (str): the directory to which parsed data is dumped
        resolution (pandas.freqstr): resolution of the output (regular
        timeseries) data.
    """
    # load the data
    data, labels = _load_dataset(input_dir, resolution)
    # data.shape -> n_samples X sample_size
    # labels.shape -> n_samples

    # print summary
    print('resolution: {}'.format(resolution))
    print('data shape: {}'.format(data.shape))
    print('labels shape: {}'.format(labels.shape))
    print('number of samples: {}'.format(data.shape[0]))
    print('sample size: {}'.format(data.shape[1]))

    # dump it compressed
    dump_path = path.join(output_dir, 'wisture_{}.npz'.format(resolution))
    np.savez_compressed(file=dump_path, data=data, labels=labels)
    print('Parsed data saved to: {}'.format(dump_path))

    # to load it
    # loaded = np.load(dump_path)
    # data = loaded['data']
    # labels = loaded['labels']


def _load_dataset(ds_path, freq='10L'):
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
        return parts[0] in LABELS and ext == 'txt'

    samples = []
    labels = []
    for ds_file in listdir(ds_path):
        if not _is_data_file(ds_file):
            continue
        print('processing: {}'.format(ds_file))

        # load the data into DataFrame
        file_path = path.join(ds_path, ds_file)
        data_raw, start, gap, label = _load_measurement_data(file_path)

        # resample: irregular -> regular
        data_regular = data_raw.resample(freq).mean().interpolate()

        # chunk it
        data_windowed = _get_windows(
            data_regular,
            offset=start + 's',
            size=gap + 's',
            step=gap + 's'
        )

        samples.extend([window.values.ravel() for window in data_windowed])
        label = LABELS.index(label)
        labels.extend([label] * len(data_windowed))

    samples = np.vstack(samples)
    labels = np.array(labels)

    return samples, labels


def _read_raw_file_into_df(file_path):
    """
    Expected file format is:
        time	rssi
        145634607014179	-66.0
        145634615740486	-66.0
        145634619692833	-67.0
        145634623423563	-68.0
    or:
        145634607014179	-66.0
        145634615740486	-66.0
        145634619692833	-67.0
        145634623423563	-68.0

    """
    df = pd.read_csv(file_path, sep='\t', header=None)
    # df = pd.read_csv(file_path, sep='\t', )
    #
    # import ipdb; ipdb.set_trace()

    # loading with head=None, will ignore the head line in the file. Below is
    # to remove the ignored header line
    if df.iloc[0][0] == 'time':
        df.drop([0], inplace=True)
        # the header row will cause the file to loaded as staring, below is
        # to convert it float64
        df = df.astype('float64')

    # Some measurements files doesn't have a header (i.e. time, rssi),
    # we'll create a header if there wasn't one
    # such files will have [0, 1] as columns
    if 'time' not in df.columns:
        df.rename(columns={0: 'time', 1: 'rssi'}, inplace=True)

    return df


def _load_measurement_data(file_path):
    """
    Args:
        file_path (str):

    Returns:
        pd.DataFrame
    """
    # load the file
    data = _read_raw_file_into_df(file_path)

    # time column to datatime type
    first_row_index = data.first_valid_index()
    data['time'] -= data['time'][first_row_index]
    # all measurements times are now relative to the first measurement
    data['time'] = pd.to_datetime(data['time'], unit='ns')
    # time is in nanoseconds
    data = data.set_index('time')

    # get the metadata from the filepath
    file_name_parts = path.basename(file_path).split('_')
    label = file_name_parts[0]
    first_event_start = file_name_parts[1]
    event_gap = file_name_parts[2]

    return data, first_event_start, event_gap, label


def _get_windows(data, offset=0, size='10s', step='10s'):
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
    # raw_data_path = path.expanduser(
    #     '~/ws/data/wisture/raw_exclude_no_induction'
    # )
    # parsed_data_path = path.expanduser(
    #     '~/ws/data/wisture/raw_exclude_no_induction_processed'
    # )
    # parse_and_dump(raw_data_path, parsed_data_path, '5ms')
    # parse_and_dump(raw_data_path, parsed_data_path, '10ms')
    # parse_and_dump(raw_data_path, parsed_data_path, '50ms')
    # parse_and_dump(raw_data_path, parsed_data_path, '100ms')
    pass
