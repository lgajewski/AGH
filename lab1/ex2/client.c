#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <stdio.h>

#define BUFFER_SIZE 1024

int port = 1234;
char* host = "127.0.0.1";
char* file_name;

int send_file(FILE * file, int c_socket);
void read_args(char* argv[]);

int main(int argc, char* argv[]) {
  if (argc == 2) {
    file_name = argv[1];
  } else if (argc == 4) {
    host = argv[1];
    port = atoi(argv[2]);
    file_name = argv[3];
  } else {
    printf("\nUsage:\n");
    printf("\t %s <file to transfer>\n", argv[0]);
    printf("\t %s <host> <port> <file to transfer>\n", argv[0]);
    exit(0);
  }

  // open file in a binary mode
  printf("Opening file '%s'", file_name);
  FILE * file = fopen(file_name, "rb");
  if (file < 0) {
    printf("Cannot open file: %s\n", argv[2]);
    exit(-1);
  }

  // create a socket
  int c_socket;
  if ((c_socket = socket(AF_INET, SOCK_STREAM, 0)) < 0) {
    printf("There is a problem with opening a socket..\n");
    exit(-2);
  }

  // set host and port
  struct sockaddr_in serv_addr;
  bzero((char*)&serv_addr, sizeof(serv_addr));
  serv_addr.sin_family = AF_INET;
  serv_addr.sin_addr.s_addr = inet_addr(host);
  serv_addr.sin_port = htons(port);

  printf("Connecting to server.\n");
  printf("\thost -> %s\n", host);
  printf("\tport -> %d\n", port);
  if (connect(c_socket, (struct sockaddr*) &serv_addr, sizeof(serv_addr)) < 0) {
    printf("Failure while connecting to server..\n");
    close(c_socket);
    exit(-3);
  }

  printf("Sending file '%s' to the server..", file_name);
  if (send_file(file, c_socket) == -1) {
    printf("Failure with sending file to the server!\n");
    close(c_socket);
    fclose(file);
    exit(-4);
  }

  close(c_socket);
  fclose(file);
  return 0;
}

int send_file(FILE * file, int c_socket) {
  int total_sent = 0, length;
  char buffer[BUFFER_SIZE];

  // get file length
  fseek(file, 0L, SEEK_END);
  length = ftell(file);
  fseek(file, 0L, SEEK_SET);

  // convert integer from host byte order to network byte order
  int rlength = htonl(length);

  // send length of file through the socket
  send(c_socket, &rlength, 4, 0);
  
  while (total_sent < length) {
    int bytes_read = fread(buffer, 1, BUFFER_SIZE, file);
    
    if (send(c_socket, buffer, bytes_read, 0) < bytes_read) {
      printf("Failure sending file chunk.\n");
      return -1;
    }
    
    total_sent += bytes_read;
  }

  return total_sent;
}