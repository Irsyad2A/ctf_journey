import java.lang.reflect.*;

public class ProbeQ {
    static void dump(Object o) throws Exception {
        if (o == null) {
            System.out.println("null");
            return;
        }

        Class<?> B = Class.forName("b");

        Field name = B.getDeclaredField("a");
        Field user = B.getDeclaredField("b");
        Field pass = B.getDeclaredField("c");

        name.setAccessible(true);
        user.setAccessible(true);
        pass.setAccessible(true);

        System.out.println("class    = " + o.getClass().getName());
        System.out.println("name     = " + name.get(o));
        System.out.println("username = " + user.get(o));
        System.out.println("password = " + pass.get(o));
    }

    public static void main(String[] args) throws Exception {
        Class<?> Q = Class.forName("q");
        Constructor<?> ctor = Q.getDeclaredConstructor();
        ctor.setAccessible(true);
        Object q = ctor.newInstance();

        Method lookup = Q.getDeclaredMethod(
            "a", String.class, String.class
        );
        lookup.setAccessible(true);

        String[] users = {
            "admin",
            "ADMIN",
            "Admin",
            "4EED3JDBHBTLYV3B",
            "K76LD64XY3URX4RM"
        };

        String[] pwds = {
            "K76LD64XY3URX4RM",
            "4EED3JDBHBTLYV3B"
        };

        for (String u : users) {
            for (String p : pwds) {
                System.out.println(
                    "\n[" + u + " / " + p + "]"
                );

                try {
                    dump(lookup.invoke(q, u, p));
                } catch (InvocationTargetException e) {
                    System.out.println(
                        "ERROR: " + e.getCause()
                    );
                }
            }
        }
    }
}
