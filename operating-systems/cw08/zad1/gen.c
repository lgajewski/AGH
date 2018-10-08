#include <stdlib.h>
#include <stdio.h>
#include <strings.h>
#include <string.h>


int main(int argc, char* argv[]) {
	if(argc != 4) {
		printf("usage: %s [file_name] [records_no] [record_length]\n", argv[0]);
		return 0;
	}

	char* file_name = argv[1];
	int records_no = atoi(argv[2]);
	int record_length = atoi(argv[3]);

	// open file
	FILE *fp;

	fp = fopen(file_name, "w+");

	// generate
	int i,j;
	char rand_char;
	char record[record_length];
	for(i=1; i<=records_no; i++) {
		bzero(record, record_length);
		int id_length = snprintf(record, record_length, "%d,", i);

		record[id_length] = 'A' + (random() % 26);
		for(j=id_length + 1; j<record_length; j++) {
			rand_char = (random() % 6) ? ('a' + (random() % 26)) : ' ';
			record[j] = rand_char;
		}
		record[record_length-2] = 'a' + (random() % 26);
		record[record_length-1] = '\0';
		printf("%s\n", record);
		fprintf(fp, record);
		fprintf(fp, "\n");
	}

	fclose(fp);
	return 0;
}