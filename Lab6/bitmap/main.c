#include "bitmap.h"
#include <stdlib.h>
#include <stdio.h>
#include <gsl/gsl_vector.h>
#include <gsl/gsl_multiroots.h>
#include <time.h>

#define WIDTH 800
#define HEIGHT 600

#define CUSTOM 1

struct rparams
  {
    double a;
    double b;
  };

typedef struct unique_list {
	double x;
	double y;
	int r;
	int g;
	int b;
	struct unique_list* next;
} unique_list;

int fequal(double a, double b) {
	return (fabs(a-b) < 1e-2);
}

int get_random_int(int start, int end) {
	return (end + rand() / (RAND_MAX / (start - end + 1) + 1));
}

unique_list* createList() {
	unique_list* list = (unique_list*) calloc(1, sizeof(unique_list));
	list->next = NULL;
	return list;
}



void put(unique_list* head, double x, double y, int* rgb) {
	unique_list* cur = head;
	while(cur->next != NULL) {
		if(fequal(cur->next->x, x) && fequal(cur->next->y, y)) {
			rgb[0] = cur->next->r;
			rgb[1] = cur->next->g;
			rgb[2] = cur->next->b;
			return;
		}
		cur = cur->next;
	}
	unique_list* node = (unique_list*) calloc(1, sizeof(unique_list));
	node->x = x;
	node->y = y;
	if(CUSTOM) {
		printf("R: ");
		scanf("%d", &node->r);
		printf("G: ");
		scanf("%d", &node->g);
		printf("B: ");
		scanf("%d", &node->b);
	} else {		
		node->r = get_random_int(0, 255);
		node->g = get_random_int(0, 255);
		node->b = get_random_int(0, 255);
	}
	node->next = NULL;
	rgb[0] = node->r;
	rgb[1] = node->g;
	rgb[2] = node->b;
	cur->next = node;
}

void print(unique_list* head) {
	unique_list* cur = head;
	while(cur->next != NULL) {
		printf("(%lf,%lf) : (%d,%d,%d)\n", cur->x, cur->y, cur->r, cur->g, cur->b);
		cur = cur->next;
	}
}

int rosenbrock_f (const gsl_vector * xv, void *params, 
              gsl_vector * f) {

  const double x = gsl_vector_get (xv, 0);
  const double y = gsl_vector_get (xv, 1);

  const double f1 = (sqrt(x*x + y*y)) - 1;
  const double f2 = (x*x*x-3*x*y*y)+(3*x*x*y-y*y*y) - 1;

  gsl_vector_set (f, 0, f1);
  gsl_vector_set (f, 1, f2);

  return GSL_SUCCESS;
}

void print_state (size_t iter, gsl_multiroot_fsolver * s) {
  printf ("iter = %3lu x = % .3f y = % .3f "
          "f(x) = % .3e % .3e\n",
          iter,
          gsl_vector_get (s->x, 0), 
          gsl_vector_get (s->x, 1),
          gsl_vector_get (s->f, 0), 
          gsl_vector_get (s->f, 1));
}



int main (void) {
	srand(time(NULL));
	unique_list* head = createList();

	int i,j;
	double x0 = -10;
	double y0 = -10;

	int w=WIDTH,h=HEIGHT;
	double stepW = (x0*2)/w;
	double stepH = (y0*2)/h;

	char rgb_map[w*h*3];


	if(stepW < 0) stepW *= -1;
	if(stepH < 0) stepH *= -1;

	double y_start = y0;

	for(i=0; i<w; i++) {

		y0 = y_start;
		for(j=0; j<h; j++) {

			const gsl_multiroot_fsolver_type *T;
			gsl_multiroot_fsolver *s;
			double x_init[2] = {x0, y0};
			int status;
			size_t iter = 0;

			const size_t n = 2;
			struct rparams p = {1.0, 1.0};
			gsl_multiroot_function f = {&rosenbrock_f, n, &p};

			gsl_vector *x = gsl_vector_alloc (n);

			gsl_vector_set (x, 0, x_init[0]);
			gsl_vector_set (x, 1, x_init[1]);

			T = gsl_multiroot_fsolver_hybrids;
			s = gsl_multiroot_fsolver_alloc (T, 2);
			gsl_multiroot_fsolver_set (s, &f, x);

			// print_state (iter, s);

			do {
			  iter++;
			  status = gsl_multiroot_fsolver_iterate (s);

			  // print_state (iter, s);

			  if (status)   /* check if solver is stuck */
			    break;

			  status = gsl_multiroot_test_residual (s->f, 1e-7);
			}
			while (status == GSL_CONTINUE && iter < 10000);

			// printf ("status = %s\n", gsl_strerror (status));
			if(status == GSL_SUCCESS) {
				int rgb[3];
				put(head, gsl_vector_get (s->x, 0), gsl_vector_get (s->x, 1), rgb);
				printf("%d %d / (%lf,%lf) : (%d,%d,%d)\n", 
					i+1, j+1, gsl_vector_get (s->x, 0), gsl_vector_get (s->x, 1), 
					rgb[0], rgb[1], rgb[2]);
				rgb_map[j*w*3 + i*3 + 0] = rgb[0];
				rgb_map[j*w*3 + i*3 + 1] = rgb[1];
				rgb_map[j*w*3 + i*3 + 2] = rgb_map[3];
			}

			gsl_multiroot_fsolver_free (s);
			gsl_vector_free (x);

			y0 += stepH;
		}
		x0 += stepW;
	}
	print(head);
	write_bmp("test.bmp", w, h, rgb_map);
  return 0;
}
