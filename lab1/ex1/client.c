#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <stdio.h>
#include <stdint.h>
#include <libgen.h>

#define HOST "127.0.0.1"
#define PORT 31415

struct Request {
  int option;
  unsigned char n_char;
  unsigned short n_short;
  unsigned int n_int;
  unsigned long long n_long;
} Request;

int send_digit(char* host, int port, struct Request* req);
int64_t swap_int64(int64_t val);

int main(int argc, char* argv[]) {
  // validate program arguments
  int port;
  char* host;
  if (argc == 1) {
    port = PORT;
    host = HOST;
  } else if(argc == 3) {
    host = argv[1];
    port = atoi(argv[2]);
  } else {
    printf("\nUsage:\n");
    printf("\t %s\n", argv[0]); // default port and host
    printf("\t %s <host> <port>\n", argv[0]);
    exit(0);
  }

  int option = 0;

  printf("Select request type [1-4]:\n");
  printf(" 1) 1-byte\n");
  printf(" 2) 2-byte\n");
  printf(" 4) 4-byte\n");
  printf(" 8) 8-byte\n");

  option = getchar() - '0';

  struct Request* req = (struct Request*) malloc(sizeof(struct Request));

  req->option = option;
  printf("Enter a digit: ");
  switch(option) {
    case 1:
      scanf(" %hhu", &req->n_char);
      break;
    case 2:
      scanf("%hu", &req->n_short);
      break;
    case 4:
      scanf("%u", &req->n_int);
      break;
    case 8:
      scanf("%llu", &req->n_long);
      break;
    default:
      printf("Unknown mode. Exit.");
      exit(-1);
  }


  if(send_digit(host, port, req) == -1) {
    printf("Can't send a digit to the server! Exiting..\n");
    exit(-1);
  }

  return 0;
}

int send_digit(char* host, int port, struct Request* req) {
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

  // send digit to the server
  printf("Sending a %d-byte digit: ", req->option);
  int result = 0;
  switch(req->option) {
    case 1:
      printf("%hhu\n", req->n_char);
      result = send(c_socket, &req->n_char, req->option, 0);
      break;
    case 2:
      printf("%hu\n", req->n_short);
      req->n_short = htons(req->n_short);
      result = send(c_socket, &req->n_short, req->option, 0);
      break;
    case 4:
      printf("%u\n", req->n_int);
      req->n_int = htonl(req->n_int);
      result = send(c_socket, &req->n_int, req->option, 0);
      break;
    case 8:
      printf("%lld\n", req->n_long);
      req->n_long = swap_int64(req->n_long);
      result = send(c_socket, &req->n_long, req->option, 0);
      break;
  }


  if(result < 0) {
    printf("Failed to send!\n");
    return -1;
  } else {
    printf("Digit sent!\n");
  }

  int digit = 0;
  recv(c_socket, &digit, 1, 0);

  printf("Received a digit: %d\n", digit);

  return 0;
}

int64_t swap_int64(int64_t val) {
    val = ((val << 8) & 0xFF00FF00FF00FF00ULL ) | ((val >> 8) & 0x00FF00FF00FF00FFULL );
    val = ((val << 16) & 0xFFFF0000FFFF0000ULL ) | ((val >> 16) & 0x0000FFFF0000FFFFULL );
    return (val << 32) | ((val >> 32) & 0xFFFFFFFFULL);
}
