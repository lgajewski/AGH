struct quadratic_params
  {
    double a, b, c;
  };

double quadratic (double x, void *params);
double quadratic_deriv (double x, void *params);
void quadratic_fdf (double x, void *params, double *y, double *dy);
float get_time_diff(struct timeval t0, struct timeval t1);