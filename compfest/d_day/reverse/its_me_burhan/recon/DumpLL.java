import java.lang.reflect.*;
import java.util.*;

public class DumpLL {
    static String hex(byte[] x) {
        StringBuilder s = new StringBuilder();
        for (byte b : x) {
            s.append(String.format("%02x", b & 0xff));
        }
        return s.toString();
    }

    public static void main(String[] args) throws Exception {
        Class<?> L = Class.forName("l");

        Constructor<?> ctor = L.getDeclaredConstructor();
        ctor.setAccessible(true);

        Object obj = ctor.newInstance();

        Method ll = L.getDeclaredMethod("l");
        ll.setAccessible(true);

        byte[][] blobs = (byte[][]) ll.invoke(obj);

        System.out.println("=== l.l() RAW BLOBS ===");

        for (int i = 0; i < blobs.length; i++) {
            byte[] b = blobs[i];

            System.out.printf(
                "[%d] len=%d hex=%s%n",
                i,
                b.length,
                hex(b)
            );
        }

        Field wF = L.getDeclaredField("w");
        wF.setAccessible(true);

        int[] w = (int[]) wF.get(obj);

        System.out.println();
        System.out.println("=== W PERMUTATION ===");
        System.out.println(Arrays.toString(w));

        System.out.println();
        System.out.println("=== W -> BLOB ===");

        for (int i = 0; i < w.length; i++) {
            int idx = w[i];

            System.out.printf(
                "w[%d]=%d -> %s%n",
                i,
                idx,
                hex(blobs[idx])
            );
        }
    }
}
