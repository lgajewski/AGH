CC = gcc -Wall
P1 = receiver
P2 = sender

all: clean compile run
	
compile:
	$(CC) $(P1).c -o $(P1)
	$(CC) $(P2).c -o $(P2)

clean:
	rm -f $(P1) $(P2)

run:
	./$(P2) 5

