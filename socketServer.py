#!/usr/bin/env python3

import socket
import time

#Create variables for host and port. Leave host blank so as to listen listen for connections on port 9090
host = ''
port = 9090

#Debugging print statement and message to be sent to the client
print("Beginning socket communication")
message = "giddy-up\n"
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
        
        #While we're connected, wait for data to come in
        while True:
            print('in while true')
            data = conn.recv(1024)
            print('recieved data')
            
            #Break if we didn't get anything
            if not data:
                break
            
            print('below data recv')
            
            #Print the data to the terminal
            print(repr(data))
            
            #Send our message out to the client
            conn.sendto(message.encode(),(host,port))
            #conn.sendall(data)

      #  conn.close()
      #  print("closing socket")
      #  time.sleep(60)
      #  print("slept for 60 seconds")
