import time
import string
import cv2
import array as arr
#import Adafruit_MCP4725

acceptPic = 0
concentration = 0
#dac = Adafruit_MCP4725.MCP4725()

#Set the voltage
def setVoltage(x):
	if x == 0:
		print 'dac at level ' + str(x)
		#dac.set_voltage(3195)#set to 3.9V
	elif x == 1:
		print 'dac at level ' + str(x)
		#dac.set_voltage(3277)#set to 4.0V
	elif x == 2:	
		print 'dac at level ' + str(x)
		#dac.set_voltage(3358)#set to 4.1V

#Camera takes picture and saves it in piControl
def takePic():
	#raspistill -ex auto -w 640 -h 480 -q 50 -100000 -o /home/pi/piControl/sampleImage.jpg
		print 'taking picture' 

#process image based on laser intensity
def processImage(x):
	global concentration
	global acceptPic

	#read in image
	img = cv2.imread('sampleImage.jpg')
	total = 0
	
	for x in range(700,2100):
		for y in range(800,2000):
			pixel = img[x,y]
			if pixel[1] > 10 and pixel[1] > pixel[2] and pixel[1] > pixel[0]:
				total = total + 1
				break
	#check if in correct laser range and calculate cam
	if x == 0 and total < 718 and total > 379:
		concentration = 2812.4701 - 24.5755*total + 0.077886*total**2 - 0.000105603*total**3 + (5.2190053*10**(-8))*total**4
		acceptPic = 1
	elif x == 1 and total > 429 and total < 1171:
		concentration = -171.1545 + 0.79789*total - 0.001025137*total**2 + (4.628246*10**(-7))*total**3
		acceptPic = 1
	elif x == 2 and total > 367 and total < 417:
		concentration = con = -5771.4376 + 34.938545*total - (6.57038*10**(-2))*total**2 + (3.63577*10**(-5))*total**3
		acceptPic = 1
	

for x in range(0,3):
	setVoltage(x)#turn on laser to set level
	takePic()
	processImage(x)
	if(acceptPic == 1):
		print str(concentration)
		break

dac.set_voltage(0)#set to 0V
		

