CC = gcc -Wall
P1 = server
P2 = client

all: clean compile
	
compile:
	$(CC) $(P1).c -o $(P1)
	$(CC) $(P2).c -o $(P2)

clean:
	rm -f $(P1) $(P2)

run:
	./$(P1)

