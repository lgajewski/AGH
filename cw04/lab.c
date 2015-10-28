#include <stdlib.h>
#include <stdio.h>
#include <signal.h>
#include <unistd.h>
#define TRUE 1
#define FALSE 0

int up = TRUE;
void sig_handler(int signo);
void handle_signal(int signal);
 
int main() {
    struct sigaction sa;
 
    // Setup the sighub handler
    sa.sa_handler = &handle_signal;

    // sigaction
    sigaction(SIGTSTP, &sa, NULL);
 
    // signal
    if (signal(SIGINT, sig_handler) == SIG_ERR)
        printf("\nCan't catch SIGINT\n");

 	int i = 1;
    while(TRUE) {
    	printf("i: %d\n", i);
    	if(up) {
			if(i >= 9) i = 0;
			i++;
    	} else {
    		if(i <= 1) i = 10;
    		i--;
    	}
    	sleep(1);
    } 
}

void sig_handler(int signo) {
    if (signo == SIGINT) {
        printf(" [signal] received SIGINT\n");
    	up = (up) ? FALSE : TRUE;
    }
}

 
void handle_signal(int signal) {

    // Find out which signal we're handling
    switch (signal) {
        case SIGTSTP:
            printf("\nCaught SIGTSTP, exiting now\n");
            exit(0);
        default:
            fprintf(stderr, "Caught wrong signal: %d\n", signal);
            return;
    }
 
    sleep(1);
}