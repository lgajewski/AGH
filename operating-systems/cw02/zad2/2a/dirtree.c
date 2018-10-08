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

typedef struct symNode symNode;

struct symNode {
	char* path;
	symNode* next;
};

static struct symNode* symList = NULL;
static char targetPath[PATH_MAX];
static int fileCount = 0;

void input_error(char const* msg);
void list(char* path, char* access_rights);
int check_acc_rights(char* filePath, char* access_rights);

void add_to_list(char* path);
int find_in_list(char* path);


int main(int argc, char *argv[]) {

	if(argc == 2 && strcmp(argv[1], "--help") == 0) {
		printf("example: ./prog ~/Dokumenty/tmp rwx-wx-w-\n");
	} else if(argc != 3) {
		printf("use '--help' for usage\n");
	} else {
		char* path = argv[1];
		char* access_rights= argv[2];
		if(access(path, F_OK) == -1) input_error("file doesn't exist");

		printf("dirtree\n");
		printf("  > path: %s\n", path);
		printf("  > access_rights: %s\n\n", access_rights);

		list(path, access_rights);

		printf("\n[done] found %d files.\n\n", fileCount);

	}


	return 0;

}


void list(char* path, char* access_rights) {

	struct stat s;
	struct dirent* file;
	char filePath[PATH_MAX];
	char* fileName;
	int length;

	DIR* root = opendir(path);

	if(!root) {
       fprintf(stderr, "Cannot open directory '%s': %s\n", path, strerror (errno));
    }

	// listing files
	while((file = readdir(root)) != NULL) {
		fileName = file->d_name;
		length = snprintf(filePath, PATH_MAX, "%s/%s", path, fileName);

		// retrieve statisticts for file
		lstat(filePath, &s);

		// check if fily is symbolic link
		if(S_ISLNK(s.st_mode)) {
			// get redirect path
			length = readlink(filePath, targetPath, sizeof(targetPath));
			targetPath[length] = '\0';

			// get absolute path if necessary
			if(targetPath[0] != '/') {
				length = snprintf(filePath, PATH_MAX, "%s/%s", path, targetPath);
			} else {
				strcpy(filePath, targetPath);
			}
			filePath[length] = '\0';

			if(find_in_list(filePath) == 0) {
				add_to_list(filePath);

				list(filePath, access_rights);
			}

		} else if(S_ISDIR(s.st_mode)) {
			if(strcmp(fileName, "..") != 0 && strcmp(fileName, ".") != 0) {
				int pathLength;
				char dirPath[PATH_MAX];


				pathLength = snprintf(dirPath, PATH_MAX, "%s/%s", path, fileName);
				if(pathLength >= PATH_MAX) {
					printf("Path length is too long.\n");
					exit(EXIT_FAILURE);
				}

				dirPath[pathLength] = '\0';

				if(find_in_list(dirPath) == 0) {
					add_to_list(dirPath);

					list(dirPath, access_rights);
				}
			}
		} else if(S_ISREG(s.st_mode)) {
			if(check_acc_rights(filePath, access_rights) == 0 && find_in_list(filePath) == 0) {
				// access rights match with file
				filePath[length] = '\0';
				add_to_list(filePath);
				printf(" file: [%s]\n", fileName);
				printf("     > size: %d | last-modified: %s\n", (int)(s.st_size), ctime(&s.st_mtime));
				fileCount++;
			}
		}

	}

	closedir(root);

}

int check_acc_rights(char* filePath, char* access_rights) {
	char* file_acc_rights = (char*) calloc(9, sizeof(char));

	struct stat s;
	lstat(filePath, &s);

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

	return strcmp(access_rights, file_acc_rights);
}


void input_error(char const* msg) {
	printf("error: ");
	printf("%s", msg);
	printf("\n");
	exit(-1);
}


void add_to_list(char* path) {
	symNode* elem = (symNode*) calloc(1, sizeof(symNode));
	elem->path = (char*) calloc(1, strlen(path));
	strcpy(elem->path, path);
	elem->next = symList;
	symList = elem;
}


int find_in_list(char* path) {
	int exist = 0;
	symNode* findList = symList;
	while(findList != NULL && exist == 0) {
		if(strcmp(findList->path, path) == 0) {
			exist = 1;
		}
		findList = findList->next;
	}
	return exist;
}
