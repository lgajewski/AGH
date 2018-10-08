#include <sys/resource.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <syslog.h>
#include <signal.h>
#include <sys/un.h>
#include <getopt.h>
#include <stdio.h>
#include <sys/socket.h>
#include <errno.h>
#include <fcntl.h>
#include <netdb.h>
#include <sys/time.h>
#include "commons.h"
  
#define TIMEOUT 10

char* SERVER_PATH;

int sock_ux = 0;
int sock_in = 0;
int user_no = 0;

// sockaddr structures
struct sockaddr_in server_in;
struct sockaddr_un server_ux;

User* users[MAX_USERS];

// set of socket descriptors
fd_set readfds;

// msg buffer
char buffer[MAX_MSG_LENGTH];

int init_sock_in(int port) {
	int sock;

	err((sock = socket(PF_INET, SOCK_DGRAM, 0)) == -1)

    server_in.sin_family = AF_INET;
    server_in.sin_port = htons(port);
    server_in.sin_addr.s_addr = htonl(INADDR_ANY);

    err(bind(sock, (struct sockaddr*)&server_in, sizeof(server_in)) < 0)

    printf("[server] inet socket created\n");
    return sock;
}

int init_sock_ux(char * file) {
	int sock;

	err((sock = socket(PF_UNIX, SOCK_DGRAM, 0)) == -1)

    server_ux.sun_family = AF_UNIX;
    strcpy(server_ux.sun_path, file);

    err(bind(sock, (struct sockaddr*)&server_ux, SUN_LEN(&server_ux)) < 0)

    printf("[server] unix socket created\n");
    return sock;
}

void clean(int arg) {
	int i;
	close(sock_ux);
	close(sock_in);
	for (i = 0; i < MAX_USERS; ++i) {
		if (users[i] != NULL) {
			free(users[i]->sockaddr);
			free(users[i]);
		}
	}
	unlink(SERVER_PATH);

    printf("[server] clean\n");

	exit(0);
}

// zwraca roznice czasow w sekundach
float get_time_diff(struct timeval t0, struct timeval t1) {
    return (t1.tv_sec - t0.tv_sec);
}

void handle_request(int socket, int mode) {
	int recv_len;
	Message msg;

	struct sockaddr address;
	socklen_t addrlen = (socklen_t) sizeof(address);
	err((recv_len = recvfrom(socket, &msg, sizeof(msg), 0, &address, &addrlen))<0)

	printf("msg: %s %s\n", msg.name, msg.content);

    //inform user of socket number - used in send and receive commands
    if(mode == MODE_INET) {
		printf("New INET connection\n");
	} else if(mode == MODE_UNIX) {
		printf("New UNIX connection\n");
	}
    printf("UserID: %s, content: %s\n", msg.name, msg.content);

    // check if user is logged
    int logged = 0;
    int i,j = -1;
    for(i=0; i<MAX_USERS; i++) {
    	if(users[i]->socket != -1) {
    		if(strcmp(users[i]->name, msg.name) == 0) {
    			logged = 1;
    			j = i;
    		}
    	} else {
    		if(j == -1) j = i;
    	}
    }

    if(j == -1) {
    	printf("[server] can't handle new connection - server is full\n");
    	strcpy(msg.name, "[server]");
    	strcpy(msg.content, "server is full");
    } else {
		if(!logged) {
			// add user
			users[j]->mode = mode;
    		strcpy(users[j]->name, msg.name);
    		users[j]->size = msg.size;
    		users[j]->socket = socket;
    		users[j]->confirmed = 0;
    		gettimeofday(&users[j]->time, NULL);
    		users[j]->sockaddr = (struct sockaddr*)malloc(sizeof(struct sockaddr));
    		memcpy(users[j]->sockaddr, &address, sizeof(address));
    		printf("Adding to list of sockets as %d\n" , j);
    		strcpy(msg.name, "[server]");
    		strcpy(msg.content, "User registered");
    	} else {
    		if(users[j]->confirmed == 0) {
    			if(strcmp(msg.content, "confirm") == 0) {
	            	// confirm user
	            	struct timeval now;
	            	gettimeofday(&now, NULL);
	            	printf("time: %f\n", get_time_diff(users[j]->time, now));
	            	if(get_time_diff(users[j]->time, now) > TIMEOUT) {
	            		// delete user
						users[j]->socket = -1;
						strcpy(users[j]->name, "-");
	        			strcpy(msg.name, "[server]");
	    				strcpy(msg.content, "too late for confirmation");
	            	} else {
	            		// confirm
	            		users[j]->confirmed = 1;
		        		strcpy(msg.name, "[server]");
	    				strcpy(msg.content, "successfully confirmed");
	            	}
	        	} else {
        			strcpy(msg.name, "[server]");
        			strcpy(msg.content, "user is not confirmed - type 'confirm'");
	        	}
        	} else {
        		Message msg_to_sent;
        		strcpy(msg_to_sent.name, msg.name);
        		strcpy(msg_to_sent.content, msg.content);
        		strcpy(msg.name, "[server]");
        		strcpy(msg.content, "message sent to other users");

        		for(i=0; i<MAX_USERS; i++) {
        			if(users[i]->socket != -1 && strcmp(users[i]->name, msg_to_sent.name) != 0
                                && users[i]->confirmed == 1) {
        				printf("sent message to: %d\n", i);
        				if( sendto(users[i]->socket, &msg_to_sent, sizeof(msg_to_sent), 0, 
        					users[i]->sockaddr, users[i]->size) == -1) {
                            printf("[server] can't send message to user#%d\n", i);
                            printf("[server] deleting %d user\n", i);
                            // delete
                            users[i]->socket = -1;
                            strcpy(users[i]->name, "-");
                        }
        			}
        		}
        	}
    	} 
    }

    //send new connection message
    err( sendto(socket, &msg, sizeof(msg), 0, &address, addrlen) == -1) 

    printf("\n");
}

int main(int argc, char ** argv) {
	int opt = 1;
	if(argc != 3) { 
        printf("usage: %s [SERVER_PATH] [PORT_NO]\n", argv[0]);
        return 0;
    }

    signal(SIGINT, clean);

    int PORT_NO = atoi(argv[2]);
    SERVER_PATH = argv[1];

    // try to unlink
    unlink(SERVER_PATH);

    // init users
	int i;
	for (i = 0; i < MAX_USERS; i++) {
		users[i] = (User*) malloc(sizeof(User));
		users[i]->size = -1;
		users[i]->mode = -1;
		users[i]->socket = -1;
		users[i]->confirmed = 0;
		users[i]->sockaddr = NULL;
	}

 	sock_ux = init_sock_ux(SERVER_PATH);
	sock_in = init_sock_in(PORT_NO);

	// not necessary, but recommended
	err(setsockopt(sock_in, SOL_SOCKET, SO_REUSEADDR, (char *)&opt, sizeof(opt)) < 0)
	err(setsockopt(sock_ux, SOL_SOCKET, SO_REUSEADDR, (char *)&opt, sizeof(opt)) < 0)

	int sd, max_sd;
	int activity;

	printf("Waiting for connections...\n");
	while(1) {
		//clear the socket set
        FD_ZERO(&readfds);
  
        //add master socket to set
        FD_SET(sock_in, &readfds);
        FD_SET(sock_ux, &readfds);
        max_sd = sock_in;
         
        //add child sockets to set
        for ( i = 0 ; i < MAX_USERS ; i++) {

            //socket descriptor
            sd = users[i]->socket;
             
            //if valid socket descriptor then add to read list
            if(sd > 0) FD_SET( sd , &readfds);
             
            //highest file descriptor number, need it for the select function
            if(sd > max_sd) max_sd = sd;
        }

        //wait for an activity on one of the sockets , timeout is NULL , so wait indefinitely
        activity = select( max_sd + 1 , &readfds , NULL , NULL , NULL);
		if ((activity < 0) && (errno!=EINTR)) {
            printf("select error");
        }
  
        //If something happened on the master socket , then its an incoming connection
        if (FD_ISSET(sock_in, &readfds)) {
	        handle_request(sock_in, MODE_INET);
        }

        if(FD_ISSET(sock_ux, &readfds)) {
        	handle_request(sock_ux, MODE_UNIX);
        }
        
	}

	return 0;
}

