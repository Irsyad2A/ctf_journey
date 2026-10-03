import java.lang.reflect.*;
import java.util.*;

public class DumpMainStrings {
    public static void main(String[] args) throws Exception {
        Class<?> c = Class.forName("Main");
        for (Field f : c.getDeclaredFields()) {
            f.setAccessible(true);
            Object v = null;
            try { v = f.get(null); } catch (Throwable ignored) {}
            System.out.println("FIELD " + f + " = " + v);
        }

        for (Method m : c.getDeclaredMethods()) {
            System.out.println("METHOD " + m);
        }
    }
}
