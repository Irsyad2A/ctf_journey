import java.lang.reflect.*;

public class ProbeLogin {
    public static void main(String[] args) throws Exception {
        Class<?> c = Class.forName("l");
        Method get = c.getDeclaredMethod("c");
        get.setAccessible(true);

        Object obj = get.invoke(null);

        System.out.println("=== METHODS ===");
        for (Method m : c.getDeclaredMethods()) {
            System.out.println(m);
        }

        String[] candidates = {
            "admin",
            "burhan",
            "Burhan",
            "SuperUser",
            "superuser",
            "user",
            "player",
            "guest",
            "test",
            "lucymayreel"
        };

        Method login = null;
        for (Method m : c.getDeclaredMethods()) {
            if (m.getName().equals("e") &&
                m.getParameterCount() == 1 &&
                m.getParameterTypes()[0] == String.class) {
                login = m;
                break;
            }
        }

        System.out.println("LOGIN METHOD = " + login);

        if (login != null) {
            login.setAccessible(true);

            String pw = "5Y444KRUSQZ3QULV";

            for (String user : candidates) {
                try {
                    Object r = login.invoke(obj, user);
                    System.out.printf("%-15s -> %s%n", user, r);
                } catch (Throwable t) {
                    System.out.printf("%-15s -> EXCEPTION %s%n",
                            user, t.getCause());
                }
            }
        }
    }
}
