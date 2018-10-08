#include <errno.h>
#include <pthread.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <string.h>
#include <unistd.h>
#include <semaphore.h>
#include <fcntl.h>
#include <stdio.h>
#include <stdlib.h>

#define RAND_SLEEP_TIME 10000
#define RAND_EATING_TIME 1000

#define PHILOSOPHERS_NO 5

int PRINT_VER = 0;
time_t last_printed = 0;

int philosopher_id[PHILOSOPHERS_NO];
pthread_mutex_t mutex;
pthread_cond_t waiter;
pthread_cond_t forks[PHILOSOPHERS_NO];
pthread_t philosopher_threads[PHILOSOPHERS_NO];

int phil_is_eating[PHILOSOPHERS_NO];
int fork_holder[PHILOSOPHERS_NO];

void printv2(){
	int i;
	for (i = 0; i < PHILOSOPHERS_NO; i++) {
		if (phil_is_eating[i]) {
			int leftFork = i;
			int rightFork = (i+1)%PHILOSOPHERS_NO;
			printf("[philosopher #%d] is eating with forks: %d, %d\n", i, leftFork, rightFork);
		}
	}
	printf("\n");
}

void printv1() {
	time_t now = time(NULL);
	if(now - last_printed < 1) {
		return;
	} else {
		system("clear");
		last_printed = time(NULL);
	}

	printf("\n\n\n");
	char* table[35];
	table[0] = "              ,iiiiiii        4                   ";
	table[1] = "              iii#i#iii      44                   ";
	table[2] = "             iiiiiiiiiii     4    iiiii           ";
	table[3] = "             iiiiiiiiiii    4   ;ii#iEiii         ";
	table[4] = "             iiiiiiiiiii    4   iii#iEiii         ";
	table[5] = "             iiiiiiiiiii   4   iiiiiiiiiii        ";
	table[6] = "             iiKiiiiiKi   44   iiiiiiiiiii        ";
	table[7] = "              iiiiiiiii   44   iiiiiiiiiii        ";
	table[8] = "     555       iiiiiii   4     iKiiiiiiiii        ";
	table[9] = "       55                       iiKKjLKDi         ";
	table[10] ="         5555                  .iiiiiiii          ";
	table[11] ="	   555                   iiiii.               ";
	table[12] ="               555                                ";
	table[13] ="       iiiii                    33333             ";
	table[14] ="     ;iifi#ii;                     3333333        ";
	table[15] ="    .iiiiitiii                          333333    ";
	table[16] ="    iiiiiiiiiii                      ;:           ";
	table[17] ="    iiiiiiiiiii                   .iiiiii         ";
	table[18] ="    iiiiiiiiiii                  ,ii#iLiii        ";
	table[19] ="    iKiiiiiiiii                  iiiiiiiiii       ";
	table[20] ="     iiKKLLKGi                  iiiiiiiiiii       ";
	table[21] ="     .iiiiiii.                  iiiiiiiiiii       ";
	table[22] ="       iiiii                    iiiiiiiiiii       ";
	table[23] ="                1          2     iKiiiiiiEi       ";
	table[24] ="              11            22   iitKKKKGi.       ";
	table[25] ="             11      ,ii:    22   iiiiiiii        ";
	table[26] ="            11     iiiiiii,   22   .iiii          ";
	table[27] ="           11     iii#iGtii,   22                 ";
	table[28] ="          11      iiiiiiiiii    22                ";
	table[29] ="         11      :iiiiiiiiii     22               ";
	table[30] ="                 ;iiiiiiiiii                      ";
	table[31] ="                 :iiiiiiiiii                      ";
	table[32] ="                  iKGiiiiiKi                      ";
	table[33] ="                  ;iiiLLiii                       ";
	table[34] ="                   ,iiiiii                        ";


	int forkArr[] = {0,0,0,0,0};
	int count = 0;
	int j=0;
	for(j=0; j<PHILOSOPHERS_NO; j++) {
		if(phil_is_eating[j]) {
			int leftFork = j;
			int rightFork = (j+1) % PHILOSOPHERS_NO;
			forkArr[leftFork] = leftFork+1;
			forkArr[rightFork] = rightFork+1;
			count++;
		}
	}

	int i,k;
	for(i=0; i<35; i++) {
		for(j=0; j<50; j++) {
			char c = table[i][j];
			int cInt = c - '0';
			if(cInt >= 1 && cInt <= 5) {
				int printed = 0;
				for(k=0; k<PHILOSOPHERS_NO; k++) {
					if(forkArr[k] == cInt) {
						printed = 1;
						printf("%d", cInt); 
					}
				}
				if(!printed) printf(" ");
			} else {
				printf("%c", c);
			}
		}
		printf("\n");

	}  
}

