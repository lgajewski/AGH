CC = gcc -Wall -lgsl -lm -lgslcblas
P1 = euler gsl_euler

all: clean $(P1) 

clean:
	rm -f $(P1)

$(P1):
	$(CC) $@.c -o $@