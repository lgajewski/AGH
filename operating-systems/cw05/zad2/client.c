#include <unistd.h>
#include <stdlib.h>
#include <stdio.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <string.h>
#include <errno.h>
#include <fcntl.h>
#include <time.h>

char fifo_name[80] = "/tmp/srv_";

void guts(int fid) {
    char cbuff[2048];
    int len;

    time_t rawtime;
    struct tm * timeinfo;

    while(1) {
        
        len = sprintf(cbuff, "\tpid: %d\n\t\tmsg: ", getpid());
            printf("[enter msg] ");
            fgets(&cbuff[len], sizeof(cbuff), stdin);
            time (&rawtime);
            timeinfo = localtime ( &rawtime );
            strcat(cbuff, asctime(timeinfo));
            write(fid, cbuff, strlen(cbuff));
        }
    }

    int main(int argc, char *argv[]) {

    if(argc != 2) {
        printf("usage: %s [fifo_name]\n", argv[0]);
    } else {
        strcat(fifo_name, argv[1]);

        printf("Trying to open %s\n", fifo_name);
        int fid = open(fifo_name, O_WRONLY);
        printf("Opened: %s\n", fifo_name);
        if (fid < 0) {
            printf("error: %s\n", strerror(errno));
            exit(-1);
        }

        guts(fid);
    }

    return 0;
}
