#ifndef lib_clist
#define lib_clist
#include <stdio.h>
#include <stdlib.h>

typedef struct ListNode ListNode;
typedef struct List List;

struct ListNode {
    ListNode *next;
    ListNode *prev;
    char *name;
    char *surname;
    char *birthdate;
    char *phoneno;
    char *email;
    char *address;

};

struct List {
    ListNode *first;
    ListNode *last;
};

List* list_create();

void list_add(List* list, char name[], char surname[], char birthdate[], char phoneno[], char email[], char address[]);

void list_remove_elem(List* list, ListNode *node);

ListNode* list_find(List* list, char name[], char surname[], char email[]);

List* list_sort(List *list);


void list_delete(List* list);

void list_print_first(List* list);
void list_print_last(List* list);

#endif
