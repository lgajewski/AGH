#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/times.h>
#include <string.h>
#include <unistd.h>

static char* file_name;

static int record_length;

void sort_sys();
void input_error(char const* msg);
int sort_sys_iter(int fp, long int endOfFile, int length);

// clocks
static clock_t st_time;
static clock_t en_time;
static struct tms st_cpu;
static struct tms en_cpu;

void start_clock() {
    st_time = times(&st_cpu);
}

void end_clock(double *result) {
    static long clk = 0;
    clk = sysconf(_SC_CLK_TCK);
    en_time = times(&en_cpu);
    result[0] = (en_time - st_time) / (double)clk;
    result[1] = (en_cpu.tms_utime - st_cpu.tms_utime) / (double)clk;
    result[2] = (en_cpu.tms_stime - st_cpu.tms_stime) / (double)clk;
}

int main(int argc, char *argv[]) {

	if(argc == 2 && strcmp(argv[1], "--help") == 0) {
		printf("./prog file record_length\n");
	} else if(argc != 3) {
		printf("use '--help' for usage\n");
	} else {
		file_name = argv[1];
		if(access(file_name, F_OK) == -1) input_error("file doesn't exist");
		if(sscanf(argv[2], "%d", &record_length) != 1) input_error("record length must be an integer");
		if(record_length <= 0) input_error("length is under 0");

		double *p = (double*)malloc(3*sizeof(double));

		start_clock();
		sort_sys();
		end_clock(p);

   		printf("\nReal Time: %4.2f\nUser Time %4.2f\nSystem Time %4.2f\n", p[0], p[1], p[2]);

	}

	return 0;

}

void input_error(char const* msg) {
	printf("error: ");
	printf("%s", msg);
	printf("\n");
	exit(-1);
}

void sort_sys() {
	int fp = open(file_name, O_RDWR);
	// calculate file end position
	lseek(fp, 0, SEEK_END);
	long int endOfFile = lseek(fp, 0, SEEK_CUR);
	lseek(fp, 0, SEEK_SET);

	int swapped = 1;

	while(swapped) {
		lseek(fp, 0, SEEK_SET);
		swapped = sort_sys_iter(fp, endOfFile, record_length + 2);
	}

	close(fp);

}

int sort_sys_iter(int fp, long int endOfFile, int length) {
	int swap = 0;

	char* record1 = (char*) calloc(length, sizeof(char));
	char* record2 = (char*) calloc(length, sizeof(char));

	// load first record
	if(lseek(fp, 0, SEEK_CUR) < endOfFile) {
		read(fp, record1, length);
	}	

	while(lseek(fp, 0, SEEK_CUR) < endOfFile) {
		read(fp, record2, length);

		// compare and save
		if(record1[0] > record2[0]) {
			lseek(fp, -2*length, SEEK_CUR);

			write(fp, record2, length);
			write(fp, record1, length);

			swap = 1;
		} else {
			char* tmp = record1;
			record1 = record2;
			record2 = tmp;
		}

	}

	return swap;
}
