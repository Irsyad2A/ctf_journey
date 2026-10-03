import java.lang.reflect.*;
import java.util.*;

public class ProbeRound35 {
    static String hx(byte[] x) {
        StringBuilder s = new StringBuilder();
        for (byte b : x) s.append(String.format("%02x", b & 0xff));
        return s.toString();
    }

    static byte[] call(int round, byte[] in) throws Exception {
        Class<?> p = Class.forName("p");

        Method m = null;

        for (Method x : p.getDeclaredMethods()) {
            if (!x.getName().equals("a")) continue;

            Class<?>[] t = x.getParameterTypes();

            if (t.length == 2 &&
                t[0] == int.class &&
                t[1] == byte[].class) {
                m = x;
                break;
            }
        }

        if (m == null)
            throw new RuntimeException("p.a(int, byte[]) not found");

        m.setAccessible(true);
        return (byte[])m.invoke(null, round, in);
    }

    public static void main(String[] args) throws Exception {
        for (int round : new int[]{3,5}) {
            System.out.println("ROUND " + round);

            int[] map = new int[256];
            boolean[] seen = new boolean[256];

            for (int v = 0; v < 256; v++) {
                byte[] in = {(byte)v};
                byte[] out = call(round, in);

                if (out.length != 1) {
                    System.out.println("unexpected output length");
                    return;
                }

                int y = out[0] & 0xff;
                map[v] = y;
                seen[y] = true;

                System.out.printf("%02x -> %02x%n", v, y);
            }

            boolean permutation = true;
            for (boolean b : seen)
                if (!b) permutation = false;

            System.out.println("permutation = " + permutation);

            if (permutation) {
                int[] inv = new int[256];

                for (int x = 0; x < 256; x++)
                    inv[map[x]] = x;

                System.out.println("INVERSE");

                for (int y = 0; y < 256; y++)
                    System.out.printf("%02x -> %02x%n", y, inv[y]);
            }

            System.out.println();
        }
    }
}
