import java.lang.reflect.*;
import java.util.*;

public class ProbeP {
    static Method round;

    static int[] call(int r, int[] x) throws Exception {
        return (int[]) round.invoke(null, r, x);
    }

    static void dump(String name, int[] a) {
        System.out.printf("%-18s %s%n", name, Arrays.toString(a));
    }

    public static void main(String[] args) throws Exception {
        Class<?> pc = Class.forName("p");

        System.out.println("=== METHODS p ===");
        for (Method m : pc.getDeclaredMethods()) {
            System.out.println(m);
        }

        round = pc.getDeclaredMethod("a", int.class, int[].class);
        round.setAccessible(true);

        System.out.println("\n=== ROUND PROBES ===");

        int[] zero = new int[16];
        dump("zero", zero);

        for (int r = 0; r <= 6; r++) {
            int[] x = new int[16];
            x[0] = 1;
            dump("R"+r+" basis0", call(r, x));
        }

        for (int r = 0; r <= 6; r++) {
            int[] x = new int[16];
            for (int i = 0; i < 16; i++)
                x[i] = i;
            dump("R"+r+" seq", call(r, x));
        }

        System.out.println("\n=== SINGLE VALUE TABLE ===");

        for (int r = 0; r <= 6; r++) {
            System.out.println("ROUND " + r);
            for (int v = 0; v < 32; v++) {
                int[] x = new int[16];
                x[0] = v;
                int[] y = call(r, x);
                System.out.printf("%02d -> %s%n", v, Arrays.toString(y));
            }
        }
    }
}
