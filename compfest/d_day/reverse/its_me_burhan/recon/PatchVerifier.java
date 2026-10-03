import java.lang.reflect.*;
import java.util.*;

public class DumpVerifierRuntime {

    public static void main(String[] args) throws Exception {
        Class<?> cls = Class.forName("l");

        Constructor<?> ctor = cls.getDeclaredConstructor();
        ctor.setAccessible(true);

        Object obj = ctor.newInstance();

        Field targetField = cls.getDeclaredField("y");
        targetField.setAccessible(true);

        int[] target = (int[]) targetField.get(obj);

        System.out.println("TARGET = " + Arrays.toString(target));

        Method verify = cls.getDeclaredMethod("a", String.class);
        verify.setAccessible(true);

        String[] tests = {
            "AAAAAAAAAAAAAAAA",
            "BBBBBBBBBBBBBBBB",
            "ABCDEFGHIJKLMNOP",
            "2345672345672345"
        };

        for (String s : tests) {
            System.out.println();
            System.out.println("INPUT  = " + s);

            boolean result = (Boolean) verify.invoke(obj, s);

            System.out.println("VERIFY = " + result);

            /*
             * Search all int[] fields after execution.
             * This is useful for discovering whether the transformed
             * candidate is retained in an object field.
             */
            for (Field f : cls.getDeclaredFields()) {
                f.setAccessible(true);

                Object value;
                try {
                    value = f.get(obj);
                } catch (Throwable e) {
                    continue;
                }

                if (value instanceof int[]) {
                    int[] a = (int[]) value;
                    System.out.println(
                        "FIELD " + f.getName() +
                        " = " + Arrays.toString(a)
                    );
                }
            }
        }
    }
}
