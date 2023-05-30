import socket
import time
HOST = '127.0.0.1' # The server's hostname or IP address
PORT = 8853 # The port used by the server
with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
    s.connect((HOST, PORT))
    print("Calculating product")
    num1 = 5
    num2 = 6
    start_time = time.time()
    s.sendall(f'{num1} {num2}'.encode())
    data = b''
    while True:
        chunk = s.recv(1024)
        data += chunk
        if len(chunk) < 1024:
            break
    response_time = time.time() - start_time
    result = int(data.decode())
    print('Received:', response_time) 