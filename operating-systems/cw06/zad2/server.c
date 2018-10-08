#include <sys/stat.h>
#include <sys/types.h>
#include <sys/msg.h>
#include <stdio.h>
#include <fcntl.h>
#include <unistd.h>
#include <stdlib.h>
#include <signal.h>
#include <time.h>
#include <string.h>
#include <signal.h>
#include <mqueue.h>

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

mqd_t serverQueue;
mqd_t * sendQueues;
mqd_t * recQueues;
char ** clientIDs;
int sendQueues_size = 0;

void closeQueue(char * name, mqd_t queue) {
    mq_close(queue);
	mq_unlink(name);
}

void cleanUp() {
    closeQueue("/servers", serverQueue);
    int i;
    for (i = 0; i < sendQueues_size; i++) {
        closeQueue(clientIDs[i],sendQueues[i]);
        free(clientIDs[i]);
    }

    free(sendQueues);
    free(recQueues);
    free(clientIDs);

    exit(0);
}

mqd_t getQueue(char * name){
    int i;

    for (i = 0; i < sendQueues_size; i++) {
        if (!strcmp(name, clientIDs[i]))
            return sendQueues[i];
    }

    return -1;
}

int main(int argc, char ** argv) {

    sendQueues = malloc(10 * sizeof(mqd_t));
    recQueues  = malloc(10 * sizeof(mqd_t));
    clientIDs  = malloc(10 * sizeof(char*));

    serverQueue = createQueue("/servers", sizeof(char) * 128);
    if (serverQueue < 0) {
        printf("failed: createQueue.\n");
        return 2;
    }
    printf("queue (id: %d) created\n", serverQueue);

    //atexit(cleanUp);
    signal(SIGINT, cleanUp);

    int rec_Status;
    char buff[128];
    char newbuff[128];
    message msg;

    while (1) {
        sleep(1);

        rec_Status = mq_receive(serverQueue, (char*)buff, sizeof(buff), NULL);
        if (rec_Status != -1) {
            sprintf(newbuff, "/%s", buff);
            mqd_t send_id = createQueue(newbuff, sizeof(msg));
            sprintf(newbuff,"/%sr",buff);
            mqd_t rec_id = createQueue(newbuff, sizeof(msg));

            sendQueues[sendQueues_size] = send_id;
            recQueues[sendQueues_size]  = rec_id;
            clientIDs[sendQueues_size]  = malloc(sizeof(char) * strlen(buff));
            sprintf(clientIDs[sendQueues_size], "%s\n", buff);
            printf("new user %s.\n", buff);
            sendQueues_size++;
        }

        int i;
        for (i = 0; i < sendQueues_size; i++) {
            int rec_Status = mq_receive(recQueues[i], (char*)(&msg), sizeof(msg), NULL);
            if (rec_Status >= 0) {
                int to = getQueue(msg.to);
                if (to != -1) {
                    sprintf(msg.to, "%s", clientIDs[i]);
                } else {
                    sprintf(msg.content, "error: no such queue \n");
                    to = sendQueues[i];
                }

                rec_Status = mq_send(to, (char*)(&msg), sizeof(msg) , 0);

                if (rec_Status < 0) {
                    perror(NULL);
                    printf("error: no such user\n");
                    return 1;
                }
            }
        }
    }

    return 0;
}
