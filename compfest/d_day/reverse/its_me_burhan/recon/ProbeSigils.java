import java.lang.reflect.*;
import java.util.*;

public class ProbeSigils {
    static Object call(Method m, Object... args) throws Exception {
        m.setAccessible(true);
        return m.invoke(null, args);
    }

    static String hex(byte[] x) {
        StringBuilder s = new StringBuilder();
        for (byte b : x)
            s.append(String.format("%02x", b & 0xff));
        return s.toString();
    }

    public static void main(String[] args) throws Exception {
        Class<?> P = Class.forName("p");

        for (Method m : P.getDeclaredMethods()) {
            String n = m.getName();
            if (!n.equals("a") && !n.equals("b") && !n.equals("c"))
                continue;

            System.out.println("METHOD: " + m);

            try {
                Class<?>[] t = m.getParameterTypes();

                if (Arrays.equals(t, new Class<?>[]{String.class})) {
                    Object r = call(m, "Q5");

                    if (r instanceof byte[])
                        System.out.println("  Q5 -> " + hex((byte[]) r));
                    else
                        System.out.println("  Q5 -> " + r);
                }

                else if (Arrays.equals(t, new Class<?>[]{byte[].class})) {
                    byte[] q = "Q5".getBytes();
                    Object r = call(m, q);

                    if (r instanceof byte[])
                        System.out.println("  bytes(Q5) -> " + hex((byte[]) r));
                    else
                        System.out.println("  bytes(Q5) -> " + r);
                }

                else if (Arrays.equals(t, new Class<?>[]{byte[].class, byte[].class})) {
                    byte[] q = "Q5".getBytes();
                    byte[] f = "frieren".getBytes();

                    Object r = call(m, q, f);

                    if (r instanceof byte[])
                        System.out.println("  Q5,frieren -> " + hex((byte[]) r));
                    else
                        System.out.println("  Q5,frieren -> " + r);
                }

                else if (Arrays.equals(t, new Class<?>[]{String.class,int.class,int.class})) {
                    Object r = call(m, "Q5", 0, 100000);
                    System.out.println("  Q5 range -> " + r);
                }

            } catch (Throwable e) {
                System.out.println("  ERROR: " + e.getCause());
            }
        }
    }
}
