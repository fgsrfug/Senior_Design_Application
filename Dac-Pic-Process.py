#!/usr/bin/env python2.7
from PIL import Image 
import subprocess
import time
import string
import Adafruit_MCP4725

concentration = 0
dac = Adafruit_MCP4725.MCP4725()

#Set the voltage
def setVoltage():

	dac.set_voltage(3195)#set to 3.9V

#Camera takes picture and saves it in piControl
def takePic():
	subprocess.call('./camera.sh', shell=True)
	print 'taking picture' 

#process image based on laser intensity
def processImage():
	global concentration
	#read in image
	img = Image.open('sampleImage.jpg')
	total = 0
	
	for x in range(1500,1900):
		for y in range(1050,1450):
			pixel = img.getpixel((x,y))			
			if pixel[1] > 5 and pixel[1] > pixel[2] and pixel[1] > pixel[0]:
				total = total + 1
                                g = pixel[1] + 25
                                r = pixel[0]
                                b = pixel[2]
                                img.putpixel((x,y),(r,g,b))
				
	#check if in correct laser range and calculate concentration-linear piecewise
	if total > 30890 and total < 39929:#0-5%
		con = (5.532813253*10**(-4))*total - 17.09159952
	elif total > 39928 and total < 58919:#5-10%
		con = (2.39343316*10**(-4))*total - 4.101528993
	elif total > 58918 and total < 66102:#10-20%
		con = (1.296494774*10**(-3))*total - 65.7005333
	elif total > 66101 and total < 84339:#20-40%
		con = (1.083195821*10**(-3))*total - 51.35540802
	elif total > 84338 and total < 108129:#40-60%
		con = (8.303536561*10**(-4))*total - 29.78508461
	elif total > 108128 and total < 138525:#60-80%
		con = (6.486892884*10**(-4))*total - 9.858840917
	
setVoltage()#turn on laser to set level
takePic()
processImage()
print str(concentration)
image = Image.open('sampleImage.jpg')
image = image.crop((1100,800,2200,1700))#crop it to important area
image.save('sampleImage.jpg')#save final image
dac.set_voltage(0)#set to 0V
