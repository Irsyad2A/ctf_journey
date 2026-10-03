import java.lang.reflect.*;
import java.util.*;

public class ProbeOps {
    static void p(Method f, int op, int[] x) throws Exception {
        int[] y = (int[]) f.invoke(null, op, (Object)x.clone());
        System.out.printf(
            "op%d  %s -> %s%n",
            op,
            Arrays.toString(x),
            Arrays.toString(y)
        );
    }

    public static void main(String[] args) throws Exception {
        Class<?> P = Class.forName("p");

        Method f = P.getDeclaredMethod(
            "a",
            int.class,
            int[].class
        );
        f.setAccessible(true);

        int[][] tests = {
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2},
            {3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3},
            {10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10},
            {31,31,31,31,31,31,31,31,31,31,31,31,31,31,31,31}
        };

        for (int op = 0; op <= 6; op++) {
            for (int[] x : tests)
                p(f, op, x);
            System.out.println();
        }
    }
}
