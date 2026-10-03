// recon/DumpLenv.java
import java.lang.reflect.*;
import java.util.*;

public class DumpLenv {
    static String intsToString(int[] xs) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < xs.length; i++) {
            int ch = xs[i] ^ 45418 ^ (i % 65535);
            s.append((char)(ch & 0xffff));
        }
        return s.toString();
    }

    static int decode(String raw) {
        return Integer.parseInt(raw);
    }

    public static void main(String[] args) throws Exception {
        Class<?> L = Class.forName("l");

        // Invoke l's private static a(int...) directly if possible.
        Method dec = L.getDeclaredMethod("a", int[].class);
        dec.setAccessible(true);

        // These are copied literally from the constructor's two newarray blocks.
        int[] env1 = {
            // TODO: fill the first generated int[] from l.javap lines ~83-230
        };

        int[] env2 = {
            // TODO: fill the second generated int[] from l.javap lines ~248-465
        };

        System.out.println("env1 = " +
            dec.invoke(null, (Object)env1));

        System.out.println("env2 = " +
            dec.invoke(null, (Object)env2));
    }
}
