public class TestL {
    public static void main(String[] args) {
        l obj = l.c();

        String[] tests = {
            "",
            "burhan",
            "burunghantu123",
            "BURHAN",
            "admin",
            "K76LD64XY3URX4RM",
            "4EED3JDBHBTLYV3B"
        };

        for (String s : tests) {
            boolean r;
            try {
                r = obj.a(s);
            } catch (Throwable e) {
                r = false;
                System.out.println("ERR [" + s + "] " + e);
                continue;
            }

            System.out.printf("[%s] -> %s%n", s, r);
        }
    }
}
