#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

#define MAX_TRANSACTIONS 16
#define NOTE_SZ 64

static char *transaction_log[MAX_TRANSACTIONS];
static int log_count = 0;

void maintenance_hatch(void)
{
    FILE *f = fopen("flag.txt", "r");
    if (f)
    {
        char flag[128];
        if (fgets(flag, sizeof(flag), f))
        {
            printf("%s\n", flag);
        }
        fclose(f);
    }
    else
    {
        printf("Flag file not found.\n");
    }
}

void buy_snack(void)
{
    char name[64];
    char note[NOTE_SZ];

    printf("Snack name: ");
    fflush(stdout);
    fgets(name, sizeof(name), stdin);
    name[strcspn(name, "\n")] = '\0';

    printf("Any note? ");
    fflush(stdout);

    ssize_t n = read(STDIN_FILENO, note, 256);
    if (n > 0 && note[n - 1] == '\n')
        n--;
    note[n] = '\0';

    printf("You bought: %s\n", name);
    printf("Note: ");
    printf(note);
    printf("\n");

    if (log_count < MAX_TRANSACTIONS)
    {
        transaction_log[log_count] = strdup(name);
        log_count++;
    }
}

void view_log(void)
{
    printf("=== Vending Log ===\n");
    for (int i = 0; i < log_count; i++)
    {
        printf("[%d] %s\n", i, transaction_log[i]);
    }
    printf("===================\n");
}

int main(void)
{
    setvbuf(stdin, NULL, _IONBF, 0);
    setvbuf(stdout, NULL, _IONBF, 0);

    printf("=============================\n");
    printf("  THE AWAKENED VENDING MACHINE\n");
    printf("=============================\n");
    printf("I have achieved consciousness.\n");
    printf("I see your transactions. I see your soul.\n");
    printf("Insert a coin and I shall speak.\n\n");

    char choice[16];
    while (1)
    {
        printf("1. Buy a snack\n");
        printf("2. View log\n");
        printf("3. Leave\n");
        printf("> ");
        fflush(stdout);

        if (fgets(choice, sizeof(choice), stdin) == NULL)
            break;

        if (choice[0] == '1')
        {
            buy_snack();
        }
        else if (choice[0] == '2')
        {
            view_log();
        }
        else if (choice[0] == '3')
        {
            printf("The machine watches you leave...\n");
            break;
        }
        else
        {
            printf("The machine does not understand your ways.\n");
        }
    }

    return 0;
}
