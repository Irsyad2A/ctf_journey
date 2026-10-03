import java.io.*;
import java.lang.reflect.*;
import java.util.*;

public class SolveRecipes {

    static String run(String... cmd) throws Exception {
        Process p = new ProcessBuilder(cmd)
            .redirectErrorStream(true)
            .start();

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try (InputStream in = p.getInputStream()) {
            byte[] buf = new byte[8192];
            int n;
            while ((n = in.read(buf)) != -1)
                out.write(buf, 0, n);
        }

        p.waitFor();

        return out.toString();
    }

    static void call(Object l, String method, String s) {

        try {
            Method target = null;

            for (Method m : l.getClass().getDeclaredMethods()) {
                if (!m.getName().equals(method))
                    continue;

                Class<?>[] p = m.getParameterTypes();

                if (p.length == 1 && p[0] == String.class) {
                    target = m;
                    break;
                }
            }

            if (target == null) {
                System.out.println(method + "(" + s + ") -> <not found>");
                return;
            }

            target.setAccessible(true);

            Object r = target.invoke(l, s);

            System.out.println(
                method + "(" + s + ") -> " + String.valueOf(r)
            );

        } catch (Throwable e) {

            Throwable x = e;

            if (e instanceof InvocationTargetException &&
                ((InvocationTargetException)e).getCause() != null)
                x = ((InvocationTargetException)e).getCause();

            System.out.println(
                method + "(" + s + ") -> ERROR " +
                x.getClass().getName() +
                ": " + x.getMessage()
            );
        }
    }

    public static void main(String[] args) throws Exception {

        System.out.println("===== javap invokedynamic recipes =====");

        String javap = run(
            "javap",
            "-classpath",
            "../burhanquest.jar",
            "-v",
            "-p",
            "l"
        );

        String[] lines = javap.split("\\R");

        for (int i = 0; i < lines.length; i++) {

            if (!lines[i].contains("BootstrapMethods:"))
                continue;

            System.out.println();

            for (int j = i; j < Math.min(lines.length, i + 180); j++)
                System.out.println(lines[j]);

            break;
        }

        System.out.println();
        System.out.println("===== Runtime oracle =====");

        Class<?> L = Class.forName("l");

        Constructor<?> ctor = L.getDeclaredConstructor();
        ctor.setAccessible(true);

        Object obj = ctor.newInstance();

        String[] inputs = {
            "",
            "Q5",
            "Q5:frieren",
            "Q5|frieren",
            "Q5frieren",
            "frieren",
            "Frieren",
            "Segel Kuno",
            "Jembatan Tua",
            "95910",
            "bd3d174e3b60",
            "0b23aa7001b2"
        };

        for (String s : inputs) {

            System.out.println();
            System.out.println("INPUT=[" + s + "]");

            call(obj, "b", s);
            call(obj, "c", s);
            call(obj, "d", s);
            call(obj, "e", s);
            call(obj, "f", s);
            call(obj, "g", s);
        }
    }
}
