import java.lang.reflect.*;
import java.util.*;

public class FindSigilMethods {
    public static void main(String[] args) throws Exception {
        Class<?>[] classes = {
            Class.forName("l"),
            Class.forName("p"),
            Class.forName("x"),
            Class.forName("aj")
        };

        for (Class<?> c : classes) {
            System.out.println();
            System.out.println("===== " + c.getName() + " =====");

            for (Method m : c.getDeclaredMethods()) {
                String s = m.toString().toLowerCase();

                if (s.contains("string") ||
                    s.contains("byte") ||
                    s.contains("int") ||
                    s.contains("long")) {

                    System.out.println(m);
                }
            }
        }
    }
}
