CC = gcc -Wall
LIB_NAME = clist


static:
	# kompilacja zrodla
	$(CC) -c $(LIB_NAME).c -o $(LIB_NAME).o

	# zbuduj biblioteke statyczna .a
	ar rcs lib$(LIB_NAME).a $(LIB_NAME).o

	# czyscimy
	make clean

shared:

	# zbuduj biblioteke wspoldzielona .so
	$(CC) -fPIC -c $(LIB_NAME).c -o $(LIB_NAME).o
	$(CC) -fPIC -shared -o lib$(LIB_NAME).so $(LIB_NAME).o

	# czyscimy
	make clean

clean:
	rm -f *.o
