#!/usr/bin/env python2.7
from PIL import Image 
import subprocess
import time
import string
import Adafruit_MCP4725

acceptPic = 0
concentration = 0
dac = Adafruit_MCP4725.MCP4725()

#Set the voltage
def setVoltage(x):

	dac.set_voltage(3195)#set to 3.9V

#Camera takes picture and saves it in piControl
def takePic():
	subprocess.call('/camera/./camera.sh', shell=True)
	print 'taking picture' 

#process image based on laser intensity
def processImage():
	global concentration
	global acceptPic

	#read in image
	img = Image.open('sampleImage.jpg')
	total = 0
	
	for x in range(1500,1900):
		for y in range(1050,14500):
			pixel = img.getpixel((x,y))			
			if pixel[1] > 5 and pixel[1] > pixel[2] and pixel[1] > pixel[0]:
				total = total + 1
				
	#check if in correct laser range and calculate cam
	if x > 139 and x < 213:
		con = 0.0694444*x - 9.72222
	elif x > 211 and x < 248:
		con = 0.142857*x - 25.2857
	elif x > 246 and x < 254:
		con = 1.666667*x - 401.6666
	elif x > 252 and x < 342:
		con = 0.22727272*x - 37.5
	elif x > 340 and x < 411:
		con = 0.289855*x - 58.84057
	elif x > 413 and x < 419:
		con = 2.5*x - 965

	
setVoltage()#turn on laser to set level
takePic()
processImage()
print str(concentration)

		
image = Image.open('sampleImage.jpg')
image = image.crop((1500,1050,1900,1450))#crop it to important area
image.save('sampleImage.jpg')#save final image

dac.set_voltage(0)#set to 0V
		

