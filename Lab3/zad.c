#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <gsl/gsl_vector.h>
#include <gsl/gsl_blas.h>

int N=1;

clock_t clk;

// ilosc pomiarow
const int AMOUNT_OF = 10;

// wartosci wektorow, macierzy
const int V_MIN = 0;
const int V_MAX = 100;

void version1(int **A, int **B, int **C){
  int i,j,k;
  for (i=0;i<N; i++)
    for(j=0;j<N;j++)
      for(k=0;k<N; k++)
        C[i][j]+=A[i][k]*B[k][j];
}

void version2(int **A, int **B, int **C) {
  int i,j,k;
  for (i=0;i<N; i++)
    for(k=0;k<N;k++)
      for(j=0;j<N; j++)
        C[i][j]+=A[i][k]*B[k][j];
}

int randfrom(int min, int max) {
  int range = max - min;
  double dif = RAND_MAX / range;
  return min + (int)(rand() / dif);
}

double getTime(clock_t start, clock_t end) {
  return (double)(((double)end - (double)start)/CLOCKS_PER_SEC);
}

int main(int argc, char *argv[]){
  srand(time(NULL));
  int **A, **B, **C;

  int a, i;
  if(argc < 2) {
    printf("usage: %s [n1] [n2] [n3] ...\n", argv[0]);
    exit(0);
  }

  FILE *fp = fopen("result.txt", "w");  
  fwrite("n,alg,time\n", 11, sizeof(char), fp);
  
  for(a=1; a<argc; a++) {
    N = atoi(argv[a]);
    
    A = (int**) calloc(N, sizeof(int*));
    B = (int**) calloc(N, sizeof(int*));
    C = (int**) calloc(N, sizeof(int*));

    for(i = 0; i<N; i++){
      A[i] = (int*) calloc(N, sizeof(int));
      B[i] = (int*) calloc(N, sizeof(int));
      C[i] = (int*) calloc(N, sizeof(int));
    }

    gsl_matrix *A_gsl = gsl_matrix_calloc(N, N);
    gsl_matrix *B_gsl = gsl_matrix_calloc(N, N);
    gsl_matrix *C_gsl = gsl_matrix_calloc(N, N);


    // fill matrix
    int tmp, j;
    for(i=0; i<N; i++){
      for(j=0; j<N; j++){
        tmp = randfrom(V_MIN, V_MAX);
        A[i][j] = tmp;
        gsl_matrix_set(A_gsl, i, j, tmp);

        tmp = randfrom(V_MIN, V_MAX);
        B[i][j] = tmp;
        gsl_matrix_set(B_gsl, i, j, tmp);
      }
    }

    int l;
    for(l = 0; l < AMOUNT_OF; l++){

      clk = clock();
      version1(A,B,C);
      fprintf(fp, "%d,ver1,%f\n", N, getTime(clk, clock()));

      clk = clock();
      version2(A,B,C);
      fprintf(fp, "%d,ver2,%f\n", N, getTime(clk, clock()));

      clk = clock();
      gsl_blas_dgemm (CblasNoTrans, CblasNoTrans, 1, A_gsl, B_gsl, 1, C_gsl);
      fprintf(fp, "%d,gsl,%f\n", N, getTime(clk, clock()));

    }

  }

  fclose(fp);

  return 0;
}

