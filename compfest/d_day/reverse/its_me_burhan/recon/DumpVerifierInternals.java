import java.lang.reflect.*;
import java.util.*;

public class DumpVerifierInternals {
    static Field field(Class<?> c, String name) throws Exception {
        Field f = c.getDeclaredField(name);
        f.setAccessible(true);
        return f;
    }

    static void dumpIntArray(Object obj, Class<?> c, String name) throws Exception {
        Field f = field(c, name);
        Object v = f.get(obj);
        if (v instanceof int[]) {
            System.out.println(name + " = " +
                Arrays.toString((int[])v));
        }
    }

    public static void main(String[] args) throws Exception {
        Class<?> c = Class.forName("l");

        Constructor<?> ctor = c.getDeclaredConstructor();
        ctor.setAccessible(true);
        Object obj = ctor.newInstance();

        Method verify = c.getDeclaredMethod("a", String.class);
        verify.setAccessible(true);

        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";

        System.out.println("alphabet = " + alphabet);

        String[] tests = {
            "AAAAAAAAAAAAAAAA",
            "BAAAAAAAAAAAAAAA",
            "CAAAAAAAAAAAAAAA",
            "DAAAAAAAAAAAAAAA",
            "EAAAAAAAAAAAAAAA",
            "FAAAAAAAAAAAAAAA",
            "GAAAAAAAAAAAAAAA",
            "HAAAAAAAAAAAAAAA",
            "IAAAAAAAAAAAAAAA",
            "JAAAAAAAAAAAAAAA",
            "KAAAAAAAAAAAAAAA",
            "LAAAAAAAAAAAAAAA",
            "MAAAAAAAAAAAAAAA",
            "NAAAAAAAAAAAAAAA",
            "OAAAAAAAAAAAAAAA",
            "PAAAAAAAAAAAAAAA",
            "QAAAAAAAAAAAAAAA",
            "RAAAAAAAAAAAAAAA",
            "SAAAAAAAAAAAAAAA",
            "TAAAAAAAAAAAAAAA",
            "UAAAAAAAAAAAAAAA",
            "VAAAAAAAAAAAAAAA",
            "WAAAAAAAAAAAAAAA",
            "XAAAAAAAAAAAAAAA",
            "YAAAAAAAAAAAAAAA",
            "ZAAAAAAAAAAAAAAA",
            "2AAAAAAAAAAAAAAA",
            "3AAAAAAAAAAAAAAA",
            "4AAAAAAAAAAAAAAA",
            "5AAAAAAAAAAAAAAA",
            "6AAAAAAAAAAAAAAA",
            "7AAAAAAAAAAAAAAA"
        };

        for (String s : tests) {
            boolean r = (Boolean)verify.invoke(obj, s);

            System.out.printf("%s -> %s%n", s, r);
        }

        System.out.println("\n=== OBJECT FIELDS ===");

        for (Field f : c.getDeclaredFields()) {
            f.setAccessible(true);

            Object value;
            try {
                value = Modifier.isStatic(f.getModifiers())
                    ? f.get(null)
                    : f.get(obj);
            } catch (Throwable e) {
                value = "<unreadable>";
            }

            if (value instanceof int[]) {
                System.out.println(
                    f.getName() + " = " +
                    Arrays.toString((int[]) value)
                );
            } else {
                System.out.println(
                    f.getName() + " = " + value
                );
            }
        }
    }
}
