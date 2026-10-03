import java.lang.reflect.*;
import java.util.*;

public class TraceStages {

    static String hex(byte[] b) {
        StringBuilder s = new StringBuilder();
        for (byte x : b)
            s.append(String.format("%02x", x & 0xff));
        return s.toString();
    }

    static Object field(Object obj, String name) throws Exception {
        Field f = obj.getClass().getDeclaredField(name);
        f.setAccessible(true);
        return f.get(obj);
    }

    public static void main(String[] args) throws Exception {

        String flag = args.length > 0 ? args[0] : "AAAAAAAAAAAAAAAA";

        System.out.println("=== TRACE ===");
        System.out.println("flag = " + flag);

        Class<?> lc = Class.forName("l");

        Method singleton = lc.getDeclaredMethod("c");
        singleton.setAccessible(true);

        Object l = singleton.invoke(null);

        System.out.println("seed = " + field(l, "e"));
        System.out.println("flag = " + field(l, "f"));

        int[] u = (int[])field(l, "u");
        System.out.println("u = " + Arrays.toString(u));

        Class<?> pc = Class.forName("p");

        Method aIntBytes = pc.getDeclaredMethod(
            "a",
            int.class,
            byte[].class
        );

        aIntBytes.setAccessible(true);

        byte[] cur = flag.getBytes(java.nio.charset.StandardCharsets.UTF_8);

        System.out.println("input length = " + cur.length);
        System.out.println("input hex    = " + hex(cur));

        for (int i = 0; i < u.length; i++) {

            System.out.println();
            System.out.println("--- stage " + i + " ---");
            System.out.println("round = " + u[i]);
            System.out.println("before = " + hex(cur));

            cur = (byte[])aIntBytes.invoke(null, u[i], cur);

            System.out.println("after  = " + hex(cur));
        }

        System.out.println();
        System.out.println("=== FINAL ===");
        System.out.println(hex(cur));
    }
}
