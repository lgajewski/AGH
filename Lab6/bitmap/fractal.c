#include "bitmap.h"
#include <stdlib.h>
#include <stdio.h>
#include <math.h>


int main (void) {

	int i,j;
	double x0 = -2;
	double y0 = -1.5;

	int w=100,h=100;
	double stepW = (x0*2)/w;
	double stepH = (y0*2)/h;


	if(stepW < 0) stepW *= -1;
	if(stepH < 0) stepH *= -1;

	for(i=0; i<w; i++) {
		x0 += stepW;
		y0 = -1.5;
		for(j=0; j<h; j++) {
			y0 += stepH;

			printf("(%lf,%lf)\n", x0, y0);
		}
	}
  return 0;
}
