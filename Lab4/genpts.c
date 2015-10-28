#include <stdlib.h>
#include <stdio.h>
#include <time.h>
#include <math.h>
#include <gsl/gsl_interp.h>
#include <gsl/gsl_spline.h>
#include <sys/time.h>
#include <gsl/gsl_statistics.h>

const int MEASURE_NO = 10;

// maksymalna wartosc punktow
const int Y_MIN = 0;
const int Y_MAX = 10;

// przedzial na osi x
const int X_MIN = 0;
const int X_MAX = 300;

// tablice z punktami
double *y_arr,*x_arr;

// ilosc punktow
int points_no = 0;

// definicja wielomianu
struct polynomial {
    int n;
    double *a;
};
typedef struct polynomial polynomial;

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

// generowanie punktow x,y
void gen(int i) {
    srand(time(NULL));
    int x=X_MIN,y=Y_MIN;
    x_arr = (double*) malloc(points_no*sizeof(double));
    y_arr = (double*) malloc(points_no*sizeof(double));
    double interval = X_MAX - X_MIN;
    double step = interval / points_no;
    x = X_MIN;
    while(i--) { 
        int index = points_no-i-1;
        y = get_random_double(Y_MIN, Y_MAX);
        x += step;
        x_arr[index] = x;
        y_arr[index] = y;
    }
}

// alokacja wielomianu - funkcja analogiczna do funkcji init z biblioteki GSL
polynomial* polynomial_alloc(int degree) {
    polynomial* result = (polynomial*) malloc( sizeof(polynomial) );
    result->a = malloc(degree*sizeof(double));
    result->n = degree;
    int i=degree;
    while(i--) (result->a)[i] = 0.0;
    return result;
}

// zwalniamy pamiec po wielomianie
void polynomial_free(polynomial* pol){
    free(pol->a);
    free(pol);
}

int addPolynomial(polynomial* a,polynomial* b){
    if(a->n==b->n){
        int i=a->n;
        while(i--){
            a->a[i]+=b->a[i];
        }
        return 0;
    } else return 1;
}
/**
* towrzy kopię wielomianu pol
*/
polynomial* polycopy(polynomial* pol){
    polynomial* ret = polynomial_alloc(pol->n);
    int i=pol->n;
    while(i--){
        ret->a[i]=pol->a[i];
    }
    return ret;
}
/**
* mnoży wielomian a razy jednomian (x + 'x') gdzie 'x' to argument
* zwraca 0 w razie sukcesu, wpp kod błędu
*/
void multiplyByMonomial(polynomial* a, double x){
    int i= a->n;
    polynomial* copy = polycopy(a);
    while(i-->1){
        a->a[i] = x* copy->a[i];
        a->a[i] += copy->a[i-1];
    }
    a->a[0]=x*copy->a[0];
}
/**
* mnoży wielomian razy skalar 'x'; zwraca 0 albo kod błędu
*/
int multiplyByScalar(polynomial* a, double x){
    int i= a->n;
    while(i--){
        a->a[i] *=x;
    }
    return 0;
}
/**
* oblicza wartoś wielomianu interpretacyjnego pol w punkcie x
* !wielomian musi być zainicjalizowany polynomial_init
*/
double polynomial_eval(polynomial* pol, double x){
    double tmp=0    ;
    int i=pol->n;
    while(i--){
        double ii=i;
        tmp+=(pol->a[i])*pow(x,ii);
    }
    return tmp;
}
/**
* pomocnicza funkcja ustalająca wielomian interpolacyjny
*/
void Lagrange(polynomial* pol, double* xa, double* ya, int num){
    int roots = pol->n;
    int i = roots;
    int j;
    double under;
    polynomial* tmp;
    while(i--){
        tmp=polynomial_alloc(pol->n);
        tmp->a[0]=1;
        under=ya[i];
        j=roots;
        while(j--){
            if(j!=i){
                under/=( xa[i]-xa[j] );
                multiplyByMonomial(tmp,-xa[j]); // (x - xa[j])
            }
        }
        multiplyByScalar(tmp,under);
        addPolynomial(pol,tmp);
    }
}
/**
* pomocnicza funkcja ustalająca wielomian interpolacyjny
*/
void Newton(polynomial* pol, double* xa, double* ya, int num){
    int roots = pol->n;
    int i = roots;
    int j = roots;
    double** tab = malloc(roots * sizeof(double*) );
    while(j--){
        tab[j]=malloc( (j+1)* sizeof(double) );
    }
    j=roots;
    while(j--){
        tab[j][0]=ya[j];
    }

    j=1;
    while(j<roots){
        int k=1;
        while(k<=j){
            tab[j][k]=(tab[j][k-1]-tab[j-1][k-1])/(xa[j]-xa[j-k]);
            k++;
        }
        j++;
    }

    polynomial* tmp = polynomial_alloc(pol->n);
    polynomial* n = polynomial_alloc(pol->n);
    n->a[0]=1;
    i=0;
    while(i<roots){
        polynomial* m = polycopy(n);
        multiplyByScalar(n,tab[i][i]);
        addPolynomial(tmp,n);
        j=roots;
        n=m;
        multiplyByMonomial(n,-xa[i]);
        i++;
    }
    i=roots;
    while(i--){
        pol->a[i]=tmp->a[i];
    }
    polynomial_free(tmp);
    polynomial_free(n);
}

