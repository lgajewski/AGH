#include <stdlib.h>
#include <signal.h>
#include <unistd.h> 
#include <stdio.h>
#include <time.h>
#include <wait.h>

int counter = 0;

// function headers
void h1(int x);
void h2(int x);
void input_error(char const* msg);


int main() {
    signal(SIGRTMAX, h1);
    signal(SIGRTMIN, h2);

    while(1) {}

    return 0;
}

void h1(int x) {
    printf(" [r] Received signal SIGRTMAX\n");
    counter++;
}

void h2(int x) {
    printf(" [r] Received %d signals\n", counter);
    int parent_pid = getppid();
    int i=0;

    while (i < counter) {
        kill(parent_pid, SIGRTMAX);
        printf(" [r] Sent SIGRTMAX signal\n");
        sleep(1);
        i++;
    }

    kill(parent_pid, SIGRTMIN);
    printf(" [r] Sent SIGRTMIN signal\n");

    exit(counter);
}

void input_error(char const* msg) {
	printf("error: ");
	printf("%s", msg);
	printf("\n");
	exit(-1);
}

