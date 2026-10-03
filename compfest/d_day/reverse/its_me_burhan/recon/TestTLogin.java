import java.lang.reflect.*;

public class TestTLogin {
    public static void main(String[] args) throws Exception {
        Class<?> T = Class.forName("t");

        Constructor<?> c = T.getDeclaredConstructor();
        c.setAccessible(true);
        Object t = c.newInstance();

        Method m = T.getDeclaredMethod(
            "a", String.class, String.class
        );
        m.setAccessible(true);

        String[] users = {
            "burhan",
            "admin",
            "ADMIN"
        };

        String[] passwords = {
            "burunghantu123",
            "K76LD64XY3URX4RM"
        };

        for (String u : users) {
            for (String p : passwords) {
                Object result = m.invoke(t, u, p);

                System.out.printf(
                    "[%-10s / %-20s] -> %s%n",
                    u,
                    p,
                    result
                );

                if (result != null) {
                    Method getName =
                        result.getClass().getMethod("getName");
                    Method getUser =
                        result.getClass().getMethod("getUsername");

                    System.out.println(
                        "    class=" + result.getClass().getName() +
                        " name=" + getName.invoke(result) +
                        " username=" + getUser.invoke(result)
                    );
                }
            }
        }
    }
}
