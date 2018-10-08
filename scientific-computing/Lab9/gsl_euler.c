#include <stdio.h>
#include <gsl/gsl_errno.h>
#include <gsl/gsl_matrix.h>
#include <gsl/gsl_odeiv2.h>
#include "commons.h"

// differential equation
// v = x'
// v' = -kx / m

int func (double t, const double y[], double f[],
      void *params) {
  f[0] = y[1];
  f[1] = -(K*y[0])/M;
  return GSL_SUCCESS;
}

int jac (double t, const double y[], double *dfdy, 
     double dfdt[], void *params) {
  gsl_matrix_view dfdy_mat 
    = gsl_matrix_view_array (dfdy, 2, 2);
  gsl_matrix * m = &dfdy_mat.matrix; 
  gsl_matrix_set (m, 0, 0, 0.0);
  gsl_matrix_set (m, 0, 1, 1.0);
  gsl_matrix_set (m, 1, 0, -K/M);
  gsl_matrix_set (m, 1, 1, 0);
  dfdt[0] = 0.0;
  dfdt[1] = 0.0;
  return GSL_SUCCESS;
}

int main (void) {
  gsl_odeiv2_system sys = {func, jac, 2, NULL};

  gsl_odeiv2_driver * d = gsl_odeiv2_driver_alloc_y_new (&sys, gsl_odeiv2_step_rk8pd, DELTA, DELTA, 0.0);
  int i;
  double t = 0.0, t1 = N;
  double y[2] = { INIT_SWING, 0.0 };

  FILE* f_values = fopen("gsl_values.txt","w");
  fprintf(f_values, "x,v\n");

  for (i = 1; i <= N; i++) {
      double ti = i * t1 / N;
      int status = gsl_odeiv2_driver_apply (d, &t, ti, y);

      if (status != GSL_SUCCESS) {
	       printf ("error, return value=%d\n", status);
         break;
      }

      printf("x: %3.4f, v: %3.4f\n", y[0], y[1]);
      fprintf(f_values, "%f,%f\n", y[0], y[1]);
    
    }


  gsl_odeiv2_driver_free (d);
  fclose(f_values);     
  return 0;
}