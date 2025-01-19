import socket # import socket module
import sys # import sys module
import argparse # import argparse module

# Parser
parser = argparse.ArgumentParser(description="Client for server communication.")
parser.add_argument('--ip', type=str, default='server', help='IP address of the server')
parser.add_argument('--port', type=int, default=5555, help='Port number to connect to')

# Call to the parser
args = parser.parse_args()

dest_ip = args.ip
dest_port = args.port

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM) # Define socket working with IPv4 and TDP protocol

s.connect((dest_ip, dest_port)) # Connect socket to destination

msg = input("") # Print and get msg to send to the server
while True:
    # Checks if nothing was inputted
    if not msg:
        print("400 Bad Request")
        msg = input("")
        continue
    s.send(bytes(msg, 'utf-8')) # send msg to the server
    data = s.recv(4096) # receive msg from the server
    print(data.decode('utf-8')) # print received msg

    msg = input("") # get more data

s.close() # close socket