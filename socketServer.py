#!/usr/bin/env python3

import socket
import time
import subprocess

#Remeber to keep newline!
messageReceived = "Message received\n"
cMessage = ""

image = 'test_images/sample.jpg'

#-----------------------------------------------------------------------
#Function used to send the image
def sendImage(imageBytes):
    print("in sendImage")
    #While there are still bytes to send
    #while (imageBytes != 0):
    print("Within sendImage while loop")
    #Send the bytes
    conn.sendall(imageBytes)
    
    print("Sent file")
#-----------------------------------------------------------------------

#-----------------------------------------------------------------------
def createImageObject():
    #Create object to hold image
    sampleImage = image
    #open the image for reading
    readImage = open(sampleImage, 'rb')
    #Get the image bytes
    imageBytes = readImage.read()
    
    return imageBytes
#-----------------------------------------------------------------------

#-----------------------------------------------------------------------
#Function used to determine image size and send it to the server
def sendImageSize (imageBytes):
    print("About to send image size")
    #Get the number of bytes
    numBytes = len(imageBytes)
    #Create string to print out numBytes
    sNumBytes = "%d\n" % numBytes
    print("Sending image of size " + sNumBytes)
    #print(sNumBytes)
    #Send the number of bytes to expect
    sizeMessage = "Image size "
    sizeMessage += sNumBytes 
    sendString(sizeMessage)
    print("Leaving sendImageSize")
#-----------------------------------------------------------------------

#-----------------------------------------------------------------------
#Function to send strings to the client
#This probably doesn't need to be function, but I'm doing this because Spencer
#will give me a hard time if I dont "mOdUlArIzE mY fUnCtIoNs"
def sendString(str):
    #Call sendall to forward the string
    conn.sendall(str.encode())
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
    output = subprocess.check_output("./New_Image_Processing.py", shell=True)
    #Convert the output from byte to string
    output = output.decode("utf-8")
    #Split the string after the 9th space (FOR THIS EXAMPLE ONLY)
    output = output.split("\n")[1]
    #Replace newlines with commas
   # output = output.replace('\n',', ', 3)
    sOutput = "%s\n" % output
    print(sOutput)
    return sOutput
#-----------------------------------------------------------------------
while True:
    socketConnected = False        
    if (~socketConnected):
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
                socketConnected = True        

                #While we're connected, wait for data to come in
                while socketConnected:
                    print('Beginning action!')
                    #Recieve our message from the client
                    cMessage = conn.recv(1024)
                    cMessage = cMessage.decode("utf-8") 
                    cMessage = cMessage.replace('\n','')
                    #Print the client message for testing purposes
                    print("Message from client: " + repr(cMessage))
                    
                    #Check the client message to determine next action
                    
                    #If cMessage is empty, close the socket
                    if not cMessage:
                        closeSocket()
                        break
                    
                    elif (cMessage == "This is the Client"):
                        print("Matched with this is the client")
                        sendString(messageReceived)

                    elif ("Laser intensity" in cMessage):
                        print("Matched with laser intensity")
                        sendString(messageReceived)
                    
                    elif ("Camera exposure" in cMessage):
                        print("Matched with camera exposure")
                        sendString(messageReceived)

                    elif ("Send image size" in cMessage):
                        print("Matched with send image size, taking picture")
                        #Call laserControl to turn on laser
                        subprocess.call("./laserControl.py", shell=True) 
                        imageBytes = createImageObject() 
                        print('calling laserControl.py and camera.sh')
                        sendImageSize(imageBytes)
                   
                   #Once the client has recieved the image, client sends ACK for us to
                   #send the image outputs
                    elif(cMessage == "Send image attributes"):
                        print ("Sending sample concentration")
                        #Send supporting data to accompany the image
                        getImageOutputs()
                        sendString(getImageOutputs())

                    #Check for first message to initiate analysis
                    elif (cMessage == "Ready for image"):
                        print ("Initiating Analysis")
                        #Send the image to the client 
                        sendImage(imageBytes)
                        #Wait 5 seconds before sending data to the client so they have
                        #time to look at the pretty colors that is the image
                        #print ("going to sleep")
                        #time.sleep(5)
                   
                    #Once the client is done, check if they want to close the socket
                    elif (cMessage == "close"):
                        #Call close socket
                        closeSocket()
                        socketConnected = False

