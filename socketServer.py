#!/usr/bin/env python3

import socket
import time
import subprocess

#global variables
message = "giddy-up\n"
cMessage = ""

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
#will give me a hard time if I dont "mOdUlArIzE mY fUnCtIoNs"
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

#-----------------------------------------------------------------------
#Function to get stdout of image_process.py and store result as a string
def getImageOutputs():
    #Call imageProcess.py and get it's output
    output = subprocess.check_output("./image_process.py", shell=True)
    #Convert the output from byte to string
    output = output.decode("utf-8")
    #Split the string after the 9th space (FOR THIS EXAMPLE ONLY)
    output = output.split(" ")[9]
    #Replace newlines with commas
    output = output.replace('\n',', ', 3)
    print(output)
#-----------------------------------------------------------------------

#Create variables for host and port. Leave host blank so as to listen listen for connections on port 9090
host = ''
port = 9090

#Debugging print statement and message to be sent to the client
print("Beginning socket communication")

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
            print('Beginning action!')
            #Recieve our message from the client
            cMessage = conn.recv(1024)
            
            #Print the client message for testing purposes
            print("Message from server: " + repr(cMessage))
            
            #Check the client message to determine next action
            
            #If cMessage is empty, close the socket
            if not cMessage:
                closeSocket()
                break

            #Check for first message to initiate analysis
            elif (cMessage == "This is the Client"):
                #Call laserControl to turn on laser
                subprocess.call("./laserControl.py", shell=True) 
                print('calling laserControl.py and camera.sh')
                #Send the image to the client
                sendImage()
                #Wait 5 seconds before sending data to the client so they have
                #time to look at the pretty colors that is the image
                time.sleep(5)
           
           #Once the client has recieved the image, client sends ACK for us to
           #send the image outputs
            elif(cMessage == "Got image"):
                #Send supporting data to accompany the image
                sendString(str = getImageOutputs)

            #Once the client is done, check if they want to close the socket
            elif (cMessage == "close"):
                #Call close socket
                closeSocket()

