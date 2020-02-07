#! /home/patryk/anaconda3/bin/python3

def recv_timeout(the_socket,timeout=2):
    #make socket non blocking
    the_socket.setblocking(0)
    
    #total data partwise in an array
    total_data=[];
    data='';
    
    #beginning time
    begin=time.time()
    while 1:
        #if you got some data, then break after timeout
        if total_data and time.time()-begin > timeout:
            break
        
        #if you got no data at all, wait a little longer, twice the timeout
        elif time.time()-begin > timeout*2:
            break
        
        #recv something
        try:
            data = the_socket.recv(8192)
            if data:
                total_data.append(data)
                #change the beginning time for measurement
                begin=time.time()
            else:
                #sleep for sometime to indicate a gap
                time.sleep(0.1)
        except:
            pass
    
    #join all parts to make final string
    ret_string = ""
    for i,val in enumerate(total_data):
        ret_string += val.decode('ascii')
    return ret_string



import socket
import time
print("SIIIIEEEEMA")
   
   
TCP_IP = '127.0.0.1'
TCP_PORT = 5005
BUFFER_SIZE = 1024
MESSAGE = bytearray(b' witam ' )
"""print(type(MESSAGE))
print(dir(MESSAGE))
print(vars(MESSAGE))
exit()
"""
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.connect((TCP_IP, TCP_PORT))

"""for i in range(0,10):
	temp_str = MESSAGE[:]
	i_str = str(i).encode('ascii')
	temp_str.extend(bytearray(i_str))
	temp_str.extend(b'\n')"""

while(True):
	mess = input("message: ")
	if mess == "exit" : break 
	s.sendall(mess.encode('ascii'))
	s.sendall(b'\n')

	data = recv_timeout(s, timeout = 0.2)
	print("received:", data)
	#time.sleep(0.5)


s.close()
 

