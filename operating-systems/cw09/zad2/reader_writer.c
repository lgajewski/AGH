#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <semaphore.h>
#include <string.h>
#include <errno.h>
#include <time.h>
#include <unistd.h>

#define err_and_clean(X) if(X){printf("%s\n", strerror(errno));clean(0);exit(-1);}
#define err(X) if(X){printf("%s\n", strerror(errno));exit(-1);}

#define BUFFER_LENGTH 10
#define MAX_VAL 10

int buffer[BUFFER_LENGTH] = {0};

int readers = 0;
sem_t s_turnstile;
sem_t s_access;
sem_t s_counter;

struct writer_attr {
	int id;
	int value;
} writer_attr;

struct reader_attr {
	int id;
	int value;
} reader_attr;

struct writer_attr* writer_attrs;
struct reader_attr* reader_attrs;

void * reader_thread(void * args) {
	struct reader_attr attr = *((struct reader_attr*)args);
	sem_wait(&s_turnstile);
	sem_wait(&s_counter);
	readers++;
	if(readers == 1) {
		sem_wait(&s_access);
	}
	sem_post(&s_turnstile);
	sem_post(&s_counter);

	// read resource
	int i,count=0;
	for(i=0; i<BUFFER_LENGTH; i++) {
		if(buffer[i] == attr.value) {
			count++;
			printf("[reader #%d] found %d at %d position\n", attr.id, attr.value, i);
		}
	}
	printf("[reader #%d] found %d items with value %d\n\n", attr.id, count, attr.value);

	sleep(1);

	sem_wait(&s_counter);
	readers--;
	if(readers == 0) {
		sem_post(&s_access);
	}
	sem_post(&s_counter);

	return NULL;

}


void * writer_thread(void * args) {
	int id = *((int*) args);
	sem_wait(&s_turnstile);
	sem_wait(&s_access);
	sem_post(&s_turnstile);

	// write to buffer
	int i, position, value;
	int numbers_to_edit = rand() % BUFFER_LENGTH;

	for(i=0; i<numbers_to_edit; i++) {
		position = rand() % BUFFER_LENGTH;
		value = rand() % MAX_VAL;
		buffer[position] = value;
		printf("[writer #%d] buffer[%d] = %d\n", id, position, value);
	}
	printf("\n");

	sleep(1);

	sem_post(&s_access);

	return NULL;
}

int main(int argc, char* argv[]) {

	// create semaphores and initialize them
    sem_init(&s_turnstile, 0, 1);
    sem_init(&s_access, 0, 1);
    sem_init(&s_counter, 0, 1);

	if(argc < 3) {
		printf("usage : %s [readers] [writers]\n", argv[0]);
		exit(1);
	}
	
	int nreaders = atoi(argv[1]);
	int nwriters = atoi(argv[2]);
	
	pthread_t * writers = (pthread_t *) malloc(sizeof(pthread_t) * nwriters);
	pthread_t * readers = (pthread_t *) malloc(sizeof(pthread_t) * nreaders);

	reader_attrs = (struct reader_attr*) malloc(sizeof(reader_attr) * nreaders);
	writer_attrs = (struct writer_attr*) malloc(sizeof(writer_attr) * nwriters);

	int i;
	for(i = 0 ; i < nwriters ; i++) {
		writer_attrs[i].id = i;
		err(pthread_create(&writers[i], NULL, &writer_thread, &writer_attrs[i]) != 0)
	}
			
	for(i = 0 ; i < nreaders ; i++) {
		reader_attrs[i].id = i;
		reader_attrs[i].value = rand() % MAX_VAL;
		err(pthread_create(&readers[i], NULL, &reader_thread, &reader_attrs[i]) != 0)
	}
			
	for(i = 0 ; i < nwriters ; i++)
		err(pthread_join(writers[i], NULL) != 0)
			
	for(i = 0 ; i < nreaders ; i++)
		err(pthread_join(readers[i], NULL) != 0)

    sem_destroy(&s_turnstile);
    sem_destroy(&s_access);
    sem_destroy(&s_counter);


	return 0;
}