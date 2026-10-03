import java.lang.reflect.*;
import java.util.*;

public class DumpA {
    static String read(Object obj, String field) throws Exception {
        Field f = Class.forName("b").getDeclaredField(field);
        f.setAccessible(true);
        return (String) f.get(obj);
    }

    public static void main(String[] args) throws Exception {
        Class<?> A = Class.forName("a");

        Constructor<?> c0 = A.getDeclaredConstructor();
        c0.setAccessible(true);
        Object a0 = c0.newInstance();

        System.out.println("a() object:");
        System.out.println("name     = " + read(a0, "a"));
        System.out.println("username = " + read(a0, "b"));
        System.out.println("password = " + read(a0, "c"));

        Constructor<?> c1 = A.getDeclaredConstructor(String.class);
        c1.setAccessible(true);

        Object a1 = c1.newInstance("TEST");

        System.out.println("\na(TEST) object:");
        System.out.println("name     = " + read(a1, "a"));
        System.out.println("username = " + read(a1, "b"));
        System.out.println("password = " + read(a1, "c"));
    }
}
