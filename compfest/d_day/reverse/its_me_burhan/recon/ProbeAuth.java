import java.lang.reflect.*;

public class ProbeAuth {

    static Object newPrivate(Class<?> c) throws Exception {
        Constructor<?> ctor = c.getDeclaredConstructor();
        ctor.setAccessible(true);
        return ctor.newInstance();
    }

    public static void main(String[] args) throws Exception {
        Class<?> oc = Class.forName("o");
        Object account = newPrivate(oc);

        Method getUser = oc.getMethod("getUsername");
        String user = (String)getUser.invoke(account);

        System.out.println("[+] username = " + user);

        Class<?> lc = Class.forName("l");
        Object lobj = newPrivate(lc);

        Method verify = lc.getDeclaredMethod("a", String.class);
        verify.setAccessible(true);

        System.out.println("[+] verifier  = " + verify);
        System.out.println();

        String[] candidates = {
            "burhan",
            "Burhan",
            "BURHAN",
            "password",
            "admin",
            "admin123",
            "123456",
            "12345678",
            "burhan123",
            "burhanquest",
            "BurhanQuest",
            "DEV-SEED"
        };

        for (String s : candidates) {
            try {
                boolean ok = (Boolean)verify.invoke(lobj, s);
                System.out.printf("%-20s -> %s%n", s, ok);
            } catch (InvocationTargetException e) {
                System.out.printf(
                    "%-20s -> EXCEPTION %s%n",
                    s,
                    e.getCause()
                );
            }
        }

        System.out.println("\n=== L METHODS ===");
        for (Method m : lc.getDeclaredMethods()) {
            System.out.println(m);
        }
    }
}
