// InspectL.java
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

public class InspectL {
    static Object field(Object obj, String name) throws Exception {
        Field f = obj.getClass().getDeclaredField(name);
        f.setAccessible(true);
        return f.get(obj);
    }

    static String hex(byte[] x) {
        StringBuilder s = new StringBuilder();
        for (byte b : x) s.append(String.format("%02x", b & 0xff));
        return s.toString();
    }

    public static void main(String[] args) throws Exception {
        Class<?> L = Class.forName("l");

        Method cm = L.getDeclaredMethod("c");
        cm.setAccessible(true);

        Object l = cm.invoke(null);

        System.out.println("=== L OBJECT ===");
        System.out.println(l);

        System.out.println("\n=== PRIVATE FIELDS ===");
        String[] fields = {
            "e", "f", "g", "h", "i", "j", "k", "n",
            "p", "q", "r", "s", "t", "v", "x"
        };

        for (String name : fields) {
            try {
                Object v = field(l, name);
                System.out.println(name + " = " + v);
            } catch (Exception ex) {
                System.out.println(name + " = <error: " + ex + ">");
            }
        }

        System.out.println("\n=== PUBLIC VALUES ===");

        for (String name : new String[]{"d","e","f","g","h","i"}) {
            for (Method m : L.getDeclaredMethods()) {
                if (m.getParameterCount() == 0 &&
                    m.getName().equals(name) &&
                    !m.getName().equals("c")) {

                    m.setAccessible(true);
                    Object v = m.invoke(l);
                    System.out.println(name + "() = " + v);
                    break;
                }
            }
        }

        System.out.println("\n=== l.a() ===");
        Method am = L.getDeclaredMethod("a");
        am.setAccessible(true);
        byte[] out = (byte[]) am.invoke(l);

        System.out.println("length = " + out.length);
        System.out.println("hex    = " + hex(out));
        System.out.println("ascii  = " +
            new String(out, java.nio.charset.StandardCharsets.UTF_8));

        Object p = field(l, "d");

        System.out.println("\n=== P OBJECT ===");
        System.out.println(p);

        Field pa = p.getClass().getDeclaredField("a");
        pa.setAccessible(true);

        byte[] state = (byte[]) pa.get(p);

        System.out.println("p.a[] length = " + state.length);
        System.out.println("p.a[] hex    = " + hex(state));
        System.out.println("p.a[] ascii  = " +
            new String(state, java.nio.charset.StandardCharsets.UTF_8));

        System.out.println("\n=== ENVIRONMENT ===");
        System.out.println("FLAG = " + System.getenv("FLAG"));
        System.out.println("CTFD_FLAG = " + System.getenv("CTFD_FLAG"));
    }
}
