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

// void managing a game
void tournament(int game_id, int player_1, int player_2)
{
    // each space has its number, shown below
    // |01|02|03|
    // |04|05|06|
    // |07|08|09|
    // additional numbers 11 - your turn, 12 - opponents turn
    // player_1 is X, player_2 is O, X always goes first
    char massage_to_player_1[3];
    char massage_to_player_2[3];
    char massage_from_player_1[3];
    char massage_from_player_2[3];

    bool game_finished = false;
    bool player_1_turn = true;

    while (!game_finished)
    {
        // printf("XDD\n");
        if (player_1_turn)
        {
            printf("Game ID: %d\tPlayer 1 Turn\n", game_id);
            strcpy(massage_to_player_1, "11");
            strcpy(massage_to_player_2, "12");

            write(player_1, massage_to_player_1, sizeof(massage_to_player_1));
            write(player_2, massage_to_player_2, sizeof(massage_to_player_2));

            // waiting for respond from player 1
            read(player_1, massage_from_player_1, sizeof(massage_from_player_1));

            // sending the respond to player 2
            strcpy(massage_to_player_2, massage_from_player_1);
            write(player_2, massage_to_player_2, sizeof(massage_to_player_2));
            printf("Game ID: %d\tPlayer 1 Move: %s\n", game_id, massage_from_player_1);

            player_1_turn = false;
        }
        else
        {
            printf("Game ID: %d\tPlayer 2 Turn\n", game_id);
            strcpy(massage_to_player_1, "12");
            strcpy(massage_to_player_2, "11");

            write(player_1, massage_to_player_1, sizeof(massage_to_player_1));
            write(player_2, massage_to_player_2, sizeof(massage_to_player_2));

            // waiting for respond from player 2
            read(player_2, massage_from_player_2, sizeof(massage_from_player_2));

            // sending the respond to player 1
            strcpy(massage_to_player_1, massage_from_player_2);
            write(player_1, massage_to_player_1, sizeof(massage_to_player_1));
            printf("Game ID: %d\tPlayer 2 Move: %s\n", game_id, massage_from_player_2);

            player_1_turn = true;
        }
    }
}
