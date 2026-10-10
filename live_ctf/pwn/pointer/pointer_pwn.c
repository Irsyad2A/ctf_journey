#include <stdio.h>
#include <stdlib.h>
#include <string.h>

void win() {
    printf("\n🎯 BOOM! Pwned! Here is your flag:\n");
    system("cat flag.txt");
    exit(0);
}

void lose() {
    printf("\n[!] Access Denied. You are not an admin.\n");
    exit(0);
}

int main() {
    setvbuf(stdout, NULL, _IONBF, 0);
    
    // Menggunakan struct agar memori 'name' dan pointer 'action' bersebelahan
    struct {
        char name[32];
        void (*action)(); // Ini adalah Function Pointer!
    } user;

    user.action = lose;

    printf("=== SCHEMATIC SERVER LOGIN ===\n");
    printf("Bocoran: Alamat fungsi win() ada di: %p\n", win);
    printf("Saat ini, pointer 'action' menunjuk ke: %p (lose)\n", user.action);
    
    printf("\nMasukkan namamu: ");
    extern char *gets(char *s);
    gets(user.name); // VULNERABILITY!

    printf("\n[!] Cek Memori:\n");
    printf("Pointer 'action' sekarang menunjuk ke alamat: %p\n", user.action);
    printf("Mengeksekusi fungsi yang ditunjuk oleh pointer action...\n");
    
    // Dereference function pointer
    user.action();

    return 0;
}
