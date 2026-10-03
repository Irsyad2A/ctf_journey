#include <stdio.h>
#include <unistd.h>
int main() {
    int x;
    char buf[10];
    scanf("%d", &x);
    int n = read(0, buf, 5);
    printf("read %d bytes: ", n);
    for(int i=0; i<n; i++) printf("%02x ", (unsigned char)buf[i]);
    printf("\n");
    return 0;
}
