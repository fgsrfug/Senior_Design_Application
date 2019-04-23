import string
import numpy as np
import cv2
import array as arr
threshold = [[95, 200],[25, 172],[75, 210]] #thresholds for each level


#take the picture
def takePicture(x):
	rapidstill -ex auto -w 640 -h 480 -q 50 -t 10000 -sh 100 -ss 110000 -o /home/pi/piControl/SampleImage.jpg

#read in original, RGB, diff intensity
img = cv2.imread('sampleImage.jpg')

#transfer function for level 1
def function1(x):
	y = 0.75*x + 16
	return y

#transfer function for level 2
def function2(x):
	y = 1.25*x - 125 
	return y

#transfer function for level 3
def function3(x):
	y = 0.25*x + 20
	return y

#shows the picture
def showpic(x):
	cv2.imshow('image',x)
	cv2.waitKey(0)

#averages out green pixel, checks threshold for level, prints concentrations
def averageGreen(x, level): 
    testimg = x
    width = np.size(x, 1)-1
    height = np.size(x, 0)-1
    total = 0 
    greenPix = 0
    average = 0
    for x in range(0,height):
        for y in range(0,width):
            pixel = testimg[x,y]
            if pixel[1] > 35 and pixel[1] > pixel[2] and pixel[1] > pixel[0]: #green values that will matter
            	total = total + pixel[1]#total up green count
                greenPix = greenPix + 1
                break
    if greenPix > 0:
    	average = total/(greenPix)
    else:
	average = 0
    if average > threshold[level][0] and average < threshold[level][1]:#check if in threshold
	print 'The average green pixel value of ' + str(average) + ' is with in level ' + str(level+1) + ' pixel value range of: ' + str(threshold[level][0]) + '-' +str(threshold[level][1])
        #find concentration
        if(level == 0):
    	    y = function1(average)
        elif(level == 1):
    	    y = function2(average)
        elif(level == 2):
    	    y = function3(average)
        print 'The concentration is ' + str(y) + '%'
    else: #not in range
	print 'The average green pixel value of ' + str(average) + ' is not within level ' + str(level+1) + ' pixel value range of: ' + str(threshold[level][0]) + '-' +str(threshold[level][1])

#function calls
for x in range(0,3):
	takePicture(x)
	averageGreen(img, 1)
