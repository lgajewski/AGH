#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <errno.h>
#include <string.h>
#include <getopt.h>
#include <signal.h>
#include <pthread.h>
#include "commons.h"

#define VERSION 2   // 1-a, 2-b, 3-c


typedef struct thread_params {
   pthread_t* threads;
   int threads_no;
   int records_no;
   int fd;
   char* word;
} thread_params;

pthread_mutex_t mutex;

int records_processed = 0;

void* run(void* args) {
   thread_params* params = (thread_params*) args;
   char buf[RECORD_SIZE];

   // pthread attributes
   int tmp=0;
   switch(VERSION) {
      case 1:
         pthread_setcancelstate(PTHREAD_CANCEL_ENABLE, &tmp);
         pthread_setcanceltype(PTHREAD_CANCEL_ASYNCHRONOUS, &tmp);
         break;
      case 2:
         pthread_setcancelstate(PTHREAD_CANCEL_ENABLE, &tmp);
         pthread_setcanceltype(PTHREAD_CANCEL_DEFERRED, &tmp);
         break;
      case 3:
         pthread_setcancelstate(PTHREAD_CANCEL_DISABLE, &tmp);
         break;
   }

   int keep_reading = 1;
   char* word_ptr;

   while(keep_reading) {
      int read_r = 0;
      pthread_mutex_lock(&mutex);
      if(records_processed < params->records_no) {
         if((read_r = read(params->fd, buf, RECORD_SIZE)) < 0) {
            pthread_mutex_unlock(&mutex);
            printf("%s\n", strerror(errno));
            exit(-1);
         }
         // end of file
         if(read_r == 0) {
            keep_reading = 0;
         }

         records_processed++;
      } else {
         // all lines processed
         keep_reading = 0;
      }
      // unlock mutex
      pthread_mutex_unlock(&mutex);

      if(VERSION == 2) pthread_testcancel(); // set cancel point, after unlocking mutex

      sleep(1);

      // TODO
      if(read_r > 0) {
         // process line
         char* delimiter = strchr(buf, ',');
         char idS[delimiter - buf + 1];
         strncpy(idS, buf, delimiter - buf);
         idS[delimiter - buf] = '\0';

         printf("(%d) line: %s\n", (int)pthread_self(), idS);

         word_ptr = strstr(buf, params->word);
         if(word_ptr != NULL) {
            // buffer contains word
            keep_reading = 0;

            // output
            printf("(%d) found '%s', id: %s\n", (int)pthread_self(), params->word, idS);

            // interrupt program when A,B version, join threads
            if(VERSION == 1 || VERSION == 2) {
               int i;
               for(i=0; i<params->threads_no; i++) {
                  if(pthread_self() != params->threads[i]) {
                     pthread_cancel(params->threads[i]);
                  }
               }
            }
         }
      }
   }

   if(word_ptr == NULL) {
      printf("(%d) no occurences of '%s'\n", (int)pthread_self(), params->word);
   }


   return NULL;
}

int main(int argc, char* argv[]) {
   if(argc != 5) {
      printf("usage: %s [threads_no] [file_name] [records_no] [word_to_search]\n", argv[0]);
      return 0;
   }

   // allocate array with threads
   int threads_no = atoi(argv[1]);
   pthread_t* threads = (pthread_t*) malloc(sizeof(threads_no*sizeof(pthread_t)));

   // open file
   int fd = open(argv[2], O_RDONLY);
   err(fd < 0);


   thread_params* params = (thread_params*)malloc(sizeof(thread_params));
   params->threads = threads;
   params->threads_no = threads_no;
   params->fd = fd;
   params->records_no = atoi(argv[3]);
   params->word = argv[4];

   err(pthread_mutex_init(&mutex, NULL) < 0)

   // create threads
   int i;
   for(i=0; i<threads_no; i++) {
      err(pthread_create(&threads[i], NULL, &run, (void*)params) < 0)
      printf("Created thread id: %d\n", (int)threads[i]);
   }

   for(i=0; i<threads_no; i++) {
      err(pthread_join(threads[i], NULL) < 0)
   }

   // close file
   err(close(fd) < 0)

   // free mutex
   err(pthread_mutex_destroy(&mutex) < 0)


   return 0;
}