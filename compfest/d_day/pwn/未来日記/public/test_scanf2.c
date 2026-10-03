#include <stdio.h>
int main() {
    int choice = -1;
    while(1) {
        printf(">> ");
        if (scanf("%d", &choice) <= 0) {
            printf("scanf failed! choice = %d\n", choice);
            // clear stdin so we don't infinite loop in this test
            char c = getchar();
            printf("cleared char: %02x\n", (unsigned char)c);
        } else {
            printf("choice = %d\n", choice);
        }
    }
}
