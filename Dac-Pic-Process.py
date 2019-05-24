#!/usr/bin/env python2.7
from PIL import Image, ImageEnhance
import subprocess
import time
import string
import Adafruit_MCP4725 
import numpy

dac = Adafruit_MCP4725.MCP4725()

concentration = 0
total =0

#Camera takes picture and saves it in piControl
def takePic():
	subprocess.call('./camera.sh', shell=True)

def transFunc(total):
    global concentration
    if total < 3150:
        concentration = 0
    if total > 3150 and total < 3307:#0-5%
        concentration = 3.168567807*10**(-2)*total - 99.8225602
    if total > 3307 and total < 4145:#5-10%
        concentration = 5.973715651*10**(-3)*total - 14.76224612
    if total > 4144 and total < 4987:#10-20%
        concentration = 1.18807176*10**(-2)*total - 39.24795058
    if total > 4986 and total < 5832:#20-40%
        concentration = 2.361832782*10**(-2)*total - 97.78223902
    if total > 5832 and total < 7912:#40-60%
        concentration = 9.620472365*10**(-3)*total - 16.12294964
    if total > 7911 and total < 8910:#60-80%
        concentration =2.510040161*10**(-2)*total - 138.6094378
    if total > 8909 and total < 9762:#80-100%
        concentration = 1.901140684*10**(-2)*total - 85.57794677
    if total > 9761:
        concentration = 100


#process image based on laser intensity
def processImage():
	global total
	#read in image
	img = Image.open('sampleImage.png')
        total = 0
	for x in range(220,420):
		for y in range(150,350):
			pixel = img.getpixel((x,y))			
                        if pixel[1] > 1 and pixel[1] > pixel[2] and pixel[1] > pixel[0]:
                                total = total + 1
        #print 'total is ' +str(total)
        img.save('sampleImage.png')#save final image

samples = [0,0,0]
for x in range(0,3):
    dac.set_voltage(3195)#set to 3.9V
    takePic()
    dac.set_voltage(0)#set to 0V
    processImage()    
    samples[x] = total
    
image = Image.open('sampleImage.png')
image = image.crop((220,150,420,350))#crop it to important area
enhancer = ImageEnhance.Brightness(image)
image = enhancer.enhance(1.8)
image.save('sampleImage.png')#save final image

nump_samp = numpy.array(samples)
maxArea = numpy.min(nump_samp, axis=0) + 700
for x in numpy.nditer(nump_samp):
    if x > maxArea:
            nump_samp = numpy.delete(nump_samp, numpy.where(nump_samp == x), axis=0)

meanArea = numpy.mean(nump_samp, axis=0)
#print(nump_samp)
#print 'Here is the mean area ' +str(meanArea)
transFunc(meanArea)
print 'Concentration is ' +str(round(concentration,4)) + '%'








