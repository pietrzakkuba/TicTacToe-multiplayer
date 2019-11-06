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

#include "checkState.h"


// void managing a game
void tournament(int game_id, int player_1, int player_2, int player_1_support, int player_2_support)
{

    // each space has its number, shown below
    // |x1|x2|x3|   x = 0 -> game continues
    // |x4|x5|x6|   x = 2 -> game is over
    // |x7|x8|x9|
    // additional numbers 11 - your turn, 12 - opponents turn
    // player_1 is X, player_2 is O, X always goes first
    char massage_to_player_1[3];
    char massage_to_player_2[3];
    char massage_from_player_1[3];
    char massage_from_player_2[3];

    bool player_1_turn = true;
    bool game_finished = false;

    char values[9];
    char check;
    char checktoSend[3];
    for (int i = 0; i < 9; i++)
    {
        values[i] = 'e'; // empty
    }

    int read_result;

    // game loop
    while (!game_finished)
    {
        if (player_1_turn)
        {
            printf("Game ID: %d\tPlayer's 1 Turn\n", game_id);
            strcpy(massage_to_player_1, "11");
            strcpy(massage_to_player_2, "12");

            write(player_1, massage_to_player_1, sizeof(massage_to_player_1));
            write(player_2, massage_to_player_2, sizeof(massage_to_player_2));

            // waiting for respond from player 1
            read_result = read(player_1, massage_from_player_1, sizeof(massage_from_player_1));
            if (read_result <= 0)
            {
                game_finished = true;
                printf("Game ID: %d\tPlayer 1 has left\n", game_id);
            }
            else
            {
                values[atoi(massage_from_player_1) - 1] = 'X';
            }
            //checking state of the game and sending them to a client
            check = checkState(values);
            checktoSend[0] = check;
            checktoSend[1] = check;
            checktoSend[2] = '\0';
            write(player_1, checktoSend, sizeof(checktoSend));

            // sending the respond to player 2
            strcpy(massage_to_player_2, massage_from_player_1);
            write(player_2, massage_to_player_2, sizeof(massage_to_player_2));

            //sending the checkstate to a second client
            write(player_2, checktoSend, sizeof(checktoSend));

            printf("Game ID: %d\tPlayer's 1 Move: %s\n", game_id, massage_from_player_1);

            player_1_turn = false;
            if (check == 'd' || check == 'X' || check == 'O')
            {
                game_finished = true;
            }
        }
        else if (!game_finished)
        {
            printf("Game ID: %d\tPlayer's 2 Turn\n", game_id);
            strcpy(massage_to_player_1, "12");
            strcpy(massage_to_player_2, "11");

            write(player_1, massage_to_player_1, sizeof(massage_to_player_1));
            write(player_2, massage_to_player_2, sizeof(massage_to_player_2));

            // waiting for respond from player 2
            read_result = read(player_2, massage_from_player_2, sizeof(massage_from_player_2));
            if (read_result < 0)
            {
                game_finished = true;
                printf("Game ID: %d\tPlayer 2 has left\n", game_id);
            }
            else
            {
                values[atoi(massage_from_player_2) - 1] = 'O';
            }
            //checking state of the game and sending them to a client
            check = checkState(values);
            checktoSend[0] = check;
            checktoSend[1] = check;
            checktoSend[2] = '\0';
            write(player_2, checktoSend, sizeof(checktoSend));

            // sending the respond to player 1
            strcpy(massage_to_player_1, massage_from_player_2);
            write(player_1, massage_to_player_1, sizeof(massage_to_player_1));

            //sending the checkstate to a second client
            write(player_1, checktoSend, sizeof(checktoSend));

            printf("Game ID: %d\tPlayer's 2 Move: %s\n", game_id, massage_from_player_2);

            player_1_turn = true;
            if (check == 'd' || check == 'X' || check == 'O')
            {
                game_finished = true;
            }
        }
    }
    printf("Game ID: %d\tGame over\n", game_id);
}
