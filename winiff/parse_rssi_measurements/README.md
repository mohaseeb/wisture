## Parsing an RSSI measurement file using python
Below commands shows how to use python to parse, load and plot data from an 
RSSI measurement file created by the Winiff app. The Winiff app 
create text files with below format (first column is measurement time, 
second column is the RSSI value):
```text
145634607014179	-66.0
145634615740486	-66.0
145634619692833	-67.0
145634623423563	-68.0
```
* Install required libraries
```commandline
$ pip install -r requirements.txt
```
* Start a python interpreter (e.g. ipython or python)
```commandline
$ ipython
```
* Specify the path to the measurement file, and load into a Dataframe. 
```python
measurement_file = '/path/to/the/measurement/file'
import pandas as pd
df = pd.read_csv(measurement_file, sep='\t', names=['time', 'rssi'], index_col='time')
```
* Plot the data
```python
import matplotlib.pyplot as plt
df.plot()
plt.show()
```
* Convert to regular timeseries (The RSSI measurements made by Winiff have 
irregular time intervals).
```python
df.index = pd.to_datetime(df.index, unit='ns')  # convert index to datetime
df_regular = df.resample('10ms').mean()  # 10ms between samples
```
* Put the data into numpy array and plot
```python
data = df_regular.values.ravel()
plt.plot(data)
plt.show()
```


## Parsing structurally recorded measurement data
[prase_rssi.py](parse_rssi.py) script is used to load the dataset described 
[here](https://ieee-dataport.org/documents/wi-fi-signal-strength-measurements-smartphone-various-hand-gestures). 
The output format is suited for using data to train machine learning models.
 The script can also be used for when the RSSI recording is made while a 
 repeated event (e.g. a hand gesture) was happening repeatedly with a fixed gap 
 between consecutive events (e.g. every 10 seconds, a hand gesture is 
 performed). For every recording:
* First connect the phone to Wi-Fi, and start a new measurement in the 
Winiff app.
* Start the event, which is intended to happen while the RSSI recording is 
ongoing (e.g. start performing the hand gesture). 
* Mark the time the subject event was started in seconds, *start_time* (e.g. 
the time when the first hand gesture was performed after the recording started, 
e.g. 7 seconds).
* Mark the time *gap*, in seconds, between each two occurrences of the 
repeated event (e.g. the time between two consecutive hand gestures, e.g. 10 
seconds).
* Name the recoding file as below:
```commandline
<label_name>_<start_time>_<gap>_<...>.txt
```
example:
```commandline
swipe_7_10_20161223044637_20161223045638_small_room.txt
```
Here the events have 'swipe' as label, the first event started 7 seconds 
after the recording was started and the gap between consecutive events was 10 
seconds.
When run, the script will:
* Parse the raw data.
* Resample it into regular timeseries (The RSSI measurements made by Winiff 
have irregular time intervals).
* Chunk the data into windows (one window per each one of the repeated events).
* Store the results into two numpy arrays (one for the event windows and 
another for the corresponding labels).
 * Dump the arrays to disk after compressing them.
### Usage
1. Make the script dependencies available in your python environment.
```commandline
pip install -r requirements.txt
```
2. Specify you dataset labels using the *LABELS* variable in [prase_rssi.py](parse_rssi.py)  
(make sure they are the same as the ones used to name your measurements 
files). E.g.:
```python
LABELS = ['swipe', 'push', 'pull']
``` 
2. Load the script from a python interpreter (e.g. ipython or python) 
```commandline
ipython -i parse_rssi.py
```
or 
```commandline
python -i parse_rssi.py
```
and execute:
```python
parse_and_dump(
    input_dir='/path/to/raw/data/directory', 
    output_dir='/path/to/parsed/data/directory', 
    resolution='5ms'  # output data resolution
)
```
The parsed data will be saved compressed under the sepcified output_dir, named
 as <label_name>\_\<resolution>.npz, (e.g. wisture_5ms.npz).
3. You can load the parsed data as follows:
```python
import numpy as np
dataset_path = '/path/to/parsed/data/directory/wisture_5ms.npz'
npz_file = np.load(dataset_path)
data = npz_file['data']
labels = npz_file['labels']
print(data.shape)
print(labels.shape)
```
