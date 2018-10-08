CC = gcc -Wall
LIBS = -lgslcblas -lgsl -lm
P1 = gsl
P2 = gsl_fail

all: clean compile

compile:
	$(CC) $(P1).c $(LIBS) -o $(P1)
	$(CC) $(P2).c $(LIBS) -o $(P2)

clean:
	rm -f $(P1) $(P2)