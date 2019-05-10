#!/bin/bash

DATE=$(date +"%Y-%m-%d_%H%M")

#raspistill -ex verylong -o /home/pi/camera/$DATE.jpg
#Exposure (ex) = very long
#Width of image (w) = 640 pixels
#Height of image (h) = 480 pixels
#Quality of image (q) = 50 (The lower the number, the more compressed the image is)
#Preview (t) = .5 second preview before image is taken
#Sharpness (sh) = Sharpest image can be
raspistill -ex auto -w 640 -h 480 -q 50 -e png -t 5000 -awb fluorescent -ISO 700 -ss 160000 -o /home/pi/piControl/sampleImage.png
                                                           
