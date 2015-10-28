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

void fifo(int fid) {
  char cbuff[5000];
  int nread;

  time_t rawtime;
    struct tm * timeinfo;

  while(1) {
    if((nread = read(fid, cbuff, sizeof(cbuff))) > 0) {
      time (&rawtime);
        timeinfo = localtime ( &rawtime );
      cbuff[nread] = 0;
      printf("[server] %s - time: %s\n", cbuff, asctime(timeinfo));  
    }
  }
}

int main(int argc, char *argv[]) {

  if(argc != 2) {
    printf("usage: %s [fifo_name]\n", argv[0]);
  } else {
    strcat(fifo_name, argv[1]);

    // remove pipe
    unlink(fifo_name);

    int rc = mkfifo(fifo_name, 0777);
    if (rc == 0) {
      printf("Fifo '%s' created\n", fifo_name);
    } else {
      printf("error: %s\n", strerror(errno));
      exit(-1);
    }   
    int fid = open(fifo_name, O_RDWR);
    if ( fid < 0) {
      printf("error: %s\n", strerror(errno));
      exit(-1);
    }

    fifo(fid);

  }

  return 0;

}