void polynomial_init_lagrange(polynomial* pol, double* x_arr, double* y_arr, int num){
        Lagrange(pol,x_arr,y_arr,num);
}

void polynomial_init_newton(polynomial* pol, double* x_arr, double* y_arr, int num){
    Newton(pol,x_arr,y_arr,num);
}

void gen_gsl_values(FILE* f_interp, FILE* f_times, gsl_interp* interp, gsl_interp_accel* acc, const char* name) {
    struct timeval start, stop;
    double mstep = 4;
    double step = (x_arr[1] - x_arr[0])/ mstep;
    int j, i = MEASURE_NO;
    double x0, eval;
    float t_delta;
    while(i--) {
        j = points_no * mstep - 3;
        x0 = x_arr[0];
        gettimeofday(&start, NULL);
        while(j--) {
            eval = gsl_interp_eval(interp, x_arr, y_arr, x0, acc);
            // save values
            if(i == 0)
                fprintf(f_interp, "%s,%lf,%lf\n", name, x0, eval);
            // update x0
            x0 += step;
            printf("j: %d x0: %lf\n", j, x0);
        }
        printf("%d\n", i);
        gettimeofday(&stop, NULL);
        t_delta = get_time_diff(start, stop);
        // save to file
        fprintf(f_times, "%s,%d,%f\n", name, points_no, t_delta);

    }
}

void gen_poly_values(FILE* f_interp, FILE* f_times, polynomial* interp, const char* name) {
    struct timeval start, stop;
    double mstep = 4;
    double step = (x_arr[1] - x_arr[0])/ mstep;
    int j, i = MEASURE_NO;
    double x0;
    float t_delta;
    while(i--) {
        j = points_no * mstep - 3;
        x0 = x_arr[0];
        gettimeofday(&start, NULL);
        while(j--) {
            double eval = polynomial_eval(interp, x0);
            // save values
            if(i==0)
                fprintf(f_interp, "%s,%lf,%lf\n", name, x0, eval);
            // update x0
            x0 += step;

        }
        gettimeofday(&stop, NULL);
        t_delta = get_time_diff(start, stop);
        // save to file
        fprintf(f_times, "%s,%d,%f\n", name, points_no, t_delta);

    }
}

int main(int argc, char *argv[]){
    if(argc != 2){
        printf("usage: %s [N]\n", argv[0]);
        return 0;
    }
    int num = atoi(argv[1]);
    if(num < 1){
        printf("\nN must be greater or equal 1\n");
        return 0;
    } if(num > 300) {
        printf("\nN must be less or equal than 300\n");
        return 0;
    } else {
        points_no = num;
    }

    gen(num);

    gsl_interp_accel *acc = gsl_interp_accel_alloc();
    gsl_interp* interp = gsl_interp_alloc(gsl_interp_polynomial, points_no);
    gsl_interp* cspline = gsl_interp_alloc(gsl_interp_cspline , points_no);
    gsl_interp* akima = gsl_interp_alloc(gsl_interp_akima , points_no);
    polynomial* lagrangeInterp =polynomial_alloc(points_no);
    polynomial* newtonInterp =polynomial_alloc(points_no);
    gsl_interp_init (interp,x_arr,y_arr, points_no);
    gsl_interp_init (cspline,x_arr,y_arr, points_no);
    gsl_interp_init (akima,x_arr,y_arr, points_no);
    polynomial_init_lagrange(lagrangeInterp,x_arr,y_arr, points_no);
    polynomial_init_newton(newtonInterp,x_arr,y_arr, points_no);

    // save points
    FILE* f_points = fopen("f_points.txt","w");
    fprintf(f_points, "x,y\n");
    int i = points_no;
    while(i--) {
        fprintf(f_points,"%f,%f\n",x_arr[i],y_arr[i]);
    }
    fclose(f_points);


    // save interp values
    FILE* f_interp = fopen("f_interp.txt","w");
    FILE* f_times = fopen("f_times.txt","a");

    fprintf(f_interp, "alg,x,y\n");
    // fprintf(f_times, "alg,n,time\n");

    gen_gsl_values(f_interp, f_times, interp, acc, "polynomial");
    gen_gsl_values(f_interp, f_times, cspline, acc, "cspline");
    gen_gsl_values(f_interp, f_times, akima, acc, "akima");
    gen_poly_values(f_interp, f_times, lagrangeInterp, "lagrange");
    gen_poly_values(f_interp, f_times, newtonInterp, "newton");

    fclose(f_times);
    fclose(f_interp);


    polynomial_free(lagrangeInterp);
    polynomial_free(newtonInterp);
    gsl_interp_free(interp);
    gsl_interp_free(cspline);
    gsl_interp_free(akima);
    gsl_interp_accel_free(acc);

    free(x_arr);
    free(y_arr);

    return 0;
}
