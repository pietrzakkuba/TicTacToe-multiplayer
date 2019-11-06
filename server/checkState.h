#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>

char checkState(char values[9])
{
    if (values[0] == values[1] && values[0] == values[2] && values[0] != 'e')
        return values[0];
    if (values[3] == values[4] && values[3] == values[5] && values[3] != 'e')
        return values[3];
    if (values[6] == values[7] && values[6] == values[8] && values[6] != 'e')
        return values[6];
    if (values[0] == values[3] && values[0] == values[6] && values[0] != 'e')
        return values[0];
    if (values[1] == values[4] && values[1] == values[7] && values[1] != 'e')
        return values[1];
    if (values[2] == values[5] && values[2] == values[8] && values[2] != 'e')
        return values[2];
    if (values[0] == values[4] && values[0] == values[8] && values[0] != 'e')
        return values[0];
    if (values[2] == values[4] && values[2] == values[6] && values[2] != 'e')
        return values[2];
    bool draw_flag = true;
    for (int i = 0; i < 9; i++)
        if (values[i] == 'e')
        {
            draw_flag = false;
            break;
        }
    if (draw_flag)
        return 'd';
    return 'n';
}
