import java.lang.reflect.*;
import java.util.*;

public class Influence {
    static Method m;

    static int[] f(int[] x) throws Exception {
        int[] u = {4,2,6,3,5,0,1};

        for (int pass = 0; pass < 2; pass++)
            for (int r : u)
                x = (int[])m.invoke(null, r, x);

        return x;
    }

    public static void main(String[] args) throws Exception {
        Class<?> p = Class.forName("p");
        m = p.getDeclaredMethod("a", int.class, int[].class);
        m.setAccessible(true);

        int[] base = new int[16];
        int[] ref = f(base);

        System.out.println("BASE = " + Arrays.toString(ref));

        for (int pos = 0; pos < 16; pos++) {
            int[] x = new int[16];
            x[pos] = 1;

            int[] out = f(x);

            System.out.printf(
                "POS %02d  %s%n",
                pos,
                Arrays.toString(out)
            );
        }
    }
}
