#include <string.h>
#include <jni.h>
#include <assert.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <time.h>
#include <android/log.h>

// TODO make buff dynamic -> array of const length strings
#define MAX_BUFF 10*1024
#define TIME_BUFF 9

const char* HOST;
int PORT;

char error_msg[MAX_BUFF];

void logErr(const char *msg) {
    sprintf(error_msg, "{ \"type\":\"q_failure\", \"msg\": \"%s\" }" , msg);
}

void getTime(char timeString[TIME_BUFF]) {
    time_t current_time;
    struct tm * time_info;

    time(&current_time);
    time_info = localtime(&current_time);

    sprintf(timeString, "%02d:%02d:%02d", time_info->tm_hour, time_info->tm_min, time_info->tm_sec);

}

int connectServerSocket() {
    int sockfd;
    struct sockaddr_in serv_addr;
    struct hostent *server;

    sockfd = socket(AF_INET, SOCK_STREAM, 0);
    if (sockfd < 0) {
        logErr("cannot open socket");
        return -1;
    }

    server = gethostbyname(HOST);

    if (server == NULL) {
        logErr("unknown host");
        return -1;
    }

    bzero((char *) &serv_addr, sizeof(serv_addr));
    serv_addr.sin_family = AF_INET;
    bcopy((char *) server->h_addr, (char *) &serv_addr.sin_addr.s_addr, server->h_length);

    serv_addr.sin_port = htons(PORT);

    if (connect(sockfd, (struct sockaddr *) &serv_addr, sizeof(serv_addr)) < 0) {
        logErr("cannot connect to the host");
        return -1;
    }

    return sockfd;
}


JNIEXPORT jstring JNICALL
Java_pl_gajewski_chatapp_connection_SocketHandler_query
    (JNIEnv* env, jobject object, jstring hostJ, jint portJ, jstring queryJ) {

    // convert Java string to UTF-8
    HOST = (*env)->GetStringUTFChars(env, hostJ, NULL);
    assert(NULL != host);

    const char *query = (*env)->GetStringUTFChars(env, queryJ, NULL);
    assert(NULL != query);

    PORT = portJ;
    assert(PORT > 0);

    // initialize
    int n;
    char buffer[MAX_BUFF];
    bzero(buffer, MAX_BUFF);

    // connect
    int sockfd = connectServerSocket();
    if(sockfd < 0) return (*env)->NewStringUTF(env, error_msg);

    // write to socket
    n = write(sockfd, query, strlen(query));
    if (n < 0) {
        close(sockfd);
        logErr("cannot write to the socket");
        return (*env)->NewStringUTF(env, error_msg);
    }

    // read response from server
    n = read(sockfd, buffer, MAX_BUFF);

    if (n < 0) {
        close(sockfd);
        logErr("cannot read from the socket");
        return (*env)->NewStringUTF(env, error_msg);
    }
    close(sockfd);

    // release strings
    (*env)->ReleaseStringUTFChars(env, queryJ, query);
    (*env)->ReleaseStringUTFChars(env, hostJ, HOST);

    // return response
    return (*env)->NewStringUTF(env, buffer);
}
