#ifndef _COMMONS_H_
#define _COMMONS_H_

#define err(X) if(X) {printf("%s\n", strerror(errno));exit(-1);}

#define MAX_MSG_LENGTH 256
#define MAX_NAME_LENGTH 72
#define MAX_USERS 32

#define MODE_UNIX 0
#define MODE_INET 1
#define REQ_LOGIN 0
#define REQ_LOGOUT 1
#define REQ_SEND 2

typedef struct User {
	int mode;
	int confirmed;
	size_t size;
	char name[MAX_NAME_LENGTH];
	int socket;
	struct sockaddr* sockaddr;
	struct timeval time;
} User;

typedef struct Message {
	size_t size;
	char name[MAX_NAME_LENGTH];
	char content[MAX_MSG_LENGTH];
} Message;

#endif
