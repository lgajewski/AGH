#include <sys/types.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <signal.h>
#include <wait.h>

int signalNo = 0;
int counter = 0;

int confirm = 1;

// function headers
void h1(int x);
void h2(int x);
int validate(int argc, char *argv[]);
void input_error(char const* msg);


int main(int argc, char * argv[]) {
    signal(SIGUSR1, h1);
    signal(SIGUSR2, h2);

    if(validate(argc, argv)) {
        signalNo = atoi(argv[1]);
       
        int i = 1;
        int pid_no = fork();

        if(pid_no < 0) {
            printf("error : fork\n");
        } else if (pid_no == 0) {
            // child
            char *env[] = { NULL};
            char *args[] = { NULL };
            if(execve("receiver", args, env) < 0) {
                input_error("cannot start receiver program");
            }
        } else {
            // parent
            while (i <= signalNo) {
                sleep(1);
                if(confirm) {
                    confirm = 0;
                    kill(pid_no, SIGUSR1);
                    printf("[s] sent SIGUSR1 #%d to process: %d\n", i, pid_no);
                    i++;
                }
                sleep(1);
            }
            sleep(1);
            kill(pid_no, SIGUSR2);
            printf("[s] sent SIGUSR2 to process %d\n", pid_no);

            // loop
            while(1){}

        }
    }

    return 0;
}

void h1(int x){
    printf("[s] received SIGUSR1 signal\n");
    counter++;
    confirm = 1;
}

void h2(int x){
    int stat = 0;
    wait(&stat);
    printf("[s] received %d signals / sent: %d\n", counter, signalNo);
    exit(0);
}

int validate(int argc, char *argv[]) {
    if(argc == 2) {
        int i = atoi(argv[1]);
        if(i < 1) {
            printf("\nPlease enter a validate value.\n");
            return -1;
        }
    } else {
        printf("usage: %s [signalNo]\n", argv[0]);
        return -1;
    }
    return 1;
}

void input_error(char const* msg) {
    printf("error: ");
    printf("%s", msg);
    printf("\n");
    exit(-1);
}
