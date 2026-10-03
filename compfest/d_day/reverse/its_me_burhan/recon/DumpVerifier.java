import java.lang.reflect.*;
import java.util.*;

public class DumpVerifier {

    static Object construct(Class<?> c) throws Exception {
        Constructor<?> ctor = c.getDeclaredConstructor();
        ctor.setAccessible(true);
        return ctor.newInstance();
    }

    static Object field(Object obj, String name) throws Exception {
        Field f = obj.getClass().getDeclaredField(name);
        f.setAccessible(true);
        return f.get(obj);
    }

    static void dumpArray(String name, Object a) {
        if (a == null) {
            System.out.println(name + " = null");
            return;
        }

        Class<?> t = a.getClass();

        if (t == int[].class) {
            System.out.println(name + " = " +
                Arrays.toString((int[])a));
        } else if (t == long[].class) {
            System.out.println(name + " = " +
                Arrays.toString((long[])a));
        } else if (t == byte[].class) {
            byte[] x = (byte[])a;
            System.out.println(name + " = " + bytesHex(x));
            System.out.println(name + "(ascii) = " +
                new String(x));
        } else if (t == String[].class) {
            System.out.println(name + " = " +
                Arrays.toString((String[])a));
        } else {
            System.out.println(name + " = " + a);
        }
    }

    static String bytesHex(byte[] x) {
        StringBuilder s = new StringBuilder();
        for (byte b : x)
            s.append(String.format("%02x", b & 0xff));
        return s.toString();
    }

    public static void main(String[] args) throws Exception {

        Class<?> lc = Class.forName("l");

        Object l = construct(lc);

        System.out.println("=== L STATE ===");

        for (Field f : lc.getDeclaredFields()) {
            f.setAccessible(true);

            Object v;
            if (Modifier.isStatic(f.getModifiers()))
                v = f.get(null);
            else
                v = f.get(l);

            System.out.print(f.getName() + " (" +
                f.getType().getSimpleName() + ") = ");

            if (v != null && v.getClass().isArray())
                dumpArray(f.getName(), v);
            else
                System.out.println(v);
        }

        System.out.println();
        System.out.println("=== P.B(0) ===");

        Class<?> pc = Class.forName("p");

        Method pb = pc.getDeclaredMethod("b", int.class);
        pb.setAccessible(true);

        String alphabet = (String)pb.invoke(null, 0);

        System.out.println("alphabet = [" + alphabet + "]");
        System.out.println("length   = " + alphabet.length());

        for (int i = 0; i < alphabet.length(); i++) {
            System.out.printf(
                "index[%02d] = %c (%d)%n",
                i,
                alphabet.charAt(i),
                (int)alphabet.charAt(i)
            );
        }

        System.out.println();
        System.out.println("=== L METHODS ===");

        for (Method m : lc.getDeclaredMethods())
            System.out.println(m);

        System.out.println();
        System.out.println("=== VERIFIER TEST ===");

        Method verify =
            lc.getDeclaredMethod("a", String.class);
        verify.setAccessible(true);

        String[] tests = {
            "AAAAAAAA",
            "BBBBBBBB",
            "00000000",
            "11111111",
            "abcdefgh",
            "password",
            "burhan00",
            "12345678"
        };

        for (String s : tests) {
            try {
                Object r = verify.invoke(l, s);
                System.out.printf("%-12s -> %s%n", s, r);
            } catch (InvocationTargetException e) {
                System.out.printf(
                    "%-12s -> EXCEPTION %s%n",
                    s,
                    e.getCause()
                );
            }
        }
    }
}
