# Senior_Design_Application
Application made for ECE 44x Design Project

This is the main collection of code used for ECE group 21's senior design project. There are two main branches,
the master branch and the piControl branch. The master branch is composed of code for the Android application, whereas
the piControl branch are programs meant to be run on the Raspberry Pi.

The files of interest on the Master branch are MainActivity.java, located under 
Senior_Design_Application/app/src/main/java/fgsrfug/seniordesignapp, and the activity_main.xml. The MainActivity.java 
describes the operation of the app, with comments sprinkled in throuout. The main functionality is the socket communication, 
which is the basis of the entire application.

The activity_main.xml describes how the application looks. This is where improvement is needed the most. I (Jesus Narvaez) hope
to make more changes to it in due time, but feel free to make changes to it how you see fit.

The piControl branch is predominantly python, and are the programs ran from the Pi in order to communicate, light up the sample,
and take the picture. The camera folder contains two bash scripts, one for taking pictures and one taking videos. 
We only ever ended up using the camera one, but the video could come in use at some point. camera.sh has comments that describe
what the options do to the image. Dac-Pic-Process.py is where the camera is instructed to take a picture and analyze the image.
This is called from within the socketServer.py program, which should always be running as soon as the Pi turns on. socketServer.py
waits for a connection and then proceeds through the protocol, calling the appropriate programs when appropriate. Once the other
programs are done, socketServer.py sends it's data to application and closes the socket, waiting for the next device to initiate
a connection.

The biggest improvements that should be made on the Pi side is to find a way to always have socketServer.py running at all times. 
It currently runs into issues every once in awhile that cause it to stop. 
