#ifndef COMMONS_H
#define COMMONS_H

#define RECORD_SIZE 1024

#define err(X) if(X) {printf("%s\n", strerror(errno));exit(-1);}

#endif
