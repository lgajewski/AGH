#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <string.h>

static char* file_name;

static int record_length;
static int record_no;

char getRandomChar();
void getRandomRecord(char* record);
void generate();
void input_error(char* msg);

int main(int argc, char *argv[]) {
	srand(time(NULL));

	if(argc == 2 && strcmp(argv[1], "--help") == 0) {
		printf("./prog file record_length number_of_records\n");
	} else if(argc != 4) {
		printf("use '--help' for usage\n");
	} else {
		file_name = argv[1];
		if(sscanf(argv[2], "%d", &record_length) != 1) input_error("record length must be an integer");
		if(sscanf(argv[3], "%d", &record_no) != 1)  input_error("number of records must be an integer");
		if(record_length <= 0) input_error("length is under 0");
		if(record_no <= 0) input_error("number of records is under 0");
		
		generate();
	}

	return 0;

}

void input_error(char* msg) {
	printf("error: ");
	printf("%s", msg);
	printf("\n");
	exit(-1);
}

void generate() {
	FILE *fp = fopen(file_name, "wb");

	int length = record_length + 2; 	// 2 more for new line chars

	char** records = (char**) calloc(record_no, sizeof(char*));
	int i;
	for (i=0; i<record_no; i++) {
		// one more character for new line
		records[i] = (char*) calloc(length, sizeof(char));
		getRandomRecord(records[i]);
		records[i][length-2] = '\r';
		records[i][length-1] = '\n';
		fwrite(records[i], length, sizeof(char), fp);
	}

	fclose(fp);
}

void getRandomRecord(char* record) {
	int i;
	for(i=0; i<record_length; i++) {
		record[i] = getRandomChar();
	}
}

char getRandomChar() {
	char a = 'A' + (rand() % 26);
	return a;
}
