This repository contains the source code relateds to the reserach work reported by the paper: M.Haseeb and R.Parasuraman, "Wisture: RNN-based Learning of Wireless Signals for Gesture Recognition in Unmodified Smartphones", submitted to the Journal of Special Topics in Signal Processing.
# Wisture Solution
Wisture is a machine learning (LSTM-RNN) and custom signal processing based solution for recognizing over-the-air hand gestures (such as swipe, push, pull, etc.) using Wi-Fi Received Signal Strength on unmodified smartphones (without requiring changes to existing hardware and software/OS of the phone).

# Winiff App
As a part of Wisture, we developed an stand-alone Android app called "Winiff" (available in the Google Playstore: https://play.google.com/store/apps/details?id=com.nebula.haseeb.winiff&hl=en). Using Winiff, one can record high-frequency Wi-Fi RSS values and save them to text files (the app provide means for exporting the saved RSSI files). The app uses an artificaial traffic induction (between the phone and the Wi-Fi access point (AP)) approach to enable useful and meaningful Radio Signal Strength values. We invite researchers, engineers, and the App develeopment community to test and improve the Winiff application and enable crowd-sourced smartphone Wi-Fi RSS datasets available for various machine learning related researches.
The app includes options to enable or disable traffic induction, and supports both positive and negative RSSI values reported by the Wi-Fi device drivers on the smartphone.

[Here](https://github.com/mohaseeb/wisture/tree/master/winiff/parse_rssi_measurements) 
you will find helper python code to parse the raw recorded RSSI measurements 
into numpy arrays.

# Wisture App
The machine learning (including signal processing) part of the Wisture solution is handled by the "Wisture" app (available in the Google PlayStore as a Beta version: https://play.google.com/store/apps/details?id=org.nebula.wisture) developed in the course of this work. We plan to release the source codes of the Wisture in future, after upgrading the app to a stable version. Please note the wisture app needs local calibration on the phone. This calibration feature will be available on the future stable version.

# Stand-alone Machine Learning solutions
As part of the Wisture project, we developed codes/applications to implement several state-of-art machine learning solutions for classifying touch-less hand gestures based on the Wi-Fi RSS time-series data from smartphones.
The implemented solutions include:
1. Learning Time-Series Shapelets (LTS) method in Python: https://github.com/mohaseeb/shaplets-python
2. K-NN DTW in Python: Link will be available soon
3. Fast Shapelets (FS) : Link will be available soon
4. Shapelet Transform Ensemble (STE): Link will be available soon
5. Collective of Transformation Ensembles (COTE): Link will be available soon
6. LSTM-RNN: Link will be available soon

# Licence

<a rel="license" href="http://creativecommons.org/licenses/by-nc/4.0/"><img alt="Creative Commons License" style="border-width:0" src="https://i.creativecommons.org/l/by-nc/4.0/88x31.png" /></a><br />All codes and applications in this work is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by-nc/4.0/">Creative Commons Attribution-Non Commercial 4.0 International License</a>.
