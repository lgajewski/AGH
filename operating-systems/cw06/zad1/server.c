#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <sys/types.h>
#include <sys/ipc.h>
#include <sys/msg.h>
#include <string.h>
#include <unistd.h>

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

struct NameBuffer create(int max_size) {
    struct NameBuffer name_buf;
    name_buf.mtype = 1;
    name_buf.length = 0;
    name_buf.max_size = max_size;
    return name_buf;
}

void add(struct NameBuffer *buf, struct Name name) {
    if(buf->length < buf->max_size) {
        int i;
        for(i=0; i<buf->length; i++) {
            if(strcmp(buf->names[i].name, name.name) == 0) return;
        }
        name.id = buf->length + 1;
        buf->names[buf->length++] = name;
    } else {
        // extend array
        struct NameBuffer new_buf = create(2 * buf->max_size);
        int i, j;
        j=0; // last busy index
        for(i=0; i<buf->length; i++) {
            if(buf->names[i].mtype == 2) {
                // empty element
                new_buf.names[j++] = buf->names[i];
            }
        }
        new_buf.names[j] = name;
        new_buf.length = j + 1;

        // CHANGE POINTERS
        *buf = new_buf;

    }
}

int getV(struct NameBuffer buf, char name[50]) {
    int i;
    for(i=0; i<buf.length; i++) {
        if(strcmp(buf.names[i].name, name) == 0) return buf.names[i].id;
    }
    return -1;
}

void delete(struct NameBuffer* buf, char name[50]) {
    int i;
    for(i=0; i<buf->length; i++) {
        if(buf->names[i].mtype == 2 && strcmp(buf->names[i].name, name) == 0) {
            buf->names[i].mtype = 0;
        }
    }
}

int main(void) {
    struct NameBuffer name_buf;
    struct Name name;
    int msqid;
    key_t key;

    if ((key = ftok("server.c", 0)) == -1) {  /* same key as kirk.c */
        perror("ftok");
        exit(1);
    }

    if ((msqid = msgget(key, 0644 | IPC_CREAT)) == -1) { /* connect to the queue */
        perror("msgget");
        exit(1);
    }
    
    printf("server: waiting for messages.\n");

    // init name_buf
    name_buf = create(10);

    while(1) {
        if (msgrcv(msqid, &name, sizeof(struct Name) - sizeof(long), 2, 0) == -1) {
            perror("msgrcv");
            exit(1);
        }

        // add queue to list
        add(&name_buf, name);

        int msqid2;
        key_t key2;

        // create new queue
        if ((key2 = ftok("server.c", getV(name_buf, name.name))) == -1) {  /* same key as kirk.c */
            perror("ftok");
            exit(1);
        }

        if ((msqid2 = msgget(key2, 0644 | IPC_CREAT)) == -1) { /* connect to the queue */
            perror("msgget");
            exit(1);
        }

        sleep(1);

        // send list of queues
        if (msgsnd(msqid, &name_buf, sizeof(struct NameBuffer) - sizeof(long), 0) == -1)
            perror("msgsnd");

    }

    if (msgctl(msqid, IPC_RMID, NULL) == -1) {
        perror("msgctl");
        exit(1);
    }

    return 0;
}