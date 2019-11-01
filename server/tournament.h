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
    // |x1|x2|x3|   x = 0 -> game continues
    // |x4|x5|x6|   x = 2 -> game is over
    // |x7|x8|x9|
    // additional numbers 11 - your turn, 12 - opponents turn
    // player_1 is X, player_2 is O, X always goes first
    char massage_to_player_1[3];
    char massage_to_player_2[3];
    char massage_from_player_1[3];
    char massage_from_player_2[3];

    bool game_finished_player_1 = false;
    bool game_finished_player_2 = false;
    bool player_1_turn = true;

    // game loop; wants confirmation from both players that game is over to stop
    while (!game_finished_player_1 || !game_finished_player_2)
    {
        // printf("XDD\n");
        if (player_1_turn)
        {
            printf("Game ID: %d\tPlayer's 1 Turn\n", game_id);
            strcpy(massage_to_player_1, "11");
            strcpy(massage_to_player_2, "12");

            write(player_1, massage_to_player_1, sizeof(massage_to_player_1));
            write(player_2, massage_to_player_2, sizeof(massage_to_player_2));

            // waiting for respond from player 1
            printf("XD1\n");
            read(player_1, massage_from_player_1, sizeof(massage_from_player_1));
            printf("XD2\n");
            // sending the respond to player 2
            strcpy(massage_to_player_2, massage_from_player_1);
            write(player_2, massage_to_player_2, sizeof(massage_to_player_2));
            printf("Game ID: %d\tPlayer's 1 Move: %s\n", game_id, massage_from_player_1);
            
            //checking whether the game is finished
            if (massage_from_player_1[0] == '2')
            {
                game_finished_player_1 = true;
                printf("Game ID: %d\tPlayer 1 has finished game\n", game_id);
            }

            player_1_turn = false;
        }
        else
        {
            printf("Game ID: %d\tPlayer's 2 Turn\n", game_id);
            strcpy(massage_to_player_1, "12");
            strcpy(massage_to_player_2, "11");

            write(player_1, massage_to_player_1, sizeof(massage_to_player_1));
            write(player_2, massage_to_player_2, sizeof(massage_to_player_2));

            // waiting for respond from player 2
            read(player_2, massage_from_player_2, sizeof(massage_from_player_2));

            // sending the respond to player 1
            strcpy(massage_to_player_1, massage_from_player_2);
            write(player_1, massage_to_player_1, sizeof(massage_to_player_1));
            printf("Game ID: %d\tPlayer's 2 Move: %s\n", game_id, massage_from_player_2);

            //checking whether the game is finished
            if (massage_from_player_2[0] == '2')
            {
                game_finished_player_2 = true;
                printf("Game ID: %d\tPlayer 2 has finished the game\n", game_id);
            }

            player_1_turn = true;
        }
    }
    printf("Game ID: %d\tGame over\n", game_id);
}
