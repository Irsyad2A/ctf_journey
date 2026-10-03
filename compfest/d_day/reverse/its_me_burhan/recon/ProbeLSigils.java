import java.lang.reflect.*;
import java.nio.charset.StandardCharsets;

public class ProbeLSigils {

    static Object invoke(Object obj, Method m, Object... args) {
        try {
            m.setAccessible(true);
            return m.invoke(obj, args);
        } catch (Throwable e) {
            Throwable x = e;

            if (e instanceof InvocationTargetException &&
                ((InvocationTargetException)e).getCause() != null) {
                x = ((InvocationTargetException)e).getCause();
            }

            return "<ERROR: " + x.getClass().getSimpleName() +
                   ": " + x.getMessage() + ">";
        }
    }

    static String show(Object x) {

        if (x == null)
            return "null";

        if (x instanceof byte[]) {
            byte[] b = (byte[]) x;

            StringBuilder h = new StringBuilder();

            for (byte v : b)
                h.append(String.format("%02x", v & 0xff));

            return "byte[" + b.length + "] hex=" + h;
        }

        return x.toString();
    }

    public static void main(String[] args) throws Exception {

        Class<?> L = Class.forName("l");

        Constructor<?> ctor = L.getDeclaredConstructor();
        ctor.setAccessible(true);

        Object obj = ctor.newInstance();

        String[] inputs = {
            "",
            "Q5",
            "Q05",
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
            System.out.println("================================================");
            System.out.println("INPUT = [" + s + "]");
            System.out.println("================================================");

            for (Method m : L.getDeclaredMethods()) {

                Class<?>[] p = m.getParameterTypes();

                if (p.length != 1 || p[0] != String.class)
                    continue;

                String name = m.getName();

                if (!(name.equals("b") ||
                      name.equals("c") ||
                      name.equals("d") ||
                      name.equals("e") ||
                      name.equals("f") ||
                      name.equals("g") ||
                      name.equals("a")))
                    continue;

                Object r = invoke(obj, m, s);

                System.out.printf(
                    "%s %s(String) -> %s%n",
                    Modifier.toString(m.getModifiers()),
                    name,
                    show(r)
                );
            }
        }
    }
}
