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
#include <stdbool.h>

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

    char message_from_server[3];
    char message_to_server[3];

    read(connection_socket_descriptor, message_from_server, sizeof(message_from_server));
    if (strcmp(message_from_server, "sr") == 0)
        printf("Second player ready!\n");

    while (1)
    {
        read(connection_socket_descriptor, message_from_server, sizeof(message_from_server));
        if (strcmp(message_from_server, "11") == 0)
        {
            printf("YOUR TURN:\n");
            scanf("%s", message_to_server);
            write(connection_socket_descriptor, message_to_server, sizeof(message_to_server));
        }
        else if (strcmp(message_from_server, "12") == 0)
        {
            printf("OPPONENT'S TURN\n");
            read(connection_socket_descriptor, message_from_server, sizeof(message_from_server));
            printf("%s\n", message_from_server);
        }
    }

    close(connection_socket_descriptor);
    return 0;
}