import java.lang.reflect.*;
import java.util.*;

public class ProbeTransform {
    public static void main(String[] args) throws Exception {
        Class<?> P = Class.forName("p");

        Method m = P.getDeclaredMethod("a", int.class, int[].class);
        m.setAccessible(true);

        int[] x = new int[16];
        for (int i = 0; i < x.length; i++) x[i] = i;

        System.out.println("input  = " + Arrays.toString(x));

        for (int op = 0; op <= 6; op++) {
            int[] in = x.clone();
            int[] out = (int[]) m.invoke(null, op, in);
            System.out.printf("op %d = %s%n", op, Arrays.toString(out));
        }
    }
}
