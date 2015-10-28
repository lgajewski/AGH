double quadratic (double x, void *params) {
  struct quadratic_params *p = (struct quadratic_params *) params;

  double a = p->a;
  double b = p->b;
  double c = p->c;

  return (a * x + b) * x + c;
}

double quadratic_deriv (double x, void *params) {
  struct quadratic_params *p = (struct quadratic_params *) params;

  double a = p->a;
  double b = p->b;

  return 2.0 * a * x + b;
}

void quadratic_fdf (double x, void *params, double *y, double *dy) {
  struct quadratic_params *p = (struct quadratic_params *) params;

  double a = p->a;
  double b = p->b;
  double c = p->c;

  *y = (a * x + b) * x + c;
  *dy = 2.0 * a * x + b;
}

float get_time_diff(struct timeval t0, struct timeval t1) {
    return (t1.tv_sec - t0.tv_sec) * 1000.0f + (t1.tv_usec - t0.tv_usec) / 1000.0f;
}