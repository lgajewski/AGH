CC = gcc -Wall -lrt
P1 = consumer
P2 = producer

all: clean compile serv_dir

clean:
	rm -rdf serv
	rm -f $(P1) $(P2)

compile:
	$(CC) $(P1).c -o $(P1)
	$(CC) $(P2).c -o $(P2)

serv_dir:
	mkdir serv
