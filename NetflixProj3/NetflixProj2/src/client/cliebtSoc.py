import socket

# יצירת סוקט TCP
client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

# הגדרת ה-IP והפורט של השרת
server_ip = '127.0.0.1'
server_port = 5555

# התחברות לשרת
client_socket.connect((server_ip, server_port))

# שליחת המידע
message = "POST 5 50"
client_socket.sendall(message.encode('utf-8'))

# סגירת החיבור
client_socket.close()
