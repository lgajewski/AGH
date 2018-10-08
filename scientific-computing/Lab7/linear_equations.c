#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <sys/time.h>
#include <gsl/gsl_linalg.h>

#define TIMES 10

FILE* f_times;

int N;
int STEP;
int AMOUNT;

void LU(gsl_matrix_view m, gsl_vector_view b, int size) {
    gsl_permutation * p = gsl_permutation_alloc (size);
    gsl_vector *x = gsl_vector_alloc (size);
    int s;
    gsl_linalg_LU_decomp (&m.matrix, p, &s);
    gsl_linalg_LU_solve (&m.matrix, p, &b.vector, x);
    gsl_permutation_free (p);
    gsl_vector_free (x);
}

void Cholesky(gsl_matrix_view m, gsl_vector_view b, int size) {
    gsl_vector *x = gsl_vector_alloc (size);
    gsl_linalg_cholesky_decomp (&m.matrix);
    gsl_linalg_cholesky_solve (&m.matrix, &b.vector, x);
    gsl_vector_free (x);
}

// generuje losowy double w podanym przedziale
double get_random_double(double fMin, double fMax) {
    double f = (double)rand() / RAND_MAX;
    return fMin + f * (fMax - fMin);
}

// generuje losowy int w podanym przedziale
int get_random_int(int start, int end) {
    return (end + rand() / (RAND_MAX / (start - end + 1) + 1));
}

// zwraca roznice czasow w milisekundach
float get_time_diff(struct timeval t0, struct timeval t1) {
    return (t1.tv_sec - t0.tv_sec) * 1000.0f + (t1.tv_usec - t0.tv_usec) / 1000.0f;
}

int main (int argc, char*argv[]) {
    srand(time(NULL));
	if(argc != 4) {
		printf("usage: %s [N_START] [STEP] [AMOUNT]\n", argv[0]);
		return 0;
	}

	FILE* f_times = fopen("f_times.txt","w");
    fprintf(f_times, "alg,n,time\n");

	N = atoi(argv[1]);
	STEP = atoi(argv[2]);
	AMOUNT = atoi(argv[3]);

	struct timeval start, stop;
	float t_delta;

	int l_n = N;
	int l_amount = AMOUNT;
	int l_times;

	while(l_amount--) {
		// multiple by two - there are two methods to test
		l_times = TIMES*2;

		double a_data[l_n * l_n];
		double b_data[l_n];

		// fill matrix
		int i=0,j=0;
		for(i=0;i<l_n;i++) {
	        for(j=i;j<l_n;j++){
	            a_data[i*l_n+j] = a_data[j*l_n+i] = 1.0/(i+j+1);
	            if(i==j) a_data[i*l_n+j]+=1;
	        }
	        b_data[i] = get_random_double(0, 10);
	    }

		while(l_times--) {
			// copy arrays and operate on them
			double a[l_n*l_n];
			double b[l_n];
			for(i=0; i<l_n; i++) {
				for(j=0; j<l_n; j++) {
					a[i*l_n + j] = a_data[i*l_n + j];
				}
				b[i] = b_data[i];
			}
			gsl_matrix_view matrix = gsl_matrix_view_array (a, l_n, l_n);
			gsl_vector_view vector = gsl_vector_view_array (b, l_n);

			gettimeofday(&start, NULL);
			char* name;

			// select appropriate method
			if(l_times > TIMES) {
				// LU method
				name = "LU";
				LU(matrix, vector, l_n);
			} else {
				// Chomsky method
				name = "Cholesky";
				Cholesky(matrix, vector, l_n);

			}
			gettimeofday(&stop, NULL);
			t_delta = get_time_diff(start, stop);

	        // save to file
	        fprintf(f_times, "%s,%d,%f\n", name, l_n, t_delta);

		}

		l_n += STEP;
	}

	fclose(f_times);


	return 0;
}