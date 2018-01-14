The [prase_rssi.py](parse_rssi.py) script will parse the raw RSSI measurement
file created by the app, and save the parsed data into a numpy array. It also
saves the numpy array to disk. This script assume the data was recorded in a
 specific way: TODO describe ?????
 
 TODO describe loaded data format

### Usage
1. Make sure numpy and pandas python packages are installed in your python 
environment.
2. Place the measurement file(s) to be parsed on a directory. The measurement 
file(s) need to be named as below:
<label_name>_<first_gesture_start>_<gesture_gap>_<...>.txt
example:
swipe_7_10_20161223044637_20161223045638_small_room.txt
Here the label is 'swipe',
the first gesture started after 7 seconds from the recording start
the gap between consecutive gestures is 10 seconds 
3. From a python interpreter (e.g. ipython or python) execute:
```python
raw_data_dir = '/path/to/raw/data/directory'
parsed_data_dir = '/path/to/parsed/data/directory'
parsed_data_resolution = '5ms'
parse_and_dump(
    input_dir=raw_data_dir, 
    output_dir=parsed_data_dir, 
    resolution=parsed_data_resolution
)
```
The parsed data will be saved compressed under parsed_data_dir, named as 
<label_name>_<resolution>.npz, (e.g. wisture_5ms.npz).
4. You can load the parsed data as follows:
```python
import numpy as np
dataset_path = '/path/to/parsed/data/directory/wisture_5ms.npz'
npz_file = np.load(dataset_path)
data = npz_file['data']
labels = npz_file['labels']
print(data.shape)
print(labels.shape)
```
