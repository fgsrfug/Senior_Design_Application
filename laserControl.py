#!/usr/bin/env python3

import RPi.GPIO as GPIO
import time

GPIO.setmode(GPIO.BCM)
GPIO.setwarnings(False)
GPIO.setup(25, GPIO.OUT)

while True:
    GPIO.output(25, GPIO.HIGH)
    print('setting high')
    time.sleep(.5)
    print('completed sleep')
    GPIO.output(25, GPIO.LOW)
    print('setting low')
    time.sleep(.5)
    print('completed sleep')


