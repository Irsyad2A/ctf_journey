import java.util.*;

public class ProbeRounds {
    static String hex(byte[] x) {
        StringBuilder s = new StringBuilder();
        for (byte b : x) s.append(String.format("%02x", b & 0xff));
        return s.toString();
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== ROUND BYTE MAPPINGS ===");

        for (int round = 0; round < 7; round++) {
            System.out.println("ROUND " + round);

            boolean permutation = true;
            boolean[] seen = new boolean[256];

            for (int v = 0; v < 256; v++) {
                byte[] in = {(byte)v};
                byte[] out = p.a(round, in);

                int y = out[0] & 0xff;

                if (out.length != 1) {
                    System.out.println("ERROR round=" + round +
                        " input=" + v +
                        " output_length=" + out.length);
                    permutation = false;
                    continue;
                }

                if (seen[y]) permutation = false;
                seen[y] = true;

                System.out.printf("%02x -> %02x%n", v, y);
            }

            System.out.println("permutation = " + permutation);
            System.out.println();
        }
    }
}
