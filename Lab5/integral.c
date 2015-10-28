#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <fcntl.h>
#include <math.h>
#include <gsl/gsl_integration.h>

const double EPS_ABS = 1e-5;
const double EPS_REL = 1e-3;


char* FILE_NEVAL = "f_neval.txt";
char* FILE_OSC = "f_osc.txt";

double integral_trapeze(gsl_function fun, double a, double b, double exact_val, int* neval_r) {
	double (*foo)(double, void*);
    foo = fun.function;
    void* params = fun.params;
	int neval = 0;
	double result;
	while(fabs(exact_val-result) > EPS_ABS || fabs(exact_val-result)/exact_val > EPS_REL) {
		result = 0;
		neval++;
		double h = (b-a)/neval;
		double tmp_a = a;
		int intervals;
		for(intervals=0; intervals<neval; intervals++) {
			double h1 = foo(tmp_a, params);
			double h2 = foo(tmp_a+h, params);
			double area =  + (h1+h2)*(tmp_a+h - tmp_a)/2; //(a+b)*h/2
			result += area;
			tmp_a += h;
		}
	}
	*neval_r = neval;
	return result;
}

void calculate(FILE* file, gsl_function fun, double a, double b, double exact_val, const char* msg) {
	int neval = 0;
	double result = 0;
	int print_to_console = 1;

	if(exact_val != -1) {
		result = integral_trapeze(fun, 0.0, 1.0, exact_val, &neval);
		printf("[Trapezoidal Rule] \t%5s\tneval: %3d, result: %3.5lf\n", msg,  neval, result);
		fprintf(file, "%s,trapez,%d\n", msg, neval);
	}

	// concat a
	size_t neval_t;
	double error;
	if(fun.params != NULL) {
		int* a = (int*)fun.params;
		char buffer[80];
		sprintf(buffer, "%s,%d", msg, *a);
		msg = buffer;
		if(*a > 1) print_to_console = 0;
	} else {
		gsl_integration_qng(&fun, a, b, EPS_ABS, EPS_REL, &result, &error, &neval_t);	// &error - error
		printf("[gsl_integration_qng] \t%5s\tneval: %3zu, result: %3.5lf\n", msg,  neval_t, result);
		fprintf(file, "%s,qng,%zu\n",  msg, neval_t);
	}


	gsl_integration_workspace* workspace;

	workspace = gsl_integration_workspace_alloc(512);
	gsl_integration_qag(&fun, a, b, EPS_ABS, EPS_REL, 512, 1 , workspace, &result, &error);	// null - abserror
	if(print_to_console) printf("[gsl_integration_qag] \t%5s\tneval: %3zu, result: %3.5lf\n", msg,  workspace->size, result);
	fprintf(file, "%s,qag,%zu\n",  msg, workspace->size);
	gsl_integration_workspace_free(workspace);


	workspace = gsl_integration_workspace_alloc(512);
	gsl_integration_qags(&fun, a, b, EPS_ABS, EPS_REL, 512, workspace, &result, &error); // null - abserror
	if(print_to_console) printf("[gsl_integration_qags] \t%5s\tneval: %3zu, result: %3.5lf\n", msg,  workspace->size, result);
	fprintf(file, "%s,qags,%zu\n",  msg, workspace->size);
	gsl_integration_workspace_free(workspace);



	double arr[] = {a,b};
	workspace = gsl_integration_workspace_alloc(512);
	gsl_integration_qagp(&fun, arr, 2, EPS_ABS, EPS_REL, 512, workspace, &result, &error); // null - abserror
	if(print_to_console) printf("[gsl_integration_qagp] \t%5s\tneval: %3zu, result: %3.5lf\n", msg,  workspace->size, result);
	fprintf(file, "%s,qagp,%zu\n",  msg, workspace->size);
	gsl_integration_workspace_free(workspace);

}


double x_x(double x, void* params) {
	return x*x;
}

double sqrt_x(double x, void* params) {
	return sqrt(x);
}

double acos_ax(double x, void* params){
	int* a = (int*)params;
	return (*a)*cos((*a)*x);
}

double qawo(double x, void* params){
	return *(int*)params;
}

double tan_derivative(double x, void* p){
	return 1/((x*x)+1);
}


int main (int argc, char* argv[]) {

	// neval file
	FILE* f_neval = fopen(FILE_NEVAL, "w");
	fprintf(f_neval, "fun,alg,neval\n");

	// gsl_functions
	gsl_function fun1;
	fun1.function = &x_x;
	fun1.params = NULL;

	// calculate x^2
	calculate(f_neval, fun1, 0.0, 1.0, 1.0/3.0, "x2");

	printf("\n");

	// gsl_functions
	gsl_function fun2;
	fun2.function = &sqrt_x;
	fun2.params = NULL;

	// calculate sqrt(x)
	calculate(f_neval, fun2, 0.0, 1.0, 2.0/3.0, "sqrt_x");


	/**
	*
	* OSCILLATING FUNCTIONS
	*
	*/

	printf("\n\n");

	// osc file
	FILE* f_osc = fopen(FILE_OSC, "w");
	fprintf(f_osc, "fun,a,alg,n\n");

	// gsl_functions
	int a=1;

	gsl_function fun3;
	fun3.function = &acos_ax;
	fun3.params = (void*)&a;

	gsl_function fun4;
	fun4.function = &qawo;
	fun4.params = (void*)&a;

	double b = 9.0/2.0*M_PI;

	calculate(f_osc, fun3, 0.0, b, -1, "acos_ax"); 



	// next step
	double error, result;
	while(a <= 100) {
		fun3.function = &acos_ax;
		fun3.params = (void*)&a;
		calculate(f_osc, fun3, 0.0, b, -1, "acos_ax");

		// gsl_integration_qawo
		gsl_integration_workspace* workspace = gsl_integration_workspace_alloc(512);
		gsl_integration_qawo_table* tab = gsl_integration_qawo_table_alloc(a, b, GSL_INTEG_COSINE, 512); 
		gsl_integration_qawo(&fun4, 0, EPS_REL, EPS_REL, 512, workspace, tab, &result, &error);
		if(a == 1) printf("[gsl_integration_qawo] \tacos_ax,%d\tneval: %3zu, result: %3.5lf\n",  a, workspace->size, result);
		fprintf(f_osc, "acos_ax,%d,qawo,%zu\n", a, workspace->size);
		gsl_integration_qawo_table_free(tab);
		gsl_integration_workspace_free(workspace);

		a++;
	}

	printf("\n\n");

	// gsl_integration_qagiu
	gsl_function fun5;
	fun5.function = &tan_derivative;
	fun5.params = NULL;
	gsl_integration_workspace* workspace = gsl_integration_workspace_alloc(512);
	gsl_integration_qagiu(&fun5, 0.1, EPS_ABS, EPS_REL, 512, workspace, &result, &error);
	printf("[gsl_integration_qagiu] 1/(x^2+1)\tneval: %3zu, result: %3.5lf\n", workspace->size, result);
	gsl_integration_workspace_free(workspace);


	fclose(f_osc);
    fclose(f_neval);

    return 0;
}
