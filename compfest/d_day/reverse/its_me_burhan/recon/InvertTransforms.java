import java.lang.reflect.*;
import java.util.*;

public class InvertTransforms {
    static int[] inverse(int[] p) {
        int[] r = new int[p.length];
        for (int i = 0; i < p.length; i++) {
            r[p[i]] = i;
        }
        return r;
    }

    public static void main(String[] args) throws Exception {
        Class<?> P = Class.forName("p");
        Method m = P.getDeclaredMethod("a", int.class, int[].class);
        m.setAccessible(true);

        int[] base = new int[16];
        for (int i = 0; i < 16; i++)
            base[i] = i;

        for (int op = 0; op <= 6; op++) {
            int[] out = (int[])m.invoke(null, op, (Object)base.clone());

            System.out.printf("op %d%n", op);
            System.out.println(" forward = " + Arrays.toString(out));

            boolean permutation = true;
            boolean[] seen = new boolean[out.length];

            for (int v : out) {
                if (v < 0 || v >= out.length || seen[v]) {
                    permutation = false;
                    break;
                }
                seen[v] = true;
            }

            System.out.println(" permutation = " + permutation);

            if (permutation)
                System.out.println(" inverse = " + Arrays.toString(inverse(out)));
        }
    }
}
