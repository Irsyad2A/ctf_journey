import java.lang.reflect.*;
import java.util.*;

public class solve_password {

    static Object getField(Class<?> c, Object obj, String name) throws Exception {
        Field f = c.getDeclaredField(name);
        f.setAccessible(true);
        return f.get(obj);
    }

    static String arr(int[] a) {
        return Arrays.toString(a);
    }

    public static void main(String[] args) throws Exception {
        Class<?> L = Class.forName("l");
        Class<?> P = Class.forName("p");

        System.out.println("===== p METHODS =====");
        for (Method m : P.getDeclaredMethods()) {
            m.setAccessible(true);
            System.out.println(m);
        }

        System.out.println();
        System.out.println("===== p FIELDS =====");
        for (Field f : P.getDeclaredFields()) {
            f.setAccessible(true);
            System.out.println(f);
        }

        Method pInt = null;
        Method pByte = null;

        for (Method m : P.getDeclaredMethods()) {
            Class<?>[] t = m.getParameterTypes();
            if (m.getReturnType() == int[].class &&
                t.length == 2 &&
                t[0] == int.class &&
                t[1] == int[].class) {
                pInt = m;
                pInt.setAccessible(true);
            }

            if (m.getReturnType() == byte[].class &&
                t.length == 2 &&
                t[0] == int.class &&
                t[1] == byte[].class) {
                pByte = m;
                pByte.setAccessible(true);
            }
        }

        if (pInt == null) {
            throw new RuntimeException("p.a(int,int[]) not found");
        }

        System.out.println();
        System.out.println("FOUND int transform = " + pInt);
        System.out.println("FOUND byte transform = " + pByte);

        // Construct singleton l.
        Method getL = L.getDeclaredMethod("c");
        getL.setAccessible(true);
        Object l = getL.invoke(null);

        int[] u = (int[]) getField(L, l, "u");
        int[] y = (int[]) getField(L, l, "y");
        String seed = (String) getField(L, l, "e");

        System.out.println();
        System.out.println("===== L STATE =====");
        System.out.println("seed = " + seed);
        System.out.println("u    = " + arr(u));
        System.out.println("y    = " + arr(y));

        /*
         * Probe p.a(round,state).
         *
         * The target y has 16 ints, so use a 16-int state.
         * We probe basis vectors and zero-vector to identify:
         *   - whether operation is XOR/additive
         *   - whether it is a permutation
         *   - whether it is affine
         */
        int[] zero = new int[y.length];

        System.out.println();
        System.out.println("===== TRANSFORM PROBES =====");

        for (int r : u) {
            int[] z = (int[]) pInt.invoke(null, r, zero.clone());
            System.out.println("round " + r + " zero -> " + arr(z));

            for (int i = 0; i < Math.min(4, y.length); i++) {
                int[] basis = new int[y.length];
                basis[i] = 1;
                int[] out = (int[]) pInt.invoke(null, r, basis);
                System.out.println(
                    "round " + r +
                    " basis[" + i + "] -> " +
                    arr(out)
                );
            }
        }

        /*
         * Test linearity:
         *
         * F(a ^ b) == F(a) ^ F(b) ^ F(0)
         *
         * If this holds, each round is affine over GF(2),
         * which lets us reconstruct the inverse exactly.
         */
        System.out.println();
        System.out.println("===== LINEARITY TEST =====");

        int[] A = new int[y.length];
        int[] B = new int[y.length];

        for (int i = 0; i < y.length; i++) {
            A[i] = (i * 0x1337 + 7);
            B[i] = (i * 0x4242 + 3);
        }

        for (int r : u) {
            int[] ab = new int[y.length];
            for (int i = 0; i < y.length; i++)
                ab[i] = A[i] ^ B[i];

            int[] fa  = (int[]) pInt.invoke(null, r, A.clone());
            int[] fb  = (int[]) pInt.invoke(null, r, B.clone());
            int[] fab = (int[]) pInt.invoke(null, r, ab.clone());
            int[] f0  = (int[]) pInt.invoke(null, r, zero.clone());

            boolean affine = true;

            for (int i = 0; i < y.length; i++) {
                int rhs = fa[i] ^ fb[i] ^ f0[i];
                if (fab[i] != rhs) {
                    affine = false;
                    break;
                }
            }

            System.out.println("round " + r + " affine = " + affine);
        }

        /*
         * Test whether round transform is merely a permutation
         * of the elements with a round-dependent XOR/addition.
         */
        System.out.println();
        System.out.println("===== SINGLE VALUE PROBE =====");

        for (int r : u) {
            for (int v : new int[]{0,1,2,7,0x41,0x1337,0xdeadbeef}) {
                int[] q = new int[y.length];
                q[0] = v;

                int[] out = (int[]) pInt.invoke(null, r, q);

                System.out.printf(
                    "round=%d input0=%08x output=%s%n",
                    r,
                    v,
                    arr(out)
                );
            }
        }

        /*
         * Finally verify candidates through the REAL verifier.
         */
        Method verify = L.getDeclaredMethod("a", String.class);
        verify.setAccessible(true);

        String[] candidates = {
            "K76LD64XY3URX4RM",
            "5Y444KRUSQZ3QULV",
            "4EED3JDBHBTLYV3B",
            "burunghantu123",
            "DEV-SEED"
        };

        System.out.println();
        System.out.println("===== REAL VERIFIER =====");

        for (String s : candidates) {
            boolean ok = (Boolean) verify.invoke(l, s);
            System.out.printf("%s -> %s%n", s, ok);
        }
    }
}
