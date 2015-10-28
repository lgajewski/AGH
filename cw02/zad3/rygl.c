#include <stdlib.h>
#include <stdio.h>
#include <fcntl.h>
#include <unistd.h>
#include <string.h>

static char* file_name;


// headers
void input();
void input_error(char const* msg);
long int getIndex(char c);
struct flock getFlock(int file_handler, short lType, off_t cIndex);
void printRygl(int file_handler);

int main(int argc, char *argv[]) {

	if(argc == 2 && strcmp(argv[1], "--help") == 0) {
		printf("./prog file\n");
	} else if(argc != 2) {
		printf("use '--help' for usage\n");
	} else {
		file_name = argv[1];
		if(access(file_name, F_OK) == -1) input_error("file doesn't exist");

		input();

	}


	return 0;

}

void input() {
	char cin;
	long int cIndex;
	struct flock fl;
	int file_handler = open(file_name, O_RDWR);

	if(file_handler == -1) {
		input_error("cannot open the file");
	}

	// main loop
	int flag = 1;
	while(flag) {
		printf("0 - wyjscie\n");
		printf("1 - Ustawienie rygla do odczytu na wybrany znak pliku\n");
		printf("2 - Ustawienie rygla do zapisu na wybrany znak pliku\n");
		printf("3 - Wyswietlenie listy zaryglowanych znakow pliku\n");
		printf("4 - Zwolnienie wybranego rygla\n");
		printf("5 - Odczyt wybranego znaku pliku\n");
		printf("6 - Zmiana wybranego znaku pliku\n");

		cin = getchar();
		char t;

		switch(cin) {
			case '0':
				flag = 0;
				break;
			case '1':
				cIndex = getIndex(cin);
				fl = getFlock(file_handler, F_RDLCK, cIndex);
				if(fcntl(file_handler, F_SETLK, &fl) == -1) {
					printf("[1] blad\n");
				} else {
					printf("[1] Rygiel ustawiony\n");
				}
				break;
			case '2':
				cIndex = getIndex(cin);
				fl = getFlock(file_handler, F_WRLCK, cIndex);
				if(fcntl(file_handler, F_SETLK, &fl) == -1) {
					printf("[2] blad\n");
				} else {
					printf("[2] Rygiel ustawiony\n");
				}
				break;
			case '3':
				printRygl(file_handler);
				break;
			case '4':
				cIndex = getIndex(cin);
				fl = getFlock(file_handler, F_UNLCK, cIndex);
				if(fcntl(file_handler, F_SETLK, &fl) == -1) {
					printf("[4] blad\n");
				} else {
					printf("[4] Rygiel zwolniony\n");
				}
				break;
			case '5':
				cIndex = getIndex(cin);
				lseek(file_handler, cIndex, SEEK_SET);
				fl = getFlock(file_handler, F_WRLCK, cIndex);
				if(fcntl(file_handler, F_GETLK, &fl) == -1) {
					printf("[5] blad!");
				} else {
					if(fl.l_type != F_RDLCK) {
						if(read(file_handler, &t, 1) == -1) {
							printf("[5] Blad odczytu!\n");
						} else {
							printf("[5] Odczyt: %c\n", t);
						}
					} else {
						printf("[5] Nie mozna odczytac znaku!\n");
					}
				}
				break;
			case '6':
				cIndex = getIndex(cin);
				lseek(file_handler, cIndex, SEEK_SET);
				fl = getFlock(file_handler, F_WRLCK, cIndex);
				if(fcntl(file_handler, F_GETLK, &fl) == -1) {
					printf("[6] blad!\n");
				} else {
					getchar();
					printf("Wprowadz znak: ");
					t = getchar();
					if(fl.l_type != F_WRLCK) {
						if(write(file_handler, &t, 1) == -1) {
							printf("[6] Blad zapisu!\n");
						} else {
							printf("[6] Zapisano pomyslnie!\n");
						}
					} else {
						printf("[6] Nie mozna zapisac znaku!\n");
					}
				}
				break;
		}

		// wait for user
		if(flag) {
			printf("\n...");
			getchar();
			getchar();
			system("clear");
		}
	}

	if(close(file_handler) == -1) {
		input_error("cannot close the file");
	}

}

long int getIndex(char c) {
	printf("[%c] Podaj indeks znaku: ", c);
	long int i;
	scanf("%ld", &i);
	if(i < 0) {
		printf("blad - podaj poprawny indeks\n");
		i = getIndex(c);
	}
	return i;
}

void input_error(char const* msg) {
	printf("error: ");
	printf("%s", msg);
	printf("\n");
	exit(-1);
}

struct flock getFlock(int file_handler, short lType, off_t cIndex) {
	struct flock fl;
	fl.l_type = lType;
	fl.l_whence = SEEK_SET;
	fl.l_start = cIndex;
	fl.l_len = 1;
	return fl;
}

void printRygl(int file_handler) {
	long i;
	long sizeFile = lseek(file_handler, 0, SEEK_END);
	struct flock fl;
	int count = 0;

	for(i = 0; i < sizeFile; i++) {
		fl = getFlock(file_handler, F_WRLCK, i);

		if(fcntl(file_handler, F_GETLK, &fl) == -1) {
			perror("[3] blad\n");
		}
		if(fl.l_type == F_RDLCK) {
			printf("Rygiel do odczytu. Indeks znaku: %ld\t PID: %d\n", i, fl.l_pid);
			count++;
		} else if(fl.l_type == F_WRLCK) {
			printf("Rygiel do zapisu. Indeks znaku: %ld\t PID: %d\n", i, fl.l_pid);
			count++;
		}
	}

	printf("[3] Znaleziono '%d' rygli.\n", count);
}

