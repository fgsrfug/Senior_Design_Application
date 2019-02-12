import numpy as np
import cv2

img = cv2.imread('Test.png')
cv2.imshow('image',img)
cv2.waitKey(0)
width = np.size(img, 1)-1
height = np.size(img, 0)-1
print(width)
print(height)
sum = 0 
greenPix = 0
average = 0
for x in range(0,height):
    for y in range(0,width):
        pixel = img[x,y]
        pixel[0] = 0 #make blue 0
        pixel[2] = 0 #make red 0
        if pixel[1] > 0:
            sum = sum + pixel[1]
            greenPix = greenPix + 1
            print pixel[1]
cv2.imshow('image',img)
cv2.waitKey(0)
average = sum/(greenPix)
print("the average green color is", average)


