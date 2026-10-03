import java.lang.reflect.*;
import java.util.*;

public class DumpL {
    static Object get(Object o, String n) throws Exception {
        Class<?> c = o.getClass();
        while (c != null) {
            try {
                Field f = c.getDeclaredField(n);
                f.setAccessible(true);
                return f.get(o);
            } catch (NoSuchFieldException e) {
                c = c.getSuperclass();
            }
        }
        throw new NoSuchFieldException(n);
    }

    static Object callStatic(Class<?> c, String name, Class<?>[] types, Object... args)
            throws Exception {
        Method m = c.getDeclaredMethod(name, types);
        m.setAccessible(true);
        return m.invoke(null, args);
    }

    public static void main(String[] args) throws Exception {
        Class<?> L = Class.forName("l");

        Method factory = L.getDeclaredMethod("c");
        factory.setAccessible(true);
        Object l = factory.invoke(null);

        int x = (Integer)get(l, "x");
        int[] y = (int[])get(l, "y");

        System.out.println("x = " + x);
        System.out.println("y = " + Arrays.toString(y));

        Class<?> P = Class.forName("p");
        String alphabet = (String)callStatic(
            P, "b", new Class<?>[]{int.class}, x
        );

        System.out.println("alphabet = " + alphabet);
        System.out.println("alphabet length = " + alphabet.length());

        Method verify = L.getDeclaredMethod("a", String.class);
        verify.setAccessible(true);

        String[] tests = {
            "",
            "K76LD64XY3URX4RM",
            "123456789012345678901234"
        };

        for (String s : tests) {
            try {
                System.out.println(
                    "[" + s + "] -> " + verify.invoke(l, s)
                );
            } catch (InvocationTargetException e) {
                System.out.println(
                    "[" + s + "] -> EXCEPTION " +
                    e.getTargetException()
                );
            }
        }
    }
}
