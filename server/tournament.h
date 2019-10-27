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

//void managing a game
void tournament(int game_id, int player_1, int player_2)
{
    printf("Game ID: %d\tConnection Socket Descriptors: %d %d\n", game_id, player_1, player_2);
    int space_number;
    // each space has its number, shown below
    // |1|2|3|
    // |4|5|6|
    // |7|8|9|
    //player_1 is X, player_2 is O, X always goes first
    bool game_finished = false;
    bool player_1_turn = true;
    while (!game_finished)
    {
        if (player_1_turn)
        {
            //sending messages to players, 11 - your turn, 12 - opponent's turn
            write(player_1, 11, sizeof(int)); 
            write(player_2, 12, sizeof(int));
            //waiting for respond from player 1
            read(player_1, &space_number, sizeof(int));
            //sending the respond to player 2
            write(player_2, &space_number, sizeof(int));
            player_1_turn = false;
        }
        else
        {
            write(player_1, 12, sizeof(int));
            write(player_2, 11, sizeof(int));
            //waiting for respond from player 2
            read(player_2, &space_number, sizeof(int));
            //sending the respond to player 2
            write(player_1, &space_number, sizeof(int));
            player_1_turn = true;
        }
    }
}