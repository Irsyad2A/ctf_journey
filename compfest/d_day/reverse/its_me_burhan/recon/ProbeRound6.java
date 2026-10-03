public class ProbeRound6 {
    static String hex(byte[] x) {
        StringBuilder s = new StringBuilder();
        for (byte b : x)
            s.append(String.format("%02x", b & 0xff));
        return s.toString();
    }

    static void test(String name, byte[] x) {
        byte[] y = p.a(6, x);
        System.out.println(name);
        System.out.println("in  = " + hex(x));
        System.out.println("out = " + hex(y));
        System.out.println();
    }

    public static void main(String[] args) {
        byte[] a = new byte[16];
        for (int i = 0; i < 16; i++)
            a[i] = (byte)i;
        test("SEQUENCE", a);

        byte[] b = new byte[16];
        for (int i = 0; i < 16; i++)
            b[i] = (byte)(0xf0 + i);
        test("F0-SEQUENCE", b);

        byte[] c = new byte[16];
        for (int i = 0; i < 16; i++)
            c[i] = (byte)0xaa;
        test("ALL-AA", c);

        byte[] d = new byte[16];
        for (int i = 0; i < 16; i++)
            d[i] = (byte)0xff;
        test("ALL-FF", d);

        byte[] e = new byte[16];
        for (int i = 0; i < 16; i++)
            e[i] = (byte)(0x10 + i * 7);
        test("RANDOM", e);
    }
}
