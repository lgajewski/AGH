CC = gcc -Wall
P1 = main
P2 = fractal
LIBS = -L. -lbitmap -lgsl -lm

all: clean compile

compile:
	$(CC) $(P1).c -o $(P1) $(LIBS)
	$(CC) $(P2).c -o $(P2) $(LIBS)

clean:
	rm -f $(P1) $(P2)