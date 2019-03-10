#!/usr/bin/env python3

import socket
import time
import subprocess

#image = 'chungus.jpg'

#-----------------------------------------------------------------------
#Function used to send the image
def sendImage():
    #Create object to hold image
    sampleImage = 'sampleImage.jpg'
    #open the image for reading
    readImage = open(sampleImage, 'rb')
    #Get the image bytes
    imageBytes = readImage.read()
    #Get the number of bytes
    numBytes = len(fileBytes)
    #Create string to print out numBytes
    sNumBytes = "%d" % numBytes
    print(numBytes)
    print(sNumBytes)
    
    #While there are still bytes to send
    while (fileBytes):
        #Send the bytes
        conn.sendall(fileBytes)
        fileBytes = myfile.read()
        print("Sent file")
#-----------------------------------------------------------------------

#-----------------------------------------------------------------------
#Function to send strings to the client
#This probably doesn't need to be function, but I'm doing this because Spencer
#will give me shit if I dont "mOdUlErIzE mY pRoGrAmS"
def sendString(str):
    #Call sendall to forward the string
    conn.sendall(str)
#-----------------------------------------------------------------------

#-----------------------------------------------------------------------
#Function to close the socket
#This also probably doesn't need to be a function, but whatevs
def closeSocket():
    print("About to close socket")
    conn.close()
    print("closing socket")
#-----------------------------------------------------------------------

#Create variables for host and port. Leave host blank so as to listen listen for connections on port 9090
host = ''
port = 9090

#Debugging print statement and message to be sent to the client
print("Beginning socket communication")
message = "giddy-up\n"
cMessage = ""
sNumBytes = ""
#while True:

subprocess.call("./laserControl.py", shell=True) 

#Create the socket to be a standart TCPstream called serverSocket
with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as serverSocket:
    
    #Bind to port 9090
    serverSocket.bind((host, port))
    
    #Listen for any connections on the port 
    serverSocket.listen()
    
    #Assign the return values of accept() to conn and addr
    #conn is a socket object 
    conn, addr = serverSocket.accept()
    
    with conn:
        
        #Tell the user that we have been connected to 
        print('Connected by', addr)
        data = ''
        

        #While we're connected, wait for data to come in
        while True:
            print('top of while loop')
            print(data)
            cMessage = conn.recv(1024)
            
            #Print the client message for testing purposes
            print("Message from server: " + repr(cMessage))
            
            #If cMessage is empty, close the socket
            if not cMessage:
                closeSocket()
                break
            
            #Check the client message to determine next action
            if (cMessage == "This is the Client"):
                #Call laserControl to turn on laser
                subprocess.call("./laserControl.py", shell=True) 
                print('calling laserControl.py and camera.sh')
                #Send the image to the client
                sendImage()
                #Wait 5 seconds before sending data to the client so they have
                #time to look at the pretty colors that is the image
                time.sleep(5)
                #Send supporting data to accompany the image
                sendString(str)
            
            if (cMessage == "close"):
                #Call close socket
                closeSocket()

            

            #print('below data recv')
            
            #Print the data to the terminal
            
            #Send our message out to the client
            #conn.sendto(message.encode(),(host,port))
            #conn.sendall(data)
        
      #  time.sleep(60)
      #  print("slept for 60 seconds")
