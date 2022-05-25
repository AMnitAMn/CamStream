import socket 
from server_thread import ServerThread

def main():

    #get local ip address of server
    hostname = socket.gethostname()
    local_ip = socket.gethostbyname(hostname)

    #default port for socket
    port = 6666

    #start the server thread
    image_server = ServerThread(local_ip, port)
    image_server.start()


if __name__ == "__main__":
    main()
