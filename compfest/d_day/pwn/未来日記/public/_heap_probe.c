
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

int main(void) {
    setvbuf(stdout, NULL, _IONBF, 0);

    void *a = malloc(0x410);
    printf("%p\n", a);

    pause();
    return 0;
}
