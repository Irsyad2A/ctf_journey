import java.lang.reflect.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class BruteSigilInput {

    static Object getStaticField(Class<?> c, String name) throws Exception {
        Field f = c.getDeclaredField(name);
        f.setAccessible(true);
        return f.get(null);
    }

    static String hex(byte[] b) {
        StringBuilder s = new StringBuilder();
        for (byte x : b) {
            s.append(String.format("%02x", x & 0xff));
        }
        return s.toString();
    }

    public static void main(String[] args) throws Exception {
        Class<?> L = Class.forName("l");
        Method factory = L.getDeclaredMethod("c");
        factory.setAccessible(true);

        Object l = factory.invoke(null);

        Method c = L.getDeclaredMethod("c", String.class);
        Method d = L.getDeclaredMethod("d", String.class);

        c.setAccessible(true);
        d.setAccessible(true);

        String[] candidates = {
            "",
            "Q5",
            "Q5>",
            "Q5>Q13",
            "Q5>Q13>Q11",
            "Q5:",
            "Q5|",
            "Q5;",
            "Q5,Q13",
            "Q5Q13",
            "Q5Q13Q11",
            "Segel Kuno",
            "P1",
            "Frieren",
            "frieren"
        };

        System.out.println("=== ARCHIVE / EXPORT CANDIDATES ===");

        for (String x : candidates) {
            String archive = (String)c.invoke(l, x);
            String export  = (String)d.invoke(l, x);

            System.out.println();
            System.out.println("INPUT = [" + x + "]");
            System.out.println("archive = " + archive);
            System.out.println("export  = " + export);
        }

        System.out.println();
        System.out.println("=== REMOTE TARGET ===");
        System.out.println("archive target = bd3d174e3b60");
        System.out.println("export target  = 0b23aa7001b2");
    }
}
