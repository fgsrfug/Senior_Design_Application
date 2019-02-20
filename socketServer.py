#!/usr/bin/env python3

import socket
import time

#Create variables for host and port. Leave host blank so as to listen listen for connections on port 9090
host = ''
port = 9090

image = 'luigi.jpeg'
    
#Debugging print statement and message to be sent to the client
print("Beginning socket communication")
message = "giddy-up\n"
cMessage = ""
sNumBytes = ""
#while True:

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
            data = conn.recv(1024)
            
            cMessage = data
            #if (data == cMessage):
            #    print('should close connection')
            #    break
            #Break if we didn't get anything
            print("Message from server: " + repr(data))
            if not data:
                print('breaking to close')
                break
            
            #print('below data recv')
            
            #Print the data to the terminal
            
            myfile = open(image, 'rb')
            fileBytes = myfile.read()
            numBytes = len(fileBytes)
            sNumBytes = "%d" % numBytes
            print(numBytes)
            print(sNumBytes)
             
            while (fileBytes):
                conn.sendall(fileBytes)
                fileBytes = myfile.read()
                print("Sent file")
            #Send our message out to the client
            #conn.sendto(message.encode(),(host,port))
            #conn.sendall(data)
        
        print("About to close socket")
        conn.close()
        print("closing socket")
      #  time.sleep(60)
      #  print("slept for 60 seconds")
