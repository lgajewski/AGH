#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/times.h>
#include <string.h>
#include <time.h>
#include <gsl/gsl_blas.h>
#include <gsl/gsl_vector.h>
#include <gsl/gsl_matrix.h>


const char* FILE_NAME = "zad1_results.txt";

const int START_N = 1000;  // poczatkowa wielkosc wektora/macierzy
const int STEP = 100;  // skok miedzy wielkosciami wektorow/macierzy

// ilosc pomiarow
const int AMOUNT_OF = 11;

// ilosc pomiarow dla kazdej wartosci n
const int AMOUNT_PER_N = 10;

// wartosci wektorow, macierzy
const int V_MIN = 0;
const int V_MAX = 10;

// struct
struct result {
  int n;
  char* alg;
  double time;
};
typedef struct result result;

// clocks
static clock_t st_time;
static clock_t en_time;
static struct tms st_cpu;
static struct tms en_cpu;

double randfrom(double min, double max) {
  double range = max - min;
  double dif = RAND_MAX / range;
  return min + (rand() / dif);
}

void blas1(struct result* res[]) {
  int i,j,k;
  int n;
  double* val = malloc(sizeof(double));
  long start,end;
  double times_sum = 0;

  for(i=0; i<AMOUNT_OF; i++) {
    n = START_N + i*STEP;
    gsl_vector* v1 = gsl_vector_alloc(n);
    gsl_vector* v2 = gsl_vector_alloc(n);

    for(k=0; k<AMOUNT_PER_N; k++) {
      for(j=0; j<n; j++) {
        gsl_vector_set(v1, j, randfrom(V_MIN, V_MAX));
        gsl_vector_set(v2, j, randfrom(V_MIN, V_MAX));
      }
      start = clock();
      gsl_blas_ddot(v1, v2, val);
      end = clock();

      times_sum += (double)(end-start)/CLOCKS_PER_SEC;
    }

    result* r = (result*) calloc(1, sizeof(result));
    r->n = n;
    r->alg = "b1";   
    r->time = times_sum / AMOUNT_PER_N;

    res[i] = r;
    gsl_vector_free(v1);
    gsl_vector_free(v2);
  }
}


void blas2(struct result* res[]) {
  int i,j,k,l;
  int n;
  double* val = malloc(sizeof(double));
  long start,end;
  double times_sum = 0;
  double alpha, beta;

  for(i=0; i<AMOUNT_OF; i++) {
    n = START_N + i*STEP;
    gsl_matrix *A = gsl_matrix_alloc(n, n);
    gsl_vector *v1 = gsl_vector_alloc(n);
    gsl_vector *v2 = gsl_vector_alloc(n);

    for(k=0; k<AMOUNT_PER_N; k++) {
      // randomize, fill
      for(j=0; j<n; j++) {
        for(l=0; l<n; l++) gsl_matrix_set(A, j, l, randfrom(V_MIN, V_MAX));
        gsl_vector_set(v1, j, randfrom(V_MIN, V_MAX));
        gsl_vector_set(v2, j, randfrom(V_MIN, V_MAX));
      }
      alpha = randfrom(V_MIN, V_MAX);
      beta = randfrom(V_MIN, V_MAX);

      start = clock();
      gsl_blas_dgemv(CblasNoTrans, alpha, A, v1, beta, v2);
      end = clock();

      times_sum += (double)(end-start)/CLOCKS_PER_SEC;
    }

    result* r = (result*) calloc(1, sizeof(result));
    r->n = n;
    r->alg = "b2";   
    r->time = times_sum / AMOUNT_PER_N;

    res[i] = r;
    gsl_matrix_free(A);
    gsl_vector_free(v1);
    gsl_vector_free(v2);
  }
}


int main() {
  srand(time(NULL));

  int i;
  result** b1;
  result** b2;
  char buffer[100];

  // open file
  FILE* fp = fopen(FILE_NAME, "w");

  fwrite("n,alg,time\n", 11, sizeof(char), fp);

  // BLAS 1
  b1 = (result**) calloc(AMOUNT_OF, sizeof(result*));
  blas1(b1);

  for(i=0; i<AMOUNT_OF; i++) {
  	int total = sprintf(buffer, "%d,%s,%lf\n", b1[i]->n, b1[i]->alg, b1[i]->time);
    printf("%s", buffer);
  	fwrite(buffer, total, sizeof(char), fp);
    
  }

  printf("\n");

  // BLAS 2
  b2 = (result**) calloc(AMOUNT_OF, sizeof(result*));
  blas2(b2);

  for(i=0; i<AMOUNT_OF; i++) {
    int total = sprintf(buffer, "%d,%s,%lf\n", b2[i]->n, b2[i]->alg, b2[i]->time);
    printf("%s", buffer);
  	fwrite(buffer, total, sizeof(char), fp);
  }

  // write to file

  fclose(fp);

  return 0;
}

