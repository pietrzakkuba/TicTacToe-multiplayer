// this client is here only because of server's development, won't be a part of final product
#include <sys/types.h>
#include <sys/wait.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <netdb.h>
#include <signal.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <pthread.h>

#define SERVER_PORT 1234

int main(int argc, char *argv[])
{
    int connection_socket_descriptor;
    struct sockaddr_in server_address;

    connection_socket_descriptor = socket(AF_INET, SOCK_STREAM, 0);

    memset(&server_address, 0, sizeof(struct sockaddr));
    server_address.sin_family = AF_INET;
    server_address.sin_port = htons(SERVER_PORT);
    inet_pton(AF_INET, "127.0.0.1", &(server_address.sin_addr));

    connect(connection_socket_descriptor, (struct sockaddr *)&server_address, sizeof(struct sockaddr));
    printf("Waiting for the second player\n");
    char buf[40];
    read(connection_socket_descriptor, buf, sizeof(buf));
    printf("%s\n", buf);

    close(connection_socket_descriptor);
    return 0;
}