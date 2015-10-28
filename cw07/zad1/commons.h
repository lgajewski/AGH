#ifndef COMMONS_H
#define COMMONS_H

#define SERV_PATH "serv/buffer"
#define FTOK_ID 1

#define BUFF_SIZE 20
#define MEM_SIZE BUFF_SIZE * sizeof(int) + 2 * sizeof(int)


#define S_EMPTY 0
#define S_FULL 1
#define S_ACCESS 2

#define err_and_clean(X) if(X){printf("%s\n", strerror(errno));clean(0);exit(-1);}
#define err(X) if(X){printf("%s\n", strerror(errno));exit(-1);}

#endif
