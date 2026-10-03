import java.lang.reflect.*;
import java.security.*;
import java.util.*;

public class VerifyAuth {
    public static void main(String[] args) throws Exception {
        String seed = "DEV-SEED";
        String password = "K76LD64XY3URX4RM";

        Class<?> P = Class.forName("p");

        Constructor<?> pc = P.getConstructor(String.class);
        Object p = pc.newInstance(seed);

        Method digest = P.getDeclaredMethod("a", byte[].class);
        digest.setAccessible(true);

        Method chunks = P.getDeclaredMethod(
            "a", byte[].class, int.class
        );
        chunks.setAccessible(true);

        Method stage = P.getDeclaredMethod(
            "a", int.class, int[].class
        );
        stage.setAccessible(true);

        // Same concatenation used by p.d(String):
        byte[] sb = seed.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        byte[] pb = password.getBytes(java.nio.charset.StandardCharsets.UTF_8);

        byte[] joined = new byte[sb.length + pb.length];
        System.arraycopy(sb, 0, joined, 0, sb.length);
        System.arraycopy(pb, 0, joined, sb.length, pb.length);

        byte[] hash = (byte[])digest.invoke(null, (Object)joined);

        System.out.printf("SHA256 = %064x%n",
            new java.math.BigInteger(1, hash));

        // IMPORTANT: try 16, because y.length == 16.
        int[] state =
            (int[])chunks.invoke(null, (Object)hash, 16);

        System.out.println(
            "initial = " + Arrays.toString(state)
        );

        int[] order = {4, 2, 6, 3, 5, 0, 1};

        for (int s : order) {
            state = (int[])stage.invoke(null, s, state);
            System.out.println(
                "T" + s + " = " + Arrays.toString(state)
            );
        }

        int[] y = {
            28,4,4,3,27,9,3,1,
            7,1,19,11,24,21,27,1
        };

        System.out.println("y      = " + Arrays.toString(y));
        System.out.println(
            "MATCH  = " + Arrays.equals(state, y)
        );
    }
}
