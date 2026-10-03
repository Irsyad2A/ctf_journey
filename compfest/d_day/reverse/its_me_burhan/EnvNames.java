public class EnvNames {
    public static void main(String[] args) {
        String[] names = {
            "BURHAN_SEED",
            "BURHANQUEST_SEED",
            "SEED",
            "CTFD_SEED",
            "INSTANCE_SEED",
            "CHALLENGE_SEED",
            "FLAG",
            "BURHAN_FLAG",
            "CTFD_FLAG"
        };

        for (String n : names)
            System.out.printf("%-24s = %s%n", n, System.getenv(n));
    }
}
