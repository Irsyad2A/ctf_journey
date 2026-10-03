import java.lang.reflect.*;
import java.util.*;

public class ProbeV {
    static void dumpMethod(Class<?> c, String wanted) {
        for (Method m : c.getDeclaredMethods()) {
            if (m.getName().equals(wanted)) {
                System.out.println(m);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Class<?> V = Class.forName("v");

        System.out.println("=== v METHODS ===");
        for (Method m : V.getDeclaredMethods()) {
            System.out.println(m);
        }

        System.out.println("\n=== v CONSTRUCTORS ===");
        for (Constructor<?> c : V.getDeclaredConstructors()) {
            System.out.println(c);
        }

        System.out.println("\n=== v FIELDS ===");
        for (Field f : V.getDeclaredFields()) {
            System.out.println(f);
        }

        System.out.println("\n=== FACTORY SOURCE TARGET ===");
        dumpMethod(V, "j");
    }
}
