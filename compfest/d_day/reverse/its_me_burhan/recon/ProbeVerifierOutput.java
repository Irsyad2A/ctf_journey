import java.lang.reflect.*;
import java.util.*;

public class ProbeVerifierOutput {
    static Object get(Field f, Object o) throws Exception {
        f.setAccessible(true);
        return f.get(o);
    }

    static void dump(Object obj, String name) throws Exception {
        Class<?> c = obj.getClass();

        System.out.println("CLASS = " + c.getName());

        for (Field f : c.getDeclaredFields()) {
            try {
                f.setAccessible(true);
                Object v = f.get(obj);

                if (v instanceof int[]) {
                    System.out.println(name + "." + f.getName() +
                        " = " + Arrays.toString((int[]) v));
                } else if (v instanceof long[]) {
                    System.out.println(name + "." + f.getName() +
                        " = " + Arrays.toString((long[]) v));
                } else if (v != null &&
                           (f.getName().equals("x") ||
                            f.getName().equals("y") ||
                            f.getName().equals("z") ||
                            f.getName().equals("e") ||
                            f.getName().equals("f"))) {
                    System.out.println(name + "." + f.getName() +
                        " = " + v);
                }
            } catch (Throwable ignored) {}
        }
    }

    public static void main(String[] args) throws Exception {
        String pw = args.length > 0 ? args[0] : "AAAAAAAAAAAAAAAA";

        Class<?> lc = Class.forName("l");

        Method factory = lc.getDeclaredMethod("c");
        factory.setAccessible(true);
        Object verifier = factory.invoke(null);

        Method verify = lc.getDeclaredMethod("a", String.class);
        verify.setAccessible(true);

        System.out.println("INPUT  = " + pw);
        System.out.println("RESULT = " + verify.invoke(verifier, pw));

        System.out.println("\nBEFORE/AFTER STATE:");
        dump(verifier, "l");
    }
}
