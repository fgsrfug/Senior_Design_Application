import numpy as np
import cv2
import array as arr
threshold = [[95, 200],[25, 172],[75, 210]] #thresholds for each level

#read in original, RGB, diff intensity
img = cv2.imread('Test.jpg')
red_img = cv2.imread('Test.jpg')
green_img = cv2.imread('Test.jpg')
blue_img = cv2.imread('Test.jpg')
img1 = cv2.imread('Test1.png')
img2 = cv2.imread('Test2.png')
img3 = cv2.imread('Test3.png')

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

#takes image separates into 3 pic of R,G,B
def rgbPic(x):
    width = np.size(x, 1)-1
    height = np.size(x, 0)-1
    for x in range(0,height):#loop through entire pic
        for y in range(0,width):
            red_pix = red_img[x,y]
            green_pix = green_img[x,y]
            blue_pix = blue_img[x,y]
	    #zero out other color
            red_pix[1] = 0 
            red_pix[0] = 0 
            blue_pix[1] = 0 
            blue_pix[2] = 0 
            green_pix[0] = 0 
            green_pix[2] = 0

    showpic(red_img)
    showpic(blue_img)
    showpic(green_img)

#averages out green pixel, checks threshold for level, prints concentrations
def averageGreen(x, level): 
    showpic(x)
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
    average = total/(greenPix)
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
showpic(img)
rgbPic(img)
averageGreen(img1, 0)
averageGreen(img2, 1)
averageGreen(img3, 2)
