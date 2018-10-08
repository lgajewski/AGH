#include <stdlib.h>
#include <stdio.h> 
#include <string.h>
#include <unistd.h>
#include <sys/types.h>
#include <wait.h>
#include <fcntl.h>


int main(int argc, char *argv[]) {

	char buffer[128];
	 
	freopen("folders.txt", "w+", stdout);

  	FILE *pipe1 = popen("ls -l", "r");
  	FILE *pipe2 = popen("grep ^d", "w");


 	while(fgets(buffer, 128, pipe1)) {
        fputs(buffer, pipe2);
 	}

 	pclose(pipe1);
 	pclose(pipe2);

  	return 0;
}

