#include <pthread.h>
#include <string.h>
#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <errno.h>
#include <signal.h>
#include "commons.h"

void * thread_run(void * args) {
	while (1) {
		printf("running..");
	}
	return NULL;
}

void * thread_run_divbyzero(void * args) {
    printf ("divbyzero = %d\n", 1/0);
    return NULL;
}

int main(int argc, char ** argv) {
	pthread_t thread;
	err(pthread_create(&thread, NULL, &thread_run, (void*) NULL) < 0)
	printf("1\n");

	pthread_t thread_bug;
	err(pthread_create(&thread_bug, NULL, &thread_run_divbyzero, (void*) NULL) < 0)
	printf("2\n");

	sleep(1);

	err(pthread_join(thread, NULL) < 0)
	printf("3\n");
    err(pthread_join(thread_bug, NULL) < 0)
    printf("4\n");

	return 0;
}