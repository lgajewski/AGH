#include <stdlib.h>
#include <stdio.h>
#include <math.h>
#include <gsl/gsl_errno.h>
#include <gsl/gsl_fft_real.h>
#include <gsl/gsl_fft_halfcomplex.h>


int version;
const int n = 200;
const int FREQ = 10;
const double AMP = 0.5;

int main (int argc, char*argv[]) {
	int i;
	double data_in[n];
	double data_fft[n];
	double data_inv[n];

	if(argc != 2) { printf("usage: %s [1,2,3]\n", argv[0]); return 0; }
	version = atoi(argv[1]);

	if(version != 1 && version != 2 && version != 3) { printf("version not supported\n"); return 0; }
	FILE* f_values = fopen("values.txt","w");
    fprintf(f_values, "n,fun,fft_val,inv_fft\n");

	gsl_fft_real_wavetable * real;
	gsl_fft_halfcomplex_wavetable * hc;
	gsl_fft_real_workspace * work;

	for (i = 0; i < n; i++) {
		switch (version) {
			case 1:
				printf("1 version\n");
				data_in[i] = cos(FREQ*(M_PI)*i/n)*AMP;
				break;
			case 2:
				printf("2 version\n");
				data_in[i] = cos(10*(M_PI)*i/n)/2+ sin(20*M_PI)*i/n;
				break;
			case 3:
				printf("3 version\n");
				data_in[i] = cos(4*M_PI*i/n)+((float)rand())/RAND_MAX/8.0;
				break;
		}
		data_fft[i] = data_in[i];
		data_inv[i] = data_in[i];
	}

	work = gsl_fft_real_workspace_alloc (n);
	real = gsl_fft_real_wavetable_alloc (n);

	gsl_fft_real_transform (data_inv, 1, n, real, work);

	gsl_fft_real_wavetable_free (real);

	hc = gsl_fft_halfcomplex_wavetable_alloc (n);


	// make copy for fft array
	for (i = 0; i < n; i++) {
		data_fft[i] = data_inv[i];
	}

	// reduce noises
	for (i = 0; i < n; i++) {
		if(abs(data_inv[i]) < 50) {
			data_inv[i] = 0;
		}
	}

	// inverse
	gsl_fft_halfcomplex_inverse (data_inv, 1, n, hc, work);
	gsl_fft_halfcomplex_wavetable_free (hc);

	gsl_fft_real_workspace_free (work);
	
	for (i = 0; i < n; i++) {
		fprintf(f_values, "%d,%e,%e,%e\n", i+1, data_in[i], data_fft[i], data_inv[i]);
		printf ("%d:\t%+e\t%+e\t%+e\n", i, data_in[i], data_fft[i], data_inv[i]);
	}

	fclose(f_values);

	return 0;
}