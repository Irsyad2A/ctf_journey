import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class ProbeHash {
    public static void main(String[] args) throws Exception {
        byte[] in = "abc".getBytes(StandardCharsets.UTF_8);

        byte[] out = p.a(in);

        System.out.println("p.a(byte[]) length = " + out.length);
        System.out.println("p.a(byte[]) hex = " + hex(out));

        String[] algos = {
            "MD5",
            "SHA-1",
            "SHA-224",
            "SHA-256",
            "SHA-384",
            "SHA-512"
        };

        for (String algo : algos) {
            try {
                byte[] x = MessageDigest.getInstance(algo).digest(in);
                System.out.printf("%-8s %s%n", algo, hex(x));
            } catch (Exception ignored) {}
        }
    }

    static String hex(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (byte x : b)
            sb.append(String.format("%02x", x & 0xff));
        return sb.toString();
    }
}
