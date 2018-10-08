#include <stdlib.h>
#include <stdio.h> 
#include <string.h>
#include <unistd.h>
#include <sys/types.h>
#include <wait.h>

/** FILE DESCRIPTORS
 * 0	Standard input	STDIN_FILENO	stdin
 * 1	Standard output	STDOUT_FILENO	stdout
 * 2	Standard error	STDERR_FILENO	stderr
 */

int fork_pipes (char *cmd1[], char *cmd2[], char *cmd3[]) {
  int fd1[2], fd2[2];
  int pid;

  pipe(fd1);		// create first pipe
  if((pid = fork()) == 0) {		// create first process
  	dup2(fd1[1], 1);		// change standard file descriptor (1) to the input
	  execvp(cmd1[0], cmd1);	// exec cmd1
  }
  close(fd1[1]);		// close input

  pipe(fd2);		// create second pipe
  if((pid = fork()) == 0) {
  	dup2(fd1[0], 0);	// change standard file descriptor (0) to the output of first pipe
  	dup2(fd2[1], 1);	// change standard file descriptor (1) to the input of second pipe
  	execvp(cmd2[0], cmd2);
  }
  close(fd2[1]);	// close input

  dup2(fd2[0], 0);	// change standard file descriptor (0) to the input

  return execvp(cmd3[0], cmd3);
}


int main(int argc, char *argv[]) {
	
  char *cmd1[] = { "/bin/ls", "-l", 0 };
  char *cmd2[] = { "/bin/grep", "^d", 0 };
	char *cmd3[] = { "/bin/wc", "-l", 0 };

  fork_pipes (cmd1, cmd2, cmd3);
	return 0;

}

