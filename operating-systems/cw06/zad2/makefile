CC = gcc -Wall -lrt
P1 = server
P2 = client

all: clean compile

clean:
	rm -f $(P1) $(P2)

compile:
	$(CC) $(P1).c -o $(P1)
	$(CC) $(P2).c -o $(P2)
