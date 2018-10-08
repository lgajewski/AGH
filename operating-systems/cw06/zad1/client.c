#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <string.h>
#include <sys/types.h>
#include <sys/ipc.h>
#include <sys/msg.h>
#include <unistd.h>
#include <signal.h>
#include <time.h>

struct MsgBuffer {
    long mtype;
    time_t rawtime;
    char mtext[256];
};

struct Name {
	long mtype;
	char name[50];
	int id;
};

struct NameBuffer {
    long mtype;
    int length;
    int max_size;
    struct Name names[100];
};

int main(int argc, char *argv[]) {

	if(argc != 2) {
		printf("usage: ./client [name]\n");
		exit(0);
	}

	struct NameBuffer buf_name;
    int msqid;
    key_t key;

    if ((key = ftok("server.c", 0)) == -1) {
        perror("ftok");
        exit(1);
    }

    if ((msqid = msgget(key, 0644)) == -1) {
        perror("msgget");
        exit(1);
    }
    
    // send message to start queue
    struct Name name;
    name.mtype = 2;
    strcpy(name.name, argv[1]);
    name.id = 0;

    if (msgsnd(msqid, &name, sizeof(struct Name) - sizeof(long), 0) == -1)
        perror("msgsnd");

    // receive list of queues
    if (msgrcv(msqid, &buf_name, sizeof(struct NameBuffer) - sizeof(long), 1, 0) == -1) {
        perror("msgrcv");
        exit(1);
    }

    int i;
    int id;
    for(i=0; i<buf_name.length; i++) {
        if(strcmp(buf_name.names[i].name, argv[1]) == 0) {
            id = buf_name.names[i].id;
        }
    }


    int msqid2;
    key_t key2;

    // create new queue
    if ((key2 = ftok("server.c", id)) == -1) { 
        perror("ftok");
        exit(1);
    }

    if ((msqid2 = msgget(key2, 0644)) == -1) {
        perror("msgget");
        exit(1);
    }

    int pid_no = fork();

    if(pid_no == 0) {
        struct tm * timeinfo;
        struct MsgBuffer buf;
        while(1) {
            if (msgrcv(msqid2, &buf, sizeof(struct MsgBuffer) - sizeof(long), 0, 0) == -1) {
                perror("msgrcv");
                exit(1);
            }
            timeinfo = localtime ( &buf.rawtime );
            char *foo = asctime(timeinfo);
            foo[strlen(foo) - 1] = 0;
            printf("[%s] %s\n", foo, buf.mtext); 

            memset(buf.mtext, 0, sizeof(buf.mtext));
        }

    } else if (pid_no > 0) {
        struct MsgBuffer buf;
        while(fgets(buf.mtext, sizeof(buf.mtext), stdin) != NULL) {
            buf.mtype = 1;
            int len = strlen(buf.mtext);

            /* ditch newline at end, if it exists */
            if (buf.mtext[len-1] == '\n') buf.mtext[len-1] = '\0';

            // save time
            time (&buf.rawtime);

            if (msgsnd(msqid2, &buf, sizeof(struct MsgBuffer) - sizeof(long), 0) == -1)
                perror("msgsnd");

            if(strcmp(buf.mtext, "exit") == 0) {
                printf("\nexitting..\n");
                kill(pid_no, SIGKILL);
                exit(0);
            }


            memset(buf.mtext, 0, sizeof(buf.mtext));
        }        
    }

    return 0;
}