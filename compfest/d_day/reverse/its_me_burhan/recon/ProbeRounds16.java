import java.util.*;

public class ProbeRounds16 {
    static String hex(byte[] x) {
        StringBuilder s = new StringBuilder();
        for (byte b : x) s.append(String.format("%02x", b & 0xff));
        return s.toString();
    }

    public static void main(String[] args) throws Exception {
        for (int round = 0; round < 7; round++) {
            System.out.println("=== ROUND " + round + " ===");

            for (int pos = 0; pos < 16; pos++) {
                byte[] in = new byte[16];
                in[pos] = (byte)0x41;

                byte[] out = p.a(round, in);

                System.out.printf(
                    "pos=%02d in=%s out=%s%n",
                    pos,
                    hex(in),
                    hex(out)
                );
            }
        }
    }
}
