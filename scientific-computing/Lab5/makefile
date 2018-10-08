CC = gcc -Wall
LIBS = -lgslcblas -lgsl -lm
P1 = integral

all: clean compile

compile:
	$(CC) $(P1).c $(LIBS) -o $(P1)

clean:
	rm -f $(P1)