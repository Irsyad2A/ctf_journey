import java.lang.reflect.*;
import java.util.*;

public class DumpC {

    static void dump(Object obj, String name) {
        if (obj == null) {
            System.out.println(name + " = null");
            return;
        }

        Class<?> cls = obj.getClass();

        if (!cls.isArray()) {
            System.out.println(name + " = " + obj);
            return;
        }

        int n = Array.getLength(obj);

        System.out.println(
            name + " = " +
            cls.getComponentType().getTypeName() +
            "[" + n + "]"
        );

        for (int i = 0; i < n; i++) {
            Object x = Array.get(obj, i);
            System.out.println("  [" + i + "] = " + x);
        }
    }

    public static void main(String[] args) throws Exception {

        Class<?> L = Class.forName("l");

        Constructor<?> ctor = L.getDeclaredConstructor();
        ctor.setAccessible(true);

        Object obj = ctor.newInstance();

        System.out.println("=== ALL DECLARED FIELDS OF l ===");

        for (Field f : L.getDeclaredFields()) {

            f.setAccessible(true);

            Object owner = Modifier.isStatic(f.getModifiers())
                ? null
                : obj;

            Object value;

            try {
                value = f.get(owner);
            } catch (Throwable e) {
                value = "<ERROR: " + e + ">";
            }

            System.out.println(
                "\nFIELD " + f.getName() +
                " : " + f.getType().getTypeName() +
                (Modifier.isStatic(f.getModifiers()) ? " [static]" : "")
            );

            dump(value, "value");
        }
    }
}
