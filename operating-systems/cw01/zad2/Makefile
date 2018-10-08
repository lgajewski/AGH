CC = gcc -Wall
LIB_NAME = clist
PROG_NAME = zad1
MAIN_SRC = main

run_static: static
		./$(PROG_NAME)_static

run_shared: shared
		./$(PROG_NAME)_shared

run_dynamic: dynamic
		./$(PROG_NAME)_dynamic

static:
	# kompilacja zrodla
	$(CC) -c $(LIB_NAME).c -o $(LIB_NAME).o

	# zbuduj biblioteke statyczna .a
	ar rcs lib$(LIB_NAME).a $(LIB_NAME).o

	# linkowanie
	$(CC) -static $(MAIN_SRC).c -L. -l$(LIB_NAME) -o $(PROG_NAME)_static

	# czyscimy
	make clean

shared:

	# zbuduj biblioteke wspoldzielona .so
	$(CC) -fPIC -c $(LIB_NAME).c -o $(LIB_NAME).o
	$(CC) -fPIC -shared -o lib$(LIB_NAME).so $(LIB_NAME).o

	# linkowanie
	$(CC) -Wl,-rpath=. -L. -o $(PROG_NAME)_shared $(MAIN_SRC).c -l$(LIB_NAME)

	# czyscimy
	make clean

dynamic:
	# zbuduj biblioteke wspoldzielona .so
	$(CC) -fPIC -c $(LIB_NAME).c -o $(LIB_NAME).o
	$(CC) -Wl,-rpath=. -fPIC -shared -o lib$(LIB_NAME).so $(LIB_NAME).o

	# linkowanie
	$(CC) -L. -o $(PROG_NAME)_dynamic $(MAIN_SRC).c -ldl -D DLL

	# czyscimy
	make clean

clean:
	rm -f *.o
