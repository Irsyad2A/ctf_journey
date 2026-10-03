import java.lang.reflect.*;
import java.util.*;

public class TraceVerifierRounds {
    static Method round;

    static int[] call(int r, int[] a) throws Exception {
        return (int[]) round.invoke(null, r, a);
    }

    static void show(String tag, int[] a) {
        System.out.printf("%-8s %s%n", tag, Arrays.toString(a));
    }

    public static void main(String[] args) throws Exception {
        Class<?> p = Class.forName("p");
        round = p.getDeclaredMethod("a", int.class, int[].class);
        round.setAccessible(true);

        int[] u = {4, 2, 6, 3, 5, 0, 1};
        int[] y = {28,4,4,3,27,9,3,1,7,1,19,11,24,21,27,1};

        int[] a = new int[16];

        System.out.println("=== ZERO THROUGH EXACT VERIFIER SCHEDULE ===");
        show("START", a);

        int n = 0;

        for (int pass = 0; pass < 2; pass++) {
            for (int r : u) {
                a = call(r, a);
                show(String.format("%02d R%d", ++n, r), a);
            }
        }

        System.out.println();
        System.out.println("TARGET = " + Arrays.toString(y));

        System.out.println();
        System.out.println("=== TARGET BACKWARDS, ONE ROUND AT A TIME ===");

        a = y.clone();
        show("START", a);

        int m = 0;
        for (int pass = 0; pass < 2; pass++) {
            for (int k = u.length - 1; k >= 0; k--) {
                int r = u[k];

                /*
                 * We intentionally do NOT implement an inverse here.
                 * Instead this establishes exactly which operations occur
                 * and whether p.a is actually bijective at runtime.
                 */
                System.out.printf("%02d INVERT R%d%n", ++m, r);
                System.out.println("state = " + Arrays.toString(a));
            }
        }

        System.out.println();
        System.out.println("=== INDIVIDUAL ROUND TESTS ===");

        int[][] tests = {
            {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15},
            {28,4,4,3,27,9,3,1,7,1,19,11,24,21,27,1}
        };

        for (int r = 0; r <= 6; r++) {
            System.out.println("ROUND " + r);

            for (int[] t : tests) {
                System.out.println(
                    "in  = " + Arrays.toString(t)
                );
                System.out.println(
                    "out = " + Arrays.toString(call(r, t.clone()))
                );
            }
        }
    }
}
