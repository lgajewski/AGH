#include <stdio.h>
#include <stdlib.h>

int main()
{
    int i, count = 300;

    float f_x;
    double d_x;

    f_x = 0.01;
    d_x = 0.01;

    for(i = 1; i<=count; i++) {
        printf("%d: float: %f \t double: %f\n", i, f_x, d_x);
        f_x = 4 * f_x - 3.0 * f_x * f_x;
        d_x = 4 * d_x - 3.0 * d_x * d_x;
    }

    return 0;
}