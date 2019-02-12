#!/bin/bash

DATE=$(date +"%Y-%m-%d_%H%M")

raspistill -ex verylong -o /home/pi/camera/$DATE.jpg
