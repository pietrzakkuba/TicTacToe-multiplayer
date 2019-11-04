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

bool leave(int csd, int csd_support, int game_id)
{
    char client[3];
    read(csd_support, client, sizeof(client));
    if (strcmp(client, "13") == 0)
    {
        printf("Game ID: %d\tFirst player has left!\n", game_id);

        // il = i have exited (write below needed to close threads on client's side)
        write(csd, "il", sizeof("il"));
        return true;
    }
    return false;
}