#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <stdio.h>
#include <libgen.h>

#define BUFFER_SIZE 1024

int port = 1234;
char* host = "127.0.0.1";
char* file_path;

int send_file(FILE * file, int c_socket);
void read_args(char* argv[]);
int send_filename(int c_socket);

int main(int argc, char* argv[]) {
  if (argc == 2) {
    file_path = argv[1];
  } else if (argc == 4) {
    host = argv[1];
    port = atoi(argv[2]);
    file_path = argv[3];
  } else {
    printf("\nUsage:\n");
    printf("\t %s <file to transfer>\n", argv[0]);
    printf("\t %s <host> <port> <file to transfer>\n", argv[0]);
    exit(0);
  }

  // open file in a binary mode
  printf("Opening file '%s'..\n", file_path);
  FILE * file = fopen(file_path, "rb");
  if (file == NULL) {
    printf("Cannot open file: %s\n", file_path);
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

  printf("Sending file '%s' to the server..\n", file_path);

  if (send_filename(c_socket) == -1) {
    printf("Failed to send filename to the server!\n");
    close(c_socket);
    fclose(file);
    exit(-4);
  }

  if (send_file(file, c_socket) == -1) {
    printf("Failure with sending file to the server!\n");
    close(c_socket);
    fclose(file);
    exit(-5);
  }

  close(c_socket);
  fclose(file);
  return 0;
}

int send_filename(int c_socket) {
  // retrieve filename and its length
  char* filename = basename(file_path);
  int filename_length = htonl(strlen(filename));

  // send length of filename
  if(send(c_socket, &filename_length, 4, 0) < 0) {
    printf("Failed to send filename length\n");
    return -1;
  }

  // send filename
  if(send(c_socket, filename, strlen(filename), 0) < 0) {
    printf("Failed to send filename\n");
    return -1;
  }

  return 0;
}

int send_file(FILE * file, int c_socket) {
  int sent_bytes = 0, file_length;
  char buffer[BUFFER_SIZE];

  // get file file_length
  fseek(file, 0L, SEEK_END);
  file_length = ftell(file);
  fseek(file, 0L, SEEK_SET);

  // convert integer from host byte order to network byte order
  int nfile_length = htonl(file_length);

  // send length of file through the socket
  if(send(c_socket, &nfile_length, 4, 0) < 0) {
    printf("Failed to send length of a file\n");
    return -1;
  }
  
  // send file by chunks
  printf("\t|  SENT  |  READ  |  LEFT  |\n");
  while (sent_bytes < file_length) {
    int read_bytes = fread(buffer, 1, BUFFER_SIZE, file);
    int sent_last_chunk = send(c_socket, buffer, read_bytes, 0);

    printf("\t| %6d | %6d | %6d |\n", sent_bytes, read_bytes, file_length - sent_bytes);

    if (sent_last_chunk < read_bytes) {
      printf("Failure sending file chunk.\n");
      return -1;
    }
    
    sent_bytes += read_bytes;
  }

  return sent_bytes;
}