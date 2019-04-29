from PIL import Image 
import time
import string
import Adafruit_MCP4725

acceptPic = 0
concentration = 0
dac = Adafruit_MCP4725.MCP4725()

#Set the voltage
def setVoltage(x):
	if x == 0:
		dac.set_voltage(3195)#set to 3.9V
	elif x == 1:
		dac.set_voltage(3277)#set to 4.0V
	elif x == 2:	
		dac.set_voltage(3358)#set to 4.1V

#Camera takes picture and saves it in piControl
def takePic():
	raspistill -ex auto -w 3280 -h 2464 -q 50 -100000 -o /home/pi/piControl/sampleImage.jpg
		print 'taking picture' 

#process image based on laser intensity
def processImage(z):
	global concentration
	global acceptPic

	#read in image
	img = Image.open('sampleImage.jpg')
	total = 0
	
	for x in range(700,2100):
		for y in range(800,2000):
			pixel = img.getpixel((x,y))			
			if pixel[1] > 10 and pixel[1] > pixel[2] and pixel[1] > pixel[0]:
				total = total + 1
				break
	#check if in correct laser range and calculate cam
	if z == 0 and total < 480 and total > 1018:
		concentration = 334.945 - 2.58665*total + 0.00706678*total**2 - (7.823577*10**(-6))*total**3 + (3.100589*10**(-9))*total**4 
		acceptPic = 1
	elif z == 1 and total > 425 and total < 1372:
		concentration = -1082.64 + 7.3584*total - 0.0190526*total**2 + (2.38123*10**(-5))*total**3 - (1.43231*10**(-8))*total**4 + (3.3334*10**(-12))*total**5
		acceptPic = 1
	elif z == 2 and total > 421 and total < 1368:
		concentration = -535.69588 + 2.47444*total - (3.452522*10**(-3))*total**2 + (1.41899*10**(-6))*total**3
		acceptPic = 1
	

for x in range(0,3):
	setVoltage(x)#turn on laser to set level
	takePic()
	processImage(x)
	if(acceptPic == 1):
		print str(concentration)
		break
		
image = Image.open('sampleImage.jpg')
image = image.crop((700,800,2100))#crop it to important area
image = image.resize((175,175))#make it 1.4 Kbytes
image.save('sampleImage.jpg')#save final image

dac.set_voltage(0)#set to 0V
		

