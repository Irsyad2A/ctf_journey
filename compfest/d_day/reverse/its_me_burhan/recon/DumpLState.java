import java.lang.reflect.*;

public class DumpLState {
    public static void main(String[] args) throws Exception {
        Class<?> lc = Class.forName("l");
        Constructor<?> ctor = lc.getDeclaredConstructor();
        ctor.setAccessible(true);

        Object obj = ctor.newInstance();

        for (Field f : lc.getDeclaredFields()) {
            f.setAccessible(true);

            Object value;
            try {
                value = Modifier.isStatic(f.getModifiers())
                    ? f.get(null)
                    : f.get(obj);
            } catch (Throwable t) {
                value = "<ERR " + t + ">";
            }

            System.out.println(
                "FIELD " + f.getName()
                + " type=" + f.getType().getName()
                + " value=" + value
            );
        }

        for (Method m : lc.getDeclaredMethods()) {
            System.out.println("METHOD " + m);
        }
    }
}