int busy_philosophers(int tab[]) {
	int i, count = 0;
	int phil[PHILOSOPHERS_NO] = {0};
	for(i=0; i<PHILOSOPHERS_NO; i++) {
		if(tab[i] >= 0) {
			phil[tab[i]]++;
		}
	}
	for(i=0; i<PHILOSOPHERS_NO; i++) {
		if(phil[i] > 0) count++;
	}
	return count;
}

void* run(void * arg) {
	int id = *((int*) arg);

	int leftFork = id;
	int rightFork = (id + 1) % PHILOSOPHERS_NO;

	while (1) {
		// sleeping time
		usleep(rand() % RAND_SLEEP_TIME);

		// sem_wait(&freeForksSemaphore);
		pthread_mutex_lock(&mutex);
		while(busy_philosophers(fork_holder) == PHILOSOPHERS_NO - 1) pthread_cond_wait(&waiter, &mutex);
		pthread_mutex_unlock(&mutex);


		// pick up forks
		pthread_mutex_lock(&mutex);
		while(fork_holder[leftFork] >= 0) pthread_cond_wait(&forks[leftFork], &mutex);
		fork_holder[leftFork] = id;
		pthread_mutex_unlock(&mutex);

		pthread_mutex_lock(&mutex);
		while(fork_holder[rightFork] >= 0) pthread_cond_wait(&forks[rightFork], &mutex);
		fork_holder[rightFork] = id;
		pthread_mutex_unlock(&mutex);

        phil_is_eating[id] = 1;

        // eating time
        usleep(rand() % RAND_EATING_TIME);

        if(PRINT_VER == 1) {
    		printv1();
        } else if (PRINT_VER == 2) {
        	printv2();
        }

        phil_is_eating[id] = 0;


		// put down forks
		pthread_mutex_lock(&mutex);
		fork_holder[leftFork] = -1;
		pthread_cond_signal(&forks[leftFork]);
		pthread_mutex_unlock(&mutex);

		pthread_mutex_lock(&mutex);
		fork_holder[rightFork] = -1;
		pthread_cond_signal(&forks[rightFork]);
		pthread_mutex_unlock(&mutex);

		pthread_mutex_lock(&mutex);
		pthread_cond_signal(&waiter);
		pthread_mutex_unlock(&mutex);

	}

	return NULL;
}

int main(int argc, char *argv[]) {
	srand(time(NULL));

	if(argc != 2) {
		printf("usage: %s [1,2]\n", argv[0]);
		return 0;
	}

	PRINT_VER = atoi(argv[1]);

	pthread_mutexattr_t attr;
	pthread_mutexattr_init(&attr);
	pthread_mutexattr_settype(&attr, PTHREAD_MUTEX_NORMAL);

	// initialize mutex and conditions
	pthread_mutex_init(&mutex, &attr);
	pthread_cond_init(&waiter, NULL);

	int i;
	for (i = 0; i < PHILOSOPHERS_NO; i++) {
		philosopher_id[i] = i;
		phil_is_eating[i] = 0;
		fork_holder[i] = -1;
        pthread_cond_init(&forks[i], NULL);
	}

	for (i = 0; i < PHILOSOPHERS_NO; i++) {
		pthread_create(&philosopher_threads[i], NULL, run, &philosopher_id[i]);
	}

	for (i = 0; i < PHILOSOPHERS_NO; i++) {
		pthread_join(philosopher_threads[i], NULL);
	}

	return 0;
}