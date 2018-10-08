#include <stdio.h>
 
 int main() {
    double epsilon = 1.0;
 
    do 
     {
       epsilon /= 2.0f;
     }
    // break when next epsilon is 1
    while ((1.0 + (epsilon/2.0)) != 1.0);
 
    printf("epsilon: %G\n", epsilon);
    return 0;
 }