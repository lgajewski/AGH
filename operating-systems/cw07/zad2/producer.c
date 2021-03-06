#include <stdio.h>
#include <stdlib.h>
#include <signal.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/wait.h>
#include <sys/ipc.h>
#include <sys/sem.h>
#include <sys/shm.h>
#include <sys/mman.h>
#include <sys/time.h>
#include <fcntl.h>
#include <unistd.h>
#include <limits.h>
#include <time.h>
#include <string.h>
#include <errno.h>
#include <semaphore.h>

#include "commons.h"

sem_t* s_empty;
sem_t* s_full;
sem_t* s_access;

int shared;
void* memory;

int get_random_int(int START, int END) {
    return (END + rand() / (RAND_MAX / (START - END + 1) + 1));
}

void sig_segv(int i) {
	exit(-1);
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

sem_t* create_sem(char* path, int* init, int value){
	sem_t* sem;
	if((sem = sem_open(path, O_CREAT | O_EXCL, S_IRWXU, value)) == SEM_FAILED) {
		err(errno != EEXIST) init = 0;
        err((sem = sem_open(path,  0)) == SEM_FAILED)
	}
	return sem;
}

void clean(int id) {
	err(munmap(memory, MEM_SIZE) == -1);
	err(shm_unlink("/shared") == -1);
	err(sem_close(s_empty) == -1);
	err(sem_close(s_full) == -1);
	err(sem_close(s_access) == -1);
	err(sem_unlink("/s_empty") == -1);
	err(sem_unlink("/s_full") == -1);
	err(sem_unlink("/s_access") == -1);
}

void produce(int init) {
	srand(time(NULL));

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
		// acquire semaphores
		err_and_clean(sem_wait(s_empty) == -1);
		err_and_clean(sem_wait(s_access) == -1);

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
		printf("(%d %s) Dodalem liczbe: %2d. Liczba zadan oczekujacych: %d\n", 
				getpid(), tmbuf, val, *actual_size);

		// release semaphores
		err_and_clean(sem_post(s_access) == -1);
		err_and_clean(sem_post(s_full) == -1);

	}
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

    // open file, get proper file descriptor
	err((des = open(SERV_PATH, O_WRONLY | O_CREAT, S_IRUSR | S_IWUSR)) == -1)
	err(close(des) == -1)
	err((key = ftok(SERV_PATH, FTOK_ID)) == -1)

    // create semaphores and initialize them
	s_empty = create_sem("/s_empty", &init, BUFF_SIZE);
	s_full = create_sem("/s_full", &init, 0);
	s_access = create_sem("/s_access", &init, 1);

    // initialize shared memory
	if((shared = shm_open("/shared", O_RDWR | O_CREAT | O_EXCL, S_IRWXU)) == -1) {
		err(errno != EEXIST)
		init = 0;
		err((shared = shm_open("/shared", O_RDWR, 0)) == -1)
	}

	// shared memory sized with ftruncate()
	err(ftruncate(shared, MEM_SIZE) == -1)

	// map into the process address
	err((memory = mmap(NULL, MEM_SIZE, PROT_READ | PROT_WRITE, MAP_SHARED, shared, 0)) == NULL)

	produce(init);

	clean(0);
	return 0;
}
