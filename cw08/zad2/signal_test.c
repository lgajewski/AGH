#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <string.h>
#include <pthread.h>
#include <signal.h>
#include <unistd.h>
#include "commons.h"

#define VERSION 1		// 1,2,3,4,5

// 1 wysłanie sygnału do procesu, gdy żaden wątek nie ma zamaskowanego tego sygnału
// 2 wysłanie sygnału do procesu, gdy główny wątek programu ma zamaskowany ten sygnał, a wszystkie pozostałe wątki nie,
// 3 wysłanie sygnału do procesu, gdy wszystkie wątki mają zainstalowaną niestandardową procedurę obsługi przerwania, która wypisuje informację o nadejściu tego sygnału oraz PID i TID danego wątku
// 4 wysłanie sygnału do wątku z zamaskowanym tym sygnałem
// 5 wysłanie sygnału do wątku, w którym zmieniona jest procedura obsługi sygnału, jak przedstawiono w punkcie 3

#define SIGNAL SIGUSR1

void sig_handler(int signo) {
	printf("PID = %d TID = %d\n", getpid(), (int)pthread_self());
}

void * thread_run(void * args) {
	if(VERSION == 3) signal(SIGNAL, sig_handler);
	if(VERSION == 4) {
		sigset_t set;
		sigemptyset(&set);
		sigaddset(&set, SIGNAL);
		err(sigprocmask(SIG_SETMASK, &set, NULL) < 0)
	    printf("sigprocmask %d thread_run\n", SIGNAL);
	}

	while (1) {}

	return NULL;
}

int main(int argc, char ** argv) {
	if(VERSION == 2) {
		sigset_t set;
		sigemptyset(&set);
		sigaddset(&set, SIGNAL);
		err(sigprocmask(SIG_SETMASK, &set, NULL) < 0) 
		printf("sigprocmask %d main\n", SIGNAL);
	}

	pthread_t thread;
	err(pthread_create(&thread, NULL, &thread_run, (void*) NULL) < 0)
	printf("pthread_create\n");

	sleep(1);

	if(VERSION == 4 || VERSION == 5) {
	err(pthread_kill(thread, SIGNAL) < 0) 
	printf("Send signal to the thread_run.\n");
	} else {
		if(VERSION == 3) signal(SIGNAL, sig_handler);
		err(raise(SIGNAL) < 0)
	    printf("Send signal to the main\n");
	}

	err(pthread_join(thread, NULL) < 0)
    printf("end of program\n");

	return 0;
}