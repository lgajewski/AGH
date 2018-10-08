#include <sys/types.h>
#include <sys/msg.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <signal.h>
#include <stdio.h>
#include <time.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <sys/times.h>
#include <mqueue.h>

int pid;

typedef struct message {
    time_t time;
    char to[128];
    char content[256];
} message;

mqd_t createQueue(char * name, size_t size){
    struct mq_attr attr;

    attr.mq_maxmsg = 10;
    attr.mq_msgsize = size;
    attr.mq_flags = 0;
    mqd_t queue = mq_open (name, O_RDWR | O_CREAT |O_NONBLOCK, 0664, &attr);
    if(queue < 0)
        perror(NULL);

    return queue;
}

void clean(){
    kill(pid,SIGKILL);
    exit(0);
}

int main(int argc, char * argv[]){
    if (argc < 2) {
        printf("usage: ./client [id]\n");
        return 1;
    }

    struct mq_attr attr;
    attr.mq_maxmsg = 10;
    attr.mq_msgsize = sizeof(char)*128;
    attr.mq_flags = 0;
    mqd_t clientQueue = mq_open ("/servers", O_WRONLY, 0664, &attr);
    if (clientQueue < 0) {
        printf("Could not open server connection.\n");
        return 2;
    }

    printf("Estabilished connection with server (id: %d).\n", clientQueue);
    char * name = argv[1];

    int rc = mq_send(clientQueue, name, sizeof(char) * 128, 0);
    sleep(1);

    if (rc == -1) {
        printf("Could not log onto a server %s. Try again later.\n", "/servers");
        return 1;
    }

    message msg;

    char buff[128];
    sprintf(buff, "/%s", name);
    mqd_t recQueue = createQueue(buff, sizeof(msg));
    sprintf(buff, "/%sr", name);
    mqd_t sendQueue = createQueue(buff, sizeof(msg));

    pid = fork();
    signal(SIGINT, clean);

    if (pid > 0) {
        while (1) {
            printf("Recipient: ");
            fgets(msg.to, sizeof(msg.to), stdin);
            printf("Msg: ");
            fgets(msg.content, sizeof(msg.content), stdin);
            msg.time = times(NULL);
            int rc = mq_send(sendQueue, (char*)(&msg), sizeof(msg) , 0);

            if (rc == -1) {
                printf("mq_send: error\n");
            }
        }
    }

    while (1) {
        sleep(1);
        rc = mq_receive(recQueue, (char*)(&msg), sizeof(msg), NULL);

        if (rc != -1) {
            printf("[%s] '%s': %s", ctime(&msg.time), msg.to, msg.content);
        }
    }

    return 0;
}