//functions for dll library
#include <dlfcn.h>

#ifndef DLL
    #include "clist.h"
#endif

#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <sys/times.h>
#include <string.h>

// clocks
static clock_t st_time;
static clock_t en_time;
static struct tms st_cpu;
static struct tms en_cpu;

void start_clock() {
    st_time = times(&st_cpu);
}

void end_clock(double *result) {
    static long clk = 0;
    clk = sysconf(_SC_CLK_TCK);
    en_time = times(&en_cpu);
    result[0] = (en_time - st_time) / (double)clk;
    result[1] = (en_cpu.tms_utime - st_cpu.tms_utime) / (double)clk;
    result[2] = (en_cpu.tms_stime - st_cpu.tms_stime) / (double)clk;
}

void print_time_results(double *p1, double *p2, double *p3, double *p4) {
    printf("p1: Real Time: %4.2f, User Time %4.2f, System Time %4.2f\n", p1[0], p1[1], p1[2]);
    printf("p2: Real Time: %4.2f, User Time %4.2f, System Time %4.2f\n", p2[0], p2[1], p2[2]);
    printf("p3: Real Time: %4.2f, User Time %4.2f, System Time %4.2f\n", p3[0], p3[1], p3[2]);
    printf("p4: Real Time: %4.2f, User Time %4.2f, System Time %4.2f\n", p4[0], p4[1], p4[2]);
    printf("\n\n\n");
    printf("p2-p1: Real Time: %4.2f, User Time %4.2f, System Time %4.2f\n", p2[0]-p1[0], p2[1]-p1[1], p2[2]-p1[2]);
    printf("p3-p1: Real Time: %4.2f, User Time %4.2f, System Time %4.2f\n", p3[0]-p1[0], p3[1]-p1[1], p3[2]-p1[2]);
    printf("p3-p2: Real Time: %4.2f, User Time %4.2f, System Time %4.2f\n", p3[0]-p2[0], p3[1]-p2[1], p3[2]-p2[2]);
    printf("p4-p3: Real Time: %4.2f, User Time %4.2f, System Time %4.2f\n", p4[0]-p3[0], p4[1]-p3[1], p4[2]-p3[2]);
    printf("p4-p1: Real Time: %4.2f, User Time %4.2f, System Time %4.2f\n", p4[0]-p1[0], p4[1]-p1[1], p4[2]-p1[2]);

}


int main() {

    #ifdef DLL
    //open library
    //function returns library handler
    //takes dynamic library path and a flag
    void *handle = dlopen("./libclist.so", RTLD_LAZY);
    typedef struct List List;
    typedef struct ListNode ListNode;

    List* (*list_create)();
    list_create = dlsym(handle, "list_create");

    void (*list_add)(List* list, char name[], char surname[], char birthdate[], char phoneno[], char email[], char address[]);
    list_add = dlsym(handle, "list_add");

    ListNode* (*list_find)(List* list, char name[], char surname[], char email[]);
    list_find = dlsym(handle, "list_find");

    void (*list_remove_elem)(List *list, ListNode* node);
    list_remove_elem = dlsym(handle, "list_remove_elem");

    List* (*list_sort)(List* list);
    list_sort = dlsym(handle, "list_sort");

    void (*list_delete)();
    list_delete = dlsym(handle, "list_delete");

    #endif

    // punkty kontrolne
    double *p1 = (double*)malloc(3*sizeof(double));
    double *p2 = (double*)malloc(3*sizeof(double));
    double *p3 = (double*)malloc(3*sizeof(double));
    double *p4 = (double*)malloc(3*sizeof(double));

    start_clock();
    List *myList = list_create();

    int i = 0;
    for (i = 0; i<=20000; i++) {
        char *str = (char*)malloc(20*sizeof(char));
        sprintf(str, "kontakt_%d", i);
        list_add(myList, str, str, str, str, str, str);
    }
    end_clock(p1);
    start_clock();


    for(i=0; i<=20000; i+=3) {
        char *str = (char*)malloc(20*sizeof(char));
        sprintf(str, "kontakt_%d", i);
        ListNode* tmp = list_find(myList, str, str, str);
        list_remove_elem(myList, tmp);
    }

    end_clock(p2);
    start_clock();

    List* sortedList = list_sort(myList);
    end_clock(p3);

    start_clock();
    list_delete(sortedList);
    list_delete(myList);
    end_clock(p4);

    print_time_results(p1, p2, p3, p4);
    return 0;

    #ifdef DLL
    //close library after doing all stuff
    dlclose(handle);
    #endif
}
