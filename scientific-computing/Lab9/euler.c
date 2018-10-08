#include <stdio.h>
#include <stdlib.h>
#include "commons.h"


int main(void) {
	int i;

	FILE* f_values = fopen("values.txt","w");
    fprintf(f_values, "x,v\n");

	double x[N];
	double v[N];

	for (i=0; i<N; i++) {
		x[i] = v[i] = 0;
	}

	x[0] = INIT_SWING;

	// differential equation - solved with Euler method
	// v(t+1) = -(kx*delta(t))/m + v(t)
	// x(t+1) = v * delta(t) + x(t)

	for (i=1; i<N; i++) {
		v[i] = -(K*x[i-1]*DELTA)/M + v[i-1];
		x[i] = v[i]*DELTA + x[i-1];
	}

	for (i=0; i<N; i++) {
		printf("x: %3.4f, v: %3.4f\n", x[i], v[i]);
		fprintf(f_values, "%f,%f\n", x[i], v[i]);
	}

	fclose(f_values);


	return 0;
}
