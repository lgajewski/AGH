CC = gcc -Wall
LIBS = -lgslcblas -lgsl -lm
P1 = genpts

all: clean compile

compile:
	$(CC) $(P1).c $(LIBS) -o $(P1)

clean:
	rm -f $(P1)