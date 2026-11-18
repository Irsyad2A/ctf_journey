#include <stdio.h>
#include <string.h>
#include <stdlib.h>

void win() {
    char flag[64];
    FILE *f = fopen("/flag.txt", "r");
    if (!f) {
        printf("No flag here. Contact admin.\n");
        return;
    }
    fgets(flag, sizeof(flag), f);
    fclose(f);
    printf("🎉 %s\n", flag);
}

void vuln() {
    char buf[64];
    printf("TryHards says: ");
    gets(buf);
    printf("You said: %s\n", buf);
}

int main() {
    setbuf(stdout, NULL);
    vuln();
    return 0;
}
