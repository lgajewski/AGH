#include "clist.h"
#include <stdlib.h>
#include <stdio.h>
#include <string.h>

List* list_create()
{
    return malloc(sizeof(List));
}

void list_add(List *list, char name[], char surname[], char birthdate[], char phoneno[], char email[], char address[])
{
    ListNode *node = malloc(sizeof(ListNode));

    node->name = name;
    node->surname = surname;
    node->birthdate = birthdate;
    node->phoneno = phoneno;
    node->email = email;
    node->address = address;

    if(list == NULL) {
        list = list_create();
        list_add(list, name, surname, birthdate, phoneno, email, address);
    } else {
        ListNode* last = list->last;

        if(last == NULL) {
            list->first = node;
            list->last = node;
        } else {
            last->next = node;
            node->prev = last;
            list->last = node;
        }

    }

}

void list_delete(List *list)
{

    ListNode *cur = list->first;

    while (cur != NULL) {
        if(cur->prev != NULL) {
            list_remove_elem(list, cur->prev);
        }
        list->first = cur;
        cur = cur->next;
    }

    free(list);
}

void list_remove_elem(List *list, ListNode *node)
{
    if(list->first == NULL || list->last == NULL){
		printf("List is empty");
		return;
	}

	if(node == NULL){
		printf("Node can't be null");
		return;
	}

	ListNode* cur = list->first;

	while(cur != NULL) {
        if(cur == node) {
            if(cur == list->first && cur == list->last) {
                list->first = NULL;
                list->last = NULL;
            } else if (cur == list->first) {
                list->first = cur->next;
                list->first->prev = NULL;
            } else if (cur == list->last) {
                list->last = list->last->prev;
                list->last->next = NULL;
            } else {
                cur->prev->next = cur->next;
                cur->next->prev = cur->prev;
            }
            cur = NULL;
        } else {
            cur = cur->next;
        }
    }
}

ListNode* list_find(List *list, char name[], char surname[], char email[]) {
	ListNode *cur = list->first;

	while(cur != NULL && !(strcmp(name, cur->name) == 0 && strcmp(surname, cur->surname) == 0 && strcmp(email, cur->email) == 0)) {
		cur = cur->next;
	}
	return cur;
}

List* list_sort (List *list) {

    if(list == NULL || list->first == NULL || list->last == NULL) {
        printf("Lista jest pusta\n");
        return NULL;
    }

    List* sortedList = list_create();

    ListNode* maxValue;
    ListNode* cur;

    // until list is empty
    while(list->first != NULL) {
        cur = list->first;
        maxValue = list->first;
        while(cur != NULL) {
            if(strcmp(cur->surname, maxValue->surname) > 0) {
                maxValue = cur;
            }
            cur = cur->next;
        }
        list_add(sortedList, maxValue->name, maxValue->surname, maxValue->birthdate, maxValue->phoneno, maxValue->email, maxValue->address);
        list_remove_elem(list, maxValue);
    }

    return sortedList;

}

void list_print_first(List *list)
{
	ListNode *cur = list->first;

	if(list->first == NULL || list->last == NULL){
		printf("\nLista jest pusta\n");
	} else {
		printf("\nListaN: ");
		while(cur != NULL) {
        	printf("%s -> ", cur->name);
        	cur = cur->next;
    	}
	}
}

void list_print_last(List *list)
{
	ListNode *cur = list->last;

	if(list->first == NULL || list->last == NULL){
		printf("\nLista jest pusta\n");
	} else {
		printf("\nListaP: ");
		while(cur != NULL) {
        	printf("%s -> ", cur->name);
        	cur = cur->prev;
    	}
	}
}
