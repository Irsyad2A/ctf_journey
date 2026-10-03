import java.lang.reflect.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.*;

public class TestDigest {
    public static void main(String[] args) throws Exception {
        String seed = "DEV-SEED";
        String password = "K76LD64XY3URX4RM";

        byte[] s = seed.getBytes(StandardCharsets.UTF_8);
        byte[] p = password.getBytes(StandardCharsets.UTF_8);

        byte[] joined = new byte[s.length + p.length];
        System.arraycopy(s, 0, joined, 0, s.length);
        System.arraycopy(p, 0, joined, s.length, p.length);

        byte[] hash = MessageDigest.getInstance("SHA-256").digest(joined);

        Class<?> P = Class.forName("p");
        Method chunks = P.getDeclaredMethod("a", byte[].class, int.class);
        chunks.setAccessible(true);

        System.out.println("hash = " + HexFormat.of().formatHex(hash));

        for (int n : new int[]{16, 23, 32}) {
            int[] out = (int[]) chunks.invoke(null, hash, n);
            System.out.println(
                "n=" + n +
                " len=" + out.length +
                " => " + Arrays.toString(out)
            );
        }
    }
}
