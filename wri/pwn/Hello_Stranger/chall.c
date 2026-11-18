#include <stdio.h>
#include <unistd.h>

void win(void) __attribute__((noinline, force_align_arg_pointer));

void win(void) {
    FILE *file = fopen("/flag.txt", "r");
    char flag[128];

    if (file == NULL || fgets(flag, sizeof(flag), file) == NULL) {
        puts("Flag is unavailable.");
        return;
    }

    puts(flag);
    fclose(file);
}

int main(void) {
    char name[64];

    setvbuf(stdout, NULL, _IONBF, 0);
    puts("Welcome to the tiny greeting service.");
    printf("Your name: ");

    if (read(STDIN_FILENO, name, 256) <= 0) {
        return 1;
    }

    puts("Nice to meet you.");
    return 0;
}
