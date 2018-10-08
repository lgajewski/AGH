#define _GNU_SOURCE  
#include <sched.h>
#include <unistd.h>
#include <sys/types.h>
#include <errno.h>
#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <sys/mman.h>
#include <sys/times.h>
#include <sys/wait.h>
#include <sys/types.h>
#include <sys/resource.h>
#include <sys/time.h>
#include <sys/times.h>
#include <sched.h>

#define STACK_SIZE 16384
#define N 10000

// 1 - fork
// 2 - vfork
// 3 - clone_fork
// 4 - clone_vfork 
int version = 1;


// clocks
clock_t st_time;
clock_t en_time;
struct tms st_cpu;
struct tms en_cpu;

void start_clock() {
    st_time = times(&st_cpu);
}

void end_clock(double result[]) {
    long clk = 0;
    clk = sysconf(_SC_CLK_TCK);
    en_time = times(&en_cpu);
    result[0] = (en_time - st_time) / (double)clk;
    result[1] = (en_cpu.tms_utime - st_cpu.tms_utime) / (double)clk;
    result[2] = (en_cpu.tms_stime - st_cpu.tms_stime) / (double)clk;
}

// global shared variables
/*int* counter;
double* clock_real;
double* clock_user;
double* clock_sys;*/

int counter=0;
double clock_real = 0;
double clock_user = 0;
double clock_sys = 0;

// global vars
double clock_child[3] = {0};

void proc_create_fork() {

    int i;
    for (i = 0; i < N; i++) {
        pid_t pid = fork();
        if (pid < 0) {
            perror("Fork Failed!");
            exit(1);
        } else if (pid == 0) {
            // child process

            start_clock();
            (counter)++;
            end_clock(clock_child);

            // update shared variable
            (clock_real) += clock_child[0];
            (clock_user) += clock_child[1];
            (clock_sys)  += clock_child[2];

            _exit(0);
        } else {
            // parent process

            // wait until child exits, NULL - no status info
            wait(NULL);
        }
    }
}

void proc_create_vfork() {

    int i;
    for (i = 0; i < N; i++) {
        pid_t pid = vfork();
        if (pid < 0) {
            perror("Fork Failed!");
            exit(1);
        } else if (pid == 0) {
            // child process

            start_clock();
            (counter)++;
            end_clock(clock_child);

            // update shared variable
            (clock_real) += clock_child[0];
            (clock_user) += clock_child[1];
            (clock_sys)  += clock_child[2];
            _exit(0);
        } else {
            // parent process

            // wait until child exits, NULL - no status info
            wait(NULL);
        }
    }
}

int proc_function(void* arg) {
    start_clock();
    counter++;
    end_clock(clock_child);

    // update shared variable
    (clock_real) += clock_child[0];
    (clock_user) += clock_child[1];
    (clock_sys)  += clock_child[2];
    _exit(0);
}

void proc_create_clone_fork() {

    int i;
    for (i = 0; i < N; i++) {
        void* child_stack = malloc(STACK_SIZE);
        child_stack += STACK_SIZE;
        clone(&proc_function, child_stack, SIGCHLD, NULL);
        wait(NULL);
    }
}

void proc_create_clone_vfork() {

    int i;
    for (i = 0; i < N; i++) {
        void* child_stack = malloc(STACK_SIZE);
        child_stack += STACK_SIZE;
        clone(&proc_function, child_stack, SIGCHLD | CLONE_VM | CLONE_VFORK, NULL);
        wait(NULL);
    }
}

int main(void) {

    // shared global variable
    // counter = mmap(NULL, sizeof(int), PROT_READ | PROT_WRITE, MAP_SHARED | MAP_ANONYMOUS, -1, 0);
    // *counter = 0;

    // setup global clock
    // clock_sys  = mmap(NULL, sizeof(double), PROT_READ | PROT_WRITE, MAP_SHARED | MAP_ANONYMOUS, -1, 0);
    // clock_user = mmap(NULL, sizeof(double), PROT_READ | PROT_WRITE, MAP_SHARED | MAP_ANONYMOUS, -1, 0);
    // clock_real = mmap(NULL, sizeof(double), PROT_READ | PROT_WRITE, MAP_SHARED | MAP_ANONYMOUS, -1, 0);

    // parent clock
    double* clock_parent = (double*) calloc(3, sizeof(double));

    // start_clock();
    clock_t st_time_parent;
    clock_t en_time_parent;
    struct tms st_cpu_parent;
    struct tms en_cpu_parent;

    st_time_parent = times(&st_cpu_parent);

    // start task
    switch(version) {
        case 1:
            printf("\n[FORK VERISON]\n");
            proc_create_fork();
            break;
        case 2:
            printf("\n[VFORK VERISON]\n");
            proc_create_vfork();
            break;
        case 3:
            printf("\n[CLONE_FORK VERISON]\n");
            proc_create_clone_fork();
            break;
        case 4:
            printf("\n[CLONE_VFORK VERISON]\n");
            proc_create_clone_vfork();
            break;
        default:
            printf("Version should be [1-4]\n");
            return -1;
    }

    // end_clock(clock_parent); 
    long clk_parent = 0;
    clk_parent = sysconf(_SC_CLK_TCK);
    en_time_parent = times(&en_cpu_parent);
    clock_parent[0] = (en_time_parent - st_time_parent) / (double)clk_parent;
    clock_parent[1] = (en_cpu_parent.tms_utime - st_cpu_parent.tms_utime) / (double)clk_parent;
    clock_parent[2] = (en_cpu_parent.tms_stime - st_cpu_parent.tms_stime) / (double)clk_parent;


    // result


    printf("\nPARENT:\n\tReal Time: %4.2f\n\tUser Time %4.2f\n\tSystem Time %4.2f\n", clock_parent[0], clock_parent[1], clock_parent[2]);

    printf("\nCHILDREN:\n\tReal Time: %4.2f\n\tUser Time %4.2f\n\tSystem Time %4.2f\n", clock_real, clock_user, clock_sys);

    printf("\nCOUNTER: %d\n\n", counter);

    // munmap(counter, sizeof(int));

    return 0;
}