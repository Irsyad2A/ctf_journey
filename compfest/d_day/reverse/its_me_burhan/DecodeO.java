import java.lang.reflect.*;

public class DecodeO {
    static String getString(Class<?> c, String method, String arg) throws Exception {
        Method m = c.getDeclaredMethod(method, String.class);
        m.setAccessible(true);
        return (String)m.invoke(null, arg);
    }

    public static void main(String[] args) throws Exception {
        Class<?> o = Class.forName("o");
        Class<?> a = Class.forName("a");

        for (String s : new String[] {
            "", "admin", "ADMIN", "burhan", "Burhan", "BURHAN"
        }) {
            try {
                Method m = o.getDeclaredMethod("b", String.class);
                m.setAccessible(true);
                System.out.printf("o.b(%s) = [%s]%n",
                        s, m.invoke(null, s));
            } catch (Throwable t) {
                System.out.println("o.b failed: " + t);
            }
        }

        Object obj = o.getDeclaredConstructor().newInstance();

        for (Class<?> c : new Class<?>[]{obj.getClass(), a, Class.forName("b")}) {
            System.out.println("\nCLASS " + c.getName());
            for (Field f : c.getDeclaredFields()) {
                f.setAccessible(true);
                try {
                    System.out.println(
                        f.getName() + " : " + f.getType().getName()
                        + " = " + f.get(obj)
                    );
                } catch (Throwable ignored) {}
            }
        }

        for (String n : new String[]{"getUsername", "getName", "getActorName"}) {
            try {
                Method m = Class.forName("b").getMethod(n);
                System.out.println(n + " = " + m.invoke(obj));
            } catch (Throwable t) {
                System.out.println(n + " ERROR " + t);
            }
        }
    }
}
