// recon/TraceEnv.java
import java.lang.reflect.*;
import java.util.*;

public class TraceEnv {
    public static void main(String[] args) throws Exception {
        Class<?> L = Class.forName("l");

        System.out.println("Relevant environment:");
        System.getenv().entrySet().stream()
            .filter(e ->
                e.getKey().toLowerCase().contains("seed") ||
                e.getKey().toLowerCase().contains("flag") ||
                e.getKey().toLowerCase().contains("burhan") ||
                e.getKey().toLowerCase().contains("ctf") ||
                e.getKey().toLowerCase().contains("secret"))
            .forEach(e ->
                System.out.println(e.getKey() + "=" + e.getValue()));

        Constructor<?> ctor = L.getDeclaredConstructor();
        ctor.setAccessible(true);

        Object obj = ctor.newInstance();

        for (String name : new String[] {
            "e", "f", "g", "h", "t", "u", "v", "w", "x", "y"
        }) {
            try {
                Field f = L.getDeclaredField(name);
                f.setAccessible(true);
                System.out.println(name + " = " + value(f.get(obj)));
            } catch (NoSuchFieldException ignored) {}
        }
    }

    static String value(Object x) {
        if (x instanceof int[])
            return Arrays.toString((int[])x);
        if (x instanceof byte[])
            return Arrays.toString((byte[])x);
        return String.valueOf(x);
    }
}
