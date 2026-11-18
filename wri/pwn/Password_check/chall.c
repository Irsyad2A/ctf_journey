#include <stdint.h>
#include <stdio.h>
#include <string.h>
#include <sys/random.h>

typedef struct {
    char password[32];
    uint32_t authenticated;
} Login;

static void show_flag(void) {
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
    Login login = {0};
    unsigned char random_bytes[24];
    char expected_password[50];

    setvbuf(stdout, NULL, _IONBF, 0);
    puts("Password Check");
    printf("Password: ");

    if (getrandom(random_bytes, sizeof(random_bytes), 0) != sizeof(random_bytes)) {
        puts("Password generator is unavailable.");
        return 1;
    }

    for (size_t index = 0; index < sizeof(random_bytes); index++) {
        snprintf(expected_password + index * 2, 3, "%02x", random_bytes[index]);
    }
    expected_password[48] = '\n';
    expected_password[49] = '\0';

    if (fgets(login.password, 96, stdin) == NULL) {
        return 1;
    }

    if (strcmp(login.password, expected_password) == 0) {
        login.authenticated = 1;
    }

    if (login.authenticated != 0) {
        puts("Access granted.");
        show_flag();
    } else {
        puts("Access denied.");
    }

    return 0;
}
