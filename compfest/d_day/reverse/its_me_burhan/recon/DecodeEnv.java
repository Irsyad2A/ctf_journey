import java.lang.reflect.*;

public class DecodeEnv {
    public static void main(String[] args) throws Exception {
        Class<?> cls = Class.forName("l");

        /*
         * h(String) is the string decoder used by the constructor.
         * We invoke it directly instead of manually reproducing
         * the obfuscation.
         */
        Method h = null;

        for (Method m : cls.getDeclaredMethods()) {
            if (m.getName().equals("h")
                    && Modifier.isStatic(m.getModifiers())
                    && m.getParameterCount() == 1
                    && m.getParameterTypes()[0] == String.class) {
                h = m;
                break;
            }
        }

        if (h == null) {
            throw new RuntimeException("decoder h(String) not found");
        }

        h.setAccessible(true);

        System.out.println("decoder = " + h);
    }
}
