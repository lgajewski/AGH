#include <stdio.h>
#include <gsl/gsl_errno.h>
#include <gsl/gsl_math.h>
#include <gsl/gsl_roots.h>

#include "root_fn.h"
#include "root_fn.c"

#define MAX_ITER 1e4

int bracket(double x_lo, double x_hi, struct quadratic_params params, const gsl_root_fsolver_type *T) {
  int iter = 0, status;
  
  double r = 0, r_expected = sqrt (5.0);

  gsl_function F;
  F.function = &quadratic;
  F.params = &params;

  
  gsl_root_fsolver *s = gsl_root_fsolver_alloc (T);
  gsl_root_fsolver_set (s, &F, x_lo, x_hi);

  printf ("using %s method\n", 
          gsl_root_fsolver_name (s));

  printf ("%5s [%9s, %9s] %9s %10s %9s\n",
          "iter", "lower", "upper", "root", 
          "err", "err(est)");
 
  do {
      iter++;
      status = gsl_root_fsolver_iterate (s);
      r = gsl_root_fsolver_root (s);
      x_lo = gsl_root_fsolver_x_lower (s);
      x_hi = gsl_root_fsolver_x_upper (s);
      status = gsl_root_test_interval (x_lo, x_hi,
                                       0, 0.001);

      if (status == GSL_SUCCESS)
        printf ("Converged:\n");

      printf ("%5d [%.7f, %.7f] %.7f %+.7f %.7f\n",
              iter, x_lo, x_hi,
              r, r - r_expected, 
              x_hi - x_lo);
    } while (status == GSL_CONTINUE && iter < MAX_ITER);

  gsl_root_fsolver_free (s);
  return status;
}

int derivatives( double x0, double x, struct quadratic_params params, const gsl_root_fdfsolver_type *T) {
  int iter = 0, status;
  double r_expected = sqrt (5.0);

  gsl_function_fdf FDF;
  FDF.f = &quadratic;
  FDF.df = &quadratic_deriv;
  FDF.fdf = &quadratic_fdf;
  FDF.params = &params;

  
  gsl_root_fdfsolver *s = gsl_root_fdfsolver_alloc (T);
  gsl_root_fdfsolver_set (s, &FDF, x);

  printf ("using %s method\n", gsl_root_fdfsolver_name (s));

  printf ("%-5s %10s %10s %10s\n",
          "iter", "root", "err", "err(est)");
  do {
      iter++;
      status = gsl_root_fdfsolver_iterate (s);
      x0 = x;
      x = gsl_root_fdfsolver_root (s);
      status = gsl_root_test_delta (x, x0, 0, 1e-3);

      if (status == GSL_SUCCESS)
        printf ("Converged:\n");

      printf ("%5d %10.7f %+10.7f %10.7f\n",
              iter, x, x - r_expected, x - x0);
    } while (status == GSL_CONTINUE && iter < MAX_ITER);

  gsl_root_fdfsolver_free (s);
  return status;
}

int main (void) {
	printf("1) gsl_root_fsolver_bisection \n");
	printf("2) gsl_root_fsolver_falsepos \n");
	printf("3) gsl_root_fsolver_brent \n");
	printf("4) gsl_root_fdfsolver_newton \n");
	printf("5) gsl_root_fdfsolver_secant \n");
	printf("6) gsl_root_fdfsolver_steffenson \n");
	
	printf("\nEnter digit: ");
	char c = getchar();
	printf("\nSelected %c method\n", c);
	
	struct quadratic_params params = {1.0, 0.0, -5.0};
	
	double x_lo = 0.0, x_hi = 5.0;
	
  int status;

	switch(c) {
		case '1':
			status = bracket(x_lo, x_hi, params, gsl_root_fsolver_bisection);
			break;
		case '2':
			status = bracket(x_lo, x_hi, params, gsl_root_fsolver_falsepos);
			break;
		case '3':
			status = bracket(x_lo, x_hi, params, gsl_root_fsolver_brent);
			break;
		case '4':
			status = derivatives(x_lo, x_hi, params, gsl_root_fdfsolver_newton);
			break;
		case '5':
			status = derivatives(x_lo, x_hi, params, gsl_root_fdfsolver_secant);
			break;
		case '6':
			status = derivatives(x_lo, x_hi, params, gsl_root_fdfsolver_steffenson);
			break;
		default:
			printf("Unsupported method");
			return -1;
	}
	



  return status;
}