#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/wait.h>
#include <dirent.h>
#include <time.h>
#include <linux/limits.h>
#include <errno.h>
#include <signal.h>

// booleans
int b_sleep = 0;
int b_info = 0;

// const
const int SLEEP_TIME = 3;

// global
char** glb_argv;

void input_error(char const* msg);
int list(char* path);
int validate(int argc, char *argv[]);

int main(int argc, char *argv[]) {
	glb_argv = argv;
	int regularFiles = -1;

	if(validate(argc, argv)) {
		char* path = argv[1];

		regularFiles = list(path);
	}

	return regularFiles;

}

int validate(int argc, char *argv[]) {
	if(argc >= 2 && argc <= 4 && 0 == access(argv[1], F_OK)) {
		int i;
		for(i=0; i<argc; i++) {
			if(strcmp(argv[i], "-w") == 0) b_sleep = 1;
			else if(strcmp(argv[i], "-i") == 0) b_info = 1;
		}
		return 1;
	} else {
		printf("usage: %s [path_to_dir] [-w] [-i]\n", argv[0]);
		return 0;
	}
}


int list(char* path) {

	struct stat s;
	struct dirent* file;
	char filePath[PATH_MAX];
	char* fileName;
	int length = 0;
	int myRegFiles = 0;
	int childRegFiles = 0;

	DIR* root = opendir(path);

	if(!root) {
       fprintf(stderr, "Cannot open directory '%s': %s\n", path, strerror (errno));
    }

	// listing files
	while((file = readdir(root)) != NULL) {
		fileName = file->d_name;
		length = snprintf(filePath, PATH_MAX, "%s/%s", path, fileName);

		if(length >= PATH_MAX) input_error("path length too long");


		// retrieve statisticts for file
		lstat(filePath, &s);

		// check file type
		if(S_ISDIR(s.st_mode)) {
			if(strcmp(fileName, "..") != 0 && strcmp(fileName, ".") != 0) {
				int pathLength = 0;
				char dirPath[PATH_MAX];

				pathLength = snprintf(dirPath, PATH_MAX, "%s/%s", path, fileName);
				if(pathLength >= PATH_MAX) input_error("path length too long");

				dirPath[pathLength] = '\0';

				// create new process here
				pid_t pid = fork();
		        if (pid < 0) {
		            perror("Fork Failed!");
		            exit(1);
		        } else if (pid == 0) {
		            // child process
	        		glb_argv[1] = dirPath;

					int dirRegFiles = execv(glb_argv[0], glb_argv);

		            // int dirRegFiles = list(dirPath);

		            _exit(dirRegFiles);
		        } else {
		            // parent process
		        	int status = 0;

		            
		            // wait until child exits, NULL - no status info
		            waitpid(pid, &status, 0);
		            childRegFiles += WEXITSTATUS(status);

		        	// sleep feature
		            if(b_sleep) sleep(SLEEP_TIME);

		        }
			}
		} else if(S_ISREG(s.st_mode)) {
			// printf("\t> file: %s/%s\n", path, fileName);
			myRegFiles++;
		}

	}

	closedir(root);

    // info feature
    if(b_info) {
    	printf("[child_pid - %d] %s\n", getpid(), path);
    	printf("\t\t > my_files: %d\n", myRegFiles);
    	printf("\t\t > child_files %d\n", childRegFiles);
    }

	return myRegFiles + childRegFiles;

}

void input_error(char const* msg) {
	printf("error: ");
	printf("%s", msg);
	printf("\n");
	exit(-1);
}
