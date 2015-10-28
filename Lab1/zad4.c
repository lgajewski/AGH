#include <stdio.h>
 
#define VAL 0.3f

int main() {

    float f = VAL;
    double d = VAL;

    int fl = *(int*)&f;

    long long int dl = *(long long *)&d;

    printf(" - float: %8X\n", fl);
    printf(" - double: %16llX", dl);

    // printf("reprezentacja\n -- float: %8X\n -- double: %16llX\n", *(int *)&f, *(long long *)&d); 

    return 0;
}