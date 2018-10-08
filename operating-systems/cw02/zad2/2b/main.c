#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <sys/stat.h>
#include <ftw.h>
#include <time.h>

static char *access_rights;

void print_stats(char * path){ //prints stats for file

    struct stat statbuf; //gets stats
    char * type; //type of file

    if (lstat(path, &statbuf) == -1)
    {
        printf("Error occoured attempting to stat %s\n", path);
        exit(0);
    }

    if (S_ISREG(statbuf.st_mode)) type = "plain file";
    if (S_ISDIR(statbuf.st_mode)) type = "directory";
    if (S_ISCHR(statbuf.st_mode)) type = "character special";
    if (S_ISBLK(statbuf.st_mode)) type = "block special";
    if (S_ISLNK(statbuf.st_mode)) type = "symbolic link";

    printf("Type of file: %s\n", type);

    printf("File size: %d bytes\n", statbuf.st_size);

    printf("Last modified: %s", ctime(&statbuf.st_mtime));
}

char * get_access_rights(char * path){ //gets access rights for file

    struct stat statbuf;
    lstat(path, &statbuf);
    char * access_rights;
    access_rights = (char *) malloc(10 * sizeof(char));
    strcpy(access_rights,"-");
    (statbuf.st_mode & S_IRUSR) ? strcat(access_rights,"r") : strcat(access_rights,"-");
    (statbuf.st_mode & S_IWUSR) ? strcat(access_rights,"w") : strcat(access_rights,"-");
    (statbuf.st_mode & S_IXUSR) ? strcat(access_rights,"x") : strcat(access_rights,"-");
    (statbuf.st_mode & S_IRGRP) ? strcat(access_rights,"r") : strcat(access_rights,"-");
    (statbuf.st_mode & S_IWGRP) ? strcat(access_rights,"w") : strcat(access_rights,"-");
    (statbuf.st_mode & S_IXGRP) ? strcat(access_rights,"x") : strcat(access_rights,"-");
    (statbuf.st_mode & S_IROTH) ? strcat(access_rights,"r") : strcat(access_rights,"-");
    (statbuf.st_mode & S_IWOTH) ? strcat(access_rights,"w") : strcat(access_rights,"-");
    (statbuf.st_mode & S_IXOTH) ? strcat(access_rights,"x") : strcat(access_rights,"-");
    
    return access_rights;
}


int find(char *path) {

    if(strcmp(access_rights,get_access_rights(path)) == 0) { //if access rights are the same as pattern
        printf("%s\n", path); //prints name of file
        print_stats(path); //prints stats for file
        printf("Access rights for file: %s\n", get_access_rights(path)); //prints access rights
        printf("\n");
    }  

    return 0;        
}


int main(int argc, char *argv[]){
    //parses command line
    if(argc != 3){
        printf("too many or too few arguments\n");
        exit(0);
    }
    char *path = argv[1];
    printf("Directory name: %s\n", path);
    access_rights = argv[2];
    printf("Access rights pattern: %s\n", access_rights);

    //searches through the directory tree
    ftw(path, find, 1);
    printf("\n");

    exit(0);
}