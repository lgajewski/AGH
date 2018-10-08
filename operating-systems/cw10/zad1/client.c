#include <sys/resource.h>
#include <sys/sysinfo.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <arpa/inet.h>
#include <sys/stat.h>
#include <pthread.h>
#include <unistd.h>
#include <syslog.h>
#include <signal.h>
#include <sys/un.h>
#include <getopt.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
#include <fcntl.h>
#include <netdb.h>
#include <stdio.h>
#include "commons.h"

typedef struct thread_params {
   int socket;
   struct sockaddr* server;
   socklen_t server_size;
   Message msg;
} thread_params;

char* CLIENT_ID;
struct sockaddr* server;
socklen_t server_size;

struct sockaddr_un server_un;

struct sockaddr_un sock_un;

int type = 0;	// 0 - unix, 1 - internet

void clean(int arg) {

    char path[256] = {0};
	sprintf(path, "/tmp/s%d", getpid());
	unlink(path);

    printf("[client] clean\n");

	exit(0);
}

void* run(void* args) {
	thread_params* params = (thread_params*) args;

	while(1) {

		//try to receive some data, this is a blocking call
		err(recvfrom(params->socket, &params->msg, sizeof(params->msg), 0, params->server, &params->server_size) == -1)

		printf("<%s>: %s\n", params->msg.name, params->msg.content);

	}

	return NULL;
}

int get_internet_socket(char * address, int port) {
	int sock;

	struct sockaddr_in* sock_in = malloc(sizeof(struct sockaddr_in));
	sock_in->sin_family = AF_INET;
    sock_in->sin_port = htons(port);

	err((sock = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP)) == -1)
    err(inet_aton(address, &sock_in->sin_addr) < 0)

    server = (struct sockaddr*)sock_in;
    server_size = sizeof(*sock_in);

    return sock;
}

int get_unix_socket(char * file) {
    char path[256] = {0};
	int sock;
	sprintf(path, "/tmp/s%d", getpid());

	path[strlen(path)] = '\0';
	unlink(path);

	err((sock = socket(PF_UNIX, SOCK_DGRAM, 0)) == -1)

    sock_un.sun_family = AF_UNIX;
    strcpy(sock_un.sun_path, path);

    err(bind(sock, (struct sockaddr*)&sock_un, SUN_LEN(&sock_un)) < 0)

    server_un.sun_family = AF_UNIX;
    strcpy(server_un.sun_path, file);
    printf("path: %s\n", server_un.sun_path);

    server = (struct sockaddr*)&server_un;
    server_size = SUN_LEN(&sock_un);

    return sock;
}


int main(int argc, char* argv[]) {
	if(argc < 2) {
		printf("usage: %s [CLIENT_ID]\n", argv[0]);
		return 0;
	}

    signal(SIGINT, clean);

	CLIENT_ID = argv[1];
	CLIENT_ID[strlen(argv[1])] = '\0';

	printf("Choose socket type:\n");
	printf("u) UNIX socket\n");
	printf("i) INTERNET socket\n");
	printf("> ");
	char c = getchar();
	getchar();

	char buffer1[MAX_NAME_LENGTH];
	char buffer2[MAX_NAME_LENGTH];

	int sock;

	switch(c) {
		case 'u':
			type = 0;
			printf("\nUNIX> Enter server path: ");
			fgets(buffer1, 99, stdin);
			buffer1[strlen(buffer1)] = '\0';

			// get unix socket
			sock = get_unix_socket(buffer1);
			break;
		case 'i':
			type = 1;
			printf("\nINET> Enter server address: ");
			fgets(buffer1, 99, stdin);
			buffer1[strlen(buffer1)] = '\0';

			printf("\nINET> Enter server port: ");
			fgets(buffer2, 99, stdin);
			buffer2[strlen(buffer2)] = '\0';

			// get internet socket
			sock = get_internet_socket(buffer1, atoi(buffer2));
			break;
		default:
			printf("Unsupported operation\n");
			return 0;
	}

	Message msg;
	msg.size = server_size;
	strcpy(msg.name, CLIENT_ID);

	// create thread for receiving message
	pthread_t thread;

	thread_params* params = (thread_params*)malloc(sizeof(thread_params));
	params->socket = sock;
	params->msg = msg;
	params->server = server;
	params->server_size = server_size;

	err(pthread_create(&thread, NULL, &run, (void*)params) < 0)

	printf("Waiting for commands..\n");
	while(1) {
        scanf("%s", msg.content);
         
        //send the message
        err(sendto(sock, &msg, sizeof(msg) , 0 , server, server_size)==-1)

    }

    err(pthread_join(thread, NULL));


	close(sock);

	return 0;
}