import java.lang.reflect.*;

public class VerifyRound35 {

    static byte[] hx(String s) {
        byte[] r = new byte[s.length()/2];
        for (int i=0; i<r.length; i++)
            r[i] = (byte)Integer.parseInt(s.substring(i*2,i*2+2),16);
        return r;
    }

    static String hex(byte[] x) {
        StringBuilder s = new StringBuilder();
        for (byte b:x)
            s.append(String.format("%02x", b & 255));
        return s.toString();
    }

    static byte[] call(int round, byte[] in) throws Exception {
        Class<?> p = Class.forName("p");

        for (Method m : p.getDeclaredMethods()) {
            if (!m.getName().equals("a")) continue;

            Class<?>[] t = m.getParameterTypes();

            if (t.length == 2 &&
                t[0] == int.class &&
                t[1] == byte[].class) {
                m.setAccessible(true);
                return (byte[])m.invoke(null, round, in);
            }
        }

        throw new RuntimeException("method not found");
    }

    static void test(int round, String a, String expected) throws Exception {
        byte[] in = hx(a);
        byte[] out = call(round, in);

        System.out.println("round " + round);
        System.out.println("in       = " + a);
        System.out.println("expected = " + expected);
        System.out.println("actual   = " + hex(out));
        System.out.println("MATCH    = " + hex(out).equals(expected));
        System.out.println();
    }

    public static void main(String[] args) throws Exception {

        // R3: stage2 -> stage3
        test(
            3,
            "be7b37f2ac651dd48a3ff3a65809b968",
            "7df66ee559ca3aa9157ee74db01273d0"
        );

        // R5: stage3 -> stage4
        test(
            5,
            "7df66ee559ca3aa9157ee74db01273d0",
            "77e24aaf0b5eaefb3f7ab5e710365970"
        );
    }
}
