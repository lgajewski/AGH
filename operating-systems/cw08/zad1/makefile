CC = gcc -Wall -pthread
P1 = pthread
P2 = gen

all: clean compile 

clean:
	rm -rdf serv
	rm -f $(P1)

compile:
	$(CC) $(P1).c -o $(P1)
	$(CC) $(P2).c -o $(P2)
