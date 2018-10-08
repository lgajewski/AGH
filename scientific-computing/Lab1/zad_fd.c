#include "stdio.h"

int main() {
int i;
#define WART 5.1
  float fl = WART;
  double d = WART;
  printf("%f reprezentacja -- float: %8X, double: %16llX\n",fl, *(int *)&fl, *(long long *)&d); 


  return 0;
}