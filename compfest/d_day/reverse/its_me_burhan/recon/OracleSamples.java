import java.lang.reflect.*;
import java.util.*;

public class OracleSamples {
    public static void main(String[] args) throws Exception {
        Class<?> lc = Class.forName("l");
        Method c = lc.getDeclaredMethod("c");
        c.setAccessible(true);
        Object l = c.invoke(null);

        Method b = lc.getMethod("b", String.class);
        Method f = lc.getMethod("c", String.class);
        Method g = lc.getMethod("d", String.class);

        String[] qs = {
            "Q5",
            "Pembersihan Hutan",
            "Reruntuhan Batu",
            "Teror Rawa"
        };

        for (String q : qs) {
            System.out.println(
                q + " | " +
                b.invoke(l, q) + " | " +
                f.invoke(l, q) + " | " +
                g.invoke(l, q)
            );
        }
    }
}
