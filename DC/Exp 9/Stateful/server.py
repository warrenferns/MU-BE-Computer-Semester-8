import socket
import time
HOST = '127.0.0.1' # Symbolic name meaning all available interfaces
PORT = 8853 # Arbitrary non-privileged port
with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
    s.bind((HOST, PORT))
    s.listen(1)
    print('Server started and listening on port', PORT)
    while True:
        conn, addr = s.accept()
        print('Client connected:', addr)
        data = b''
        start_time = time.time()
        while True:
            chunk = conn.recv(1024)
            data += chunk
            if len(chunk) < 1024:
                break
        num1, num2 = map(int, data.decode().split())
        result = num1 * num2
        response_time = time.time() - start_time
        print('Received:', num1, num2)
        print('Result:', result)
        print('Response time:', response_time)
        conn.sendall(str(result).encode()) 