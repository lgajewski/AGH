CC = gcc -Wall -lgsl -lm
P1 = fft

all: clean $(P1) 

clean:
	rm -f $(P1)

$(P1):
	$(CC) $@.c -o $@