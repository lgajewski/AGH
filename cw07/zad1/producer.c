#include <stdio.h>
#include <stdlib.h>
#include <signal.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/wait.h>
#include <sys/ipc.h>
#include <sys/sem.h>
#include <sys/shm.h>
#include <sys/time.h>
#include <fcntl.h>
#include <unistd.h>
#include <limits.h>
#include <time.h>
#include <string.h>
#include <errno.h>

#include "commons.h"

int semaphores_set;
int shared;
int buff_elems_no;
void* memory;

int get_random_int(int START, int END) {
    return (END + rand() / (RAND_MAX / (START - END + 1) + 1));
}

void get_timestamp(char* buffer) {
	char tmbuf[64];
	struct timeval tv;
	time_t nowtime;
	struct tm *nowtm;
	gettimeofday(&tv, NULL);
	nowtime = tv.tv_sec;
	nowtm = localtime(&nowtime);
	strftime(tmbuf, sizeof(tmbuf), "%H:%M:%S", nowtm);
	sprintf(buffer, "%s.%06d", tmbuf, (int)tv.tv_usec);
}

void clean(int a){
	err(shmdt(memory) == -1)
	err(shmctl(buff_elems_no, IPC_RMID, NULL) == -1)
	err(shmctl(shared, IPC_RMID, NULL) == -1)
	err(semctl(semaphores_set, IPC_RMID, 0) == -1)
}

void produce(int init) {
	srand(time(NULL));
	struct sembuf semaphore_attributes;
	semaphore_attributes.sem_flg = 0;

    // variables in shared memory
	int* actual_position = memory;
	int* actual_size = memory+sizeof(int);
    int* buffer = memory+2*sizeof(int);

    // initialize variables if non-initialized
	if(init) {
		*actual_size = 0;
		*actual_position = 0;
	}
	int counter;

	while(1) {
        // acquire semaphore
		semaphore_attributes.sem_num = S_EMPTY;
		semaphore_attributes.sem_op = -1;
		err_and_clean(semop(semaphores_set, &semaphore_attributes, 1) == -1)

        // acquire semaphore
		semaphore_attributes.sem_num = S_ACCESS;
		err_and_clean(semop(semaphores_set, &semaphore_attributes, 1) == -1)

        // update current position
		counter = *actual_position;
		(*actual_position) = (counter+1)%BUFF_SIZE;

        // generate random number
        int val = get_random_int(1,100);
		buffer[counter] = val;
		*actual_size = *actual_size + 1;

		// generate time
		char tmbuf[64];
		get_timestamp(tmbuf);
		
		// output
		printf("(%d %s) Dodalem liczbe: %2d. Liczba zadan oczekujacych: %d, index: %d\n", 
				getpid(), tmbuf, val, *actual_size, counter);

		// release semaphore
		semaphore_attributes.sem_op = 1;
		err_and_clean(semop(semaphores_set, &semaphore_attributes, 1) == -1)

        // release semaphore
		semaphore_attributes.sem_num = S_FULL;
		err_and_clean(semop(semaphores_set, &semaphore_attributes, 1) == -1)

		sleep(1);
	}
}

void sig_segv(int i) {
	exit(-1);
}

int main(int argc, char* argv[]){
	// random seed
	srand(time(NULL));

	int des;
	key_t key;
	int init = 1;
	
	// handle signals to clean up
	err(signal(SIGINT, clean) == SIG_ERR)
	err(signal(SIGSEGV, sig_segv) == SIG_ERR)

    // create file, get proper file descriptor
	err((des = open(SERV_PATH, O_WRONLY | O_CREAT, S_IRUSR | S_IWUSR)) == -1)
	err(close(des) == -1)
	err((key = ftok(SERV_PATH, FTOK_ID)) == -1)


    // create semaphores and initialize them
	if((semaphores_set = semget(key, 3, IPC_CREAT | IPC_EXCL | S_IRWXU)) == -1){
		err(errno != EEXIST)// check if semaphores exist
        init = 0;
        err((semaphores_set = semget(key, 3, 0)) == -1)
	} else {
	    // initialize semaphores with values
		err(semctl(semaphores_set, S_EMPTY, SETVAL, BUFF_SIZE) == -1)
		err(semctl(semaphores_set, S_FULL, SETVAL, 0) == -1)
		err(semctl(semaphores_set, S_ACCESS, SETVAL, 1) == -1)
	}
    
	// initialize shared memory 
	if((shared = shmget(key, MEM_SIZE, IPC_CREAT | IPC_EXCL | S_IRWXU)) == -1){
		err(errno != EEXIST)
        init = 0;
        err((shared = shmget(key, MEM_SIZE, 0)) == -1)
	}

	// map into the process address
	err((memory = shmat(shared, NULL, 0)) == NULL)

	produce(init);

	clean(0);
	return 0;
}
