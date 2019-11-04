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

#include "tournament.h"
#include "leave.h"

#define SERVER_PORT 1234
#define QUEUE_SIZE 10

struct game_thread_args
{
    int game_id;
    int connection_socket_descriptor_1;
    int connection_socket_descriptor_2;
};

struct leave_thread_args
{
    int game_id;
    int connection_socket_descriptor_1;
    int connection_socket_descriptor_1_support;
};

void *handlingConnection(void *game_args)
{
    struct game_thread_args *ids = (struct game_thread_args *)game_args;
    tournament(ids->game_id, ids->connection_socket_descriptor_1, ids->connection_socket_descriptor_2);
    pthread_exit(NULL);
    return EXIT_SUCCESS;
}

bool player_one_has_exited;
void *client1Waits(void *client_1_waits_args)
{
    struct leave_thread_args *msg = (struct leave_thread_args *)client_1_waits_args;
    player_one_has_exited = leave(msg->connection_socket_descriptor_1, msg->connection_socket_descriptor_1_support, msg->game_id);
    pthread_exit(NULL);
    return EXIT_SUCCESS;
}

int main(int argc, char *argv[])
{

    // initializating new socket
    struct sockaddr_in server_address;
    memset(&server_address, 0, sizeof(struct sockaddr));
    server_address.sin_family = AF_INET;
    server_address.sin_addr.s_addr = htonl(INADDR_ANY);
    server_address.sin_port = htons(SERVER_PORT);

    // creating new socket (domain, type, 0 )
    int server_socket_descriptor;
    server_socket_descriptor = socket(AF_INET, SOCK_STREAM, 0);

    if (server_socket_descriptor < 0)
    {
        perror("Creating new socket failed");
        exit(-1);
    }

    //using socket, even if not free
    int reusing_socket_result;
    reusing_socket_result = setsockopt(server_socket_descriptor, SOL_SOCKET, SO_REUSEADDR, &(int){1}, sizeof(int));
    if (reusing_socket_result < 0)
    {
        perror("setsockopt(SO_REUSEADDR) failed");
    }

    // binding name to the socket
    int bind_result;
    bind_result = bind(server_socket_descriptor, (struct sockaddr *)&server_address, sizeof(struct sockaddr));

    if (bind_result < 0)
    {
        perror("Binding socket failed");
        exit(-1);
    }

    // preparing socket to support connections and configurating a queue
    int listen_result;
    listen_result = listen(server_socket_descriptor, QUEUE_SIZE);
    if (listen_result < 0)
    {
        perror("Setting up socket to listen failed");
        exit(-1);
    }

    // infinite loop - waiting for clients
    // each client has 2 connection socket descriptors
    // one for sending and receiving game moves
    // other one is for letting server know that client has exited
    int connection_socket_descriptor[4];
    char check0[3], check1[3], check2[3], check3[3];
    int game_thread_result;
    int game_id = 0;
    while (1)
    {
        player_one_has_exited = false;
        game_id++;
        printf("Game ID: %d\tGame initialized\n", game_id);
        // waiting for client 1 to enter
        connection_socket_descriptor[0] = accept(server_socket_descriptor, NULL, NULL);
        if (connection_socket_descriptor[0] < 0)
        {
            perror("Setting up socket to listen failed");
            exit(-1);
        }
        read(connection_socket_descriptor[0], check0, sizeof(check0));
        connection_socket_descriptor[2] = accept(server_socket_descriptor, NULL, NULL);
        if (connection_socket_descriptor[2] < 0)
        {
            perror("Setting up socket to listen failed");
            exit(-1);
        }
        read(connection_socket_descriptor[2], check2, sizeof(check2));

        printf("Game ID: %d\tFirst player has joined!\tplayer's CSD: %d\n", game_id, connection_socket_descriptor[0]);

        // checking whether 1 client had left before 2 has joined
        int leave_thread_result;
        pthread_t leave_thread;

        struct leave_thread_args leave_args;
        leave_args.connection_socket_descriptor_1 = connection_socket_descriptor[0];
        leave_args.connection_socket_descriptor_1_support = connection_socket_descriptor[2];
        leave_args.game_id = game_id;

        leave_thread_result = pthread_create(&leave_thread, NULL, client1Waits, (void *)&leave_args);
        if (leave_thread_result < 0)
        {
            perror("Creating a game thread failed");
            exit(-1);
        }
        pthread_detach(leave_thread);

        // waiting for client 2 to enter

        connection_socket_descriptor[1] = accept(server_socket_descriptor, NULL, NULL);
        if (connection_socket_descriptor[1] < 0)
        {
            perror("Setting up socket to listen failed");
            exit(-1);
        }

        read(connection_socket_descriptor[1], check1, sizeof(check1));

        connection_socket_descriptor[3] = accept(server_socket_descriptor, NULL, NULL);
        if (connection_socket_descriptor[3] < 0)
        {
            perror("Setting up socket to listen failed");
            exit(-1);
        }
        read(connection_socket_descriptor[3], check3, sizeof(check3));

        // if clients joins simulataneuly
        if (strcmp(check0, check2) != 0 || strcmp(check1, check3) != 0)
        {
            int temp;
            temp = connection_socket_descriptor[1];
            connection_socket_descriptor[1] = connection_socket_descriptor[2];
            connection_socket_descriptor[2] = temp;
        }
        if (player_one_has_exited)
        {
            //sl = second player already has left befere 
            write(connection_socket_descriptor[1], "sl", sizeof("sl"));
            printf("Game ID: %d\tSecond player has joined!\n", game_id);
            printf("Game ID: %d\tMoving second player to the next game as a first player\n", game_id);
        }
        else
        {
            pthread_cancel(leave_thread);
            
            //setting up a game
            printf("Game ID: %d\tSecond player has joined!\tplayer's CSD: %d\n", game_id, connection_socket_descriptor[1]);

            //sr = second player is ready
            write(connection_socket_descriptor[0], "sr", sizeof("sr"));
            write(connection_socket_descriptor[1], "sr", sizeof("sr"));

            //creating a game thread
            pthread_t game_thread;
            struct game_thread_args game_args;
            game_args.connection_socket_descriptor_1 = connection_socket_descriptor[0];
            game_args.connection_socket_descriptor_2 = connection_socket_descriptor[1];
            game_args.game_id = game_id;
            game_thread_result = pthread_create(&game_thread, NULL, handlingConnection, (void *)&game_args);
            if (game_thread_result < 0)
            {
                perror("Creating a game thread failed");
                exit(-1);
            }
            pthread_detach(game_thread);
        }
    }

    // closing socket
    close(server_socket_descriptor);

    return 0;
}
