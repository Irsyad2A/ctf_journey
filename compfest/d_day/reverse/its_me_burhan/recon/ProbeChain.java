import java.lang.reflect.*;
import java.util.*;

public class ProbeChain {
    static final String ALPHA =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";

    static int[] idx(String s) {
        int[] r = new int[s.length()];
        for (int i = 0; i < s.length(); i++) {
            r[i] = ALPHA.indexOf(s.charAt(i));
        }
        return r;
    }

    static void dump(String tag, int[] x) {
        System.out.println(tag + " = " + Arrays.toString(x));

        StringBuilder s = new StringBuilder();
        for (int v : x) {
            if (v >= 0 && v < ALPHA.length())
                s.append(ALPHA.charAt(v));
            else
                s.append('?');
        }
        System.out.println("       = " + s);
    }

    public static void main(String[] args) throws Exception {
        Class<?> P = Class.forName("p");

        Method f = P.getDeclaredMethod(
            "a",
            int.class,
            int[].class
        );
        f.setAccessible(true);

        int[] u = {4, 2, 6, 3, 5, 0, 1};

        int[] state = idx("K76LD64XY3URX4RM");

        dump("START", state);

        for (int i = 0; i < u.length; i++) {
            int op = u[i];

            state = (int[]) f.invoke(
                null,
                op,
                state
            );

            dump("op[" + i + "] = " + op, state);
        }

        int[] target = {
            28,4,4,3,27,9,3,1,
            7,1,19,11,24,21,27,1
        };

        dump("TARGET", target);
        System.out.println(
            "MATCH = " + Arrays.equals(state, target)
        );
    }
}
