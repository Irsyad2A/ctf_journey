import java.lang.reflect.*;
import java.util.*;

public class ProbeBytes {
    public static void main(String[] args) throws Exception {
        Class<?> p = Class.forName("p");

        Method m = p.getDeclaredMethod(
            "a", byte[].class, int.class
        );
        m.setAccessible(true);

        byte[][] tests = {
            new byte[] {},
            new byte[] {0},
            new byte[] {1},
            new byte[] {31},
            new byte[] {32},
            new byte[] {63},
            new byte[] {64},
            new byte[] {(byte)255},
            new byte[] {0,1,2,3,4,5,6,7},
            new byte[] {31,31,31,31,31,31,31,31},
            new byte[] {1,2,4,8,16,32,64,127,(byte)128}
        };

        int[] modes = {0, 1, 2, 7, 8, 16, 23, 31, 32};

        for (int mode : modes) {
            System.out.println("======== mode " + mode + " ========");

            for (byte[] input : tests) {
                try {
                    int[] out = (int[])m.invoke(null, input, mode);

                    System.out.printf(
                        "in=%s -> len=%d out=%s%n",
                        Arrays.toString(input),
                        out.length,
                        Arrays.toString(out)
                    );
                } catch (InvocationTargetException e) {
                    System.out.printf(
                        "in=%s -> ERROR: %s%n",
                        Arrays.toString(input),
                        e.getCause()
                    );
                }
            }
        }
    }
}
