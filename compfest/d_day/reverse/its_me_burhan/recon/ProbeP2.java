import java.lang.reflect.*;
import java.util.*;

public class ProbeP2 {
    static String hex(byte[] x) {
        StringBuilder s = new StringBuilder();

        for (byte b : x)
            s.append(String.format("%02x", b & 0xff));

        return s.toString();
    }

    public static void main(String[] args) throws Exception {
        Class<?> P = Class.forName("p");

        Method[] methods = P.getDeclaredMethods();

        String[] inputs = {
            "Q5",
            "Q05",
            "Segel Kuno",
            "P5",
            "frieren",
            "Q5:frieren",
            "Q5|frieren",
            "Q5frieren",
            "95910"
        };

        for (String input : inputs) {
            System.out.println();
            System.out.println("===== " + input + " =====");

            byte[] raw = input.getBytes(
                java.nio.charset.StandardCharsets.UTF_8
            );

            for (Method m : methods) {
                if (!m.getName().equals("b"))
                    continue;

                Class<?>[] t = m.getParameterTypes();

                try {
                    m.setAccessible(true);

                    if (Arrays.equals(
                        t,
                        new Class<?>[]{byte[].class}
                    )) {
                        Object r = m.invoke(null, raw);

                        if (r instanceof byte[])
                            System.out.println(
                                "b(byte[]) = " +
                                hex((byte[]) r)
                            );
                        else
                            System.out.println(
                                "b(byte[]) = " + r
                            );
                    }

                    if (Arrays.equals(
                        t,
                        new Class<?>[]{String.class}
                    )) {
                        Object r = m.invoke(null, input);

                        System.out.println(
                            "b(String) = " + r
                        );
                    }

                } catch (Throwable ignored) {
                }
            }
        }
    }
}
