CC = gcc -Wall -pthread
P1 = divby0
P2 = signal_test

all: clean compile 

clean:
	rm -rdf serv
	rm -f $(P1)

compile:
	$(CC) $(P1).c -o $(P1)
	$(CC) $(P2).c -o $(P2)
