import java.lang.reflect.*;

public class DumpEnvName {
    public static void main(String[] args) throws Exception {
        Class<?> L = Class.forName("l");

        for (Method m : L.getDeclaredMethods()) {
            if (m.getName().equals("h")
                    && Modifier.isStatic(m.getModifiers())
                    && m.getParameterCount() == 1
                    && m.getParameterTypes()[0] == String.class) {

                m.setAccessible(true);

                // Candidate strings are difficult to identify automatically
                // from javap, so this simply confirms h() itself is callable.
                System.out.println("Found decoder: " + m);
            }
        }

        // Instantiate singleton so class initialization executes.
        Method c = L.getDeclaredMethod("c");
        c.setAccessible(true);
        Object obj = c.invoke(null);

        Field f = L.getDeclaredField("f");
        f.setAccessible(true);

        System.out.println("l.f = " + f.get(obj));
    }
}
