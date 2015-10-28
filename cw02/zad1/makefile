CC = gcc -Wall
P1 = generator
P2 = sort
P3 = sort_sys

all: clean generator sort sort_sys

generator:
	$(CC) $(P1).c -o $(P1)

sort:
	$(CC) $(P2).c -o $(P2)

sort_sys:
	$(CC) $(P3).c -o $(P3)

clean:
	rm -f $(P1) $(P2) $(P3)
