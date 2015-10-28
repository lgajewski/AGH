#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <dirent.h>
#include <time.h>
#include <linux/limits.h>
#include <errno.h>
#include <ftw.h>

static char* access_rights;

void input_error(char const* msg);
int list(const char *name, const struct stat *status, int type);
char* get_path(char* path, char* fileName);
char* get_acc_rights(struct stat s);


int main(int argc, char *argv[]) {

	if(argc == 2 && strcmp(argv[1], "--help") == 0) {
		printf("example: ./prog ~/Dokumenty/tmp rwx-wx-w-\n");
	} else if(argc != 3) {
		printf("use '--help' for usage\n");
	} else {
		char* path = argv[1];
		access_rights= argv[2];
		if(access(path, F_OK) == -1) input_error("file doesn't exist");

		printf("dirtree\n");
		printf("  > path: %s\n", path);
		printf("  > access_rights: %s\n\n", access_rights);

		ftw(path, list, 1);

	}


	return 0;

}


int list(const char *name, const struct stat *status, int type) {

	if(type == FTW_F && strcmp(get_acc_rights(*status), access_rights) == 0) {
		struct stat s;
		s = *status;
		printf(" file: [%s]\n", name);
		printf("     > size: %d | last-modified: %s\n", (int)(s.st_size), ctime(&s.st_mtime));
	}
	
	// access rights match with file
	return 0;
}

char* get_path(char* path, char* fileName) {
	int pathLength = strlen(path) + 1 + strlen(fileName);
	char* result = (char*) calloc(pathLength, sizeof(char));
	strcat(result, path);
	strcat(result, "/");
	strcat(result, fileName);

	return result;
}

char* get_acc_rights(struct stat s) {
	char* file_acc_rights = (char*) calloc(9, sizeof(char));

	strcpy(file_acc_rights, "---------");

    if (s.st_mode & S_IRUSR) file_acc_rights[0] = 'r';
    if (s.st_mode & S_IWUSR) file_acc_rights[1] = 'w';
    if (s.st_mode & S_IXUSR) file_acc_rights[2] = 'x';
    if (s.st_mode & S_IRGRP) file_acc_rights[3] = 'r';
    if (s.st_mode & S_IWGRP) file_acc_rights[4] = 'w';
    if (s.st_mode & S_IXGRP) file_acc_rights[5] = 'x';
    if (s.st_mode & S_IROTH) file_acc_rights[6] = 'r';
    if (s.st_mode & S_IWOTH) file_acc_rights[7] = 'w';
    if (s.st_mode & S_IXOTH) file_acc_rights[8] = 'x';

	return file_acc_rights;
}


void input_error(char const* msg) {
	printf("error: ");
	printf("%s", msg);
	printf("\n");
	exit(-1);
}
