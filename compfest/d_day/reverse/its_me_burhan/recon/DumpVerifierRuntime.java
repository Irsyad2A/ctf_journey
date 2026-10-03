import java.lang.reflect.*;
import java.util.*;

public class DumpVerifierRuntime {
    static void dumpFields(Object obj, Class<?> cls) throws Exception {
        for (Field f : cls.getDeclaredFields()) {
            f.setAccessible(true);

            Object value;
            try {
                value = f.get(obj);
            } catch (Throwable t) {
                continue;
            }

            if (value instanceof int[]) {
                System.out.println(
                    "FIELD " + f.getName() +
                    " (int[]) = " +
                    Arrays.toString((int[]) value)
                );
            } else if (value instanceof String[]) {
                System.out.println(
                    "FIELD " + f.getName() +
                    " (String[]) = " +
                    Arrays.toString((String[]) value)
                );
            } else if (value instanceof long[]) {
                System.out.println(
                    "FIELD " + f.getName() +
                    " (long[]) = " +
                    Arrays.toString((long[]) value)
                );
            } else if (value != null &&
                       (f.getName().equals("x") ||
                        f.getName().equals("z") ||
                        f.getName().equals("e") ||
                        f.getName().equals("f"))) {
                System.out.println(
                    "FIELD " + f.getName() +
                    " = " + value
                );
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Class<?> cls = Class.forName("l");

        Constructor<?> ctor = cls.getDeclaredConstructor();
        ctor.setAccessible(true);

        Object obj = ctor.newInstance();

        Method verify = cls.getDeclaredMethod("a", String.class);
        verify.setAccessible(true);

        Field targetField = cls.getDeclaredField("y");
        targetField.setAccessible(true);

        int[] target = (int[]) targetField.get(obj);

        System.out.println("TARGET = " + Arrays.toString(target));

        String[] tests = {
            "AAAAAAAAAAAAAAAA",
            "BBBBBBBBBBBBBBBB",
            "ABCDEFGHIJKLMNOP",
            "2345672345672345",
            "4EED3JDBHBTLYV3B"
        };

        for (String s : tests) {
            System.out.println();
            System.out.println("========================================");
            System.out.println("INPUT = " + s);

            dumpFields(obj, cls);

            boolean result;

            try {
                result = (Boolean) verify.invoke(obj, s);
            } catch (InvocationTargetException e) {
                System.out.println(
                    "EXCEPTION = " + e.getCause()
                );
                continue;
            }

            System.out.println("RESULT = " + result);

            dumpFields(obj, cls);
        }
    }
}
