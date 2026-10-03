import java.lang.reflect.*;

public class TestAdmin {
    public static void main(String[] args) throws Exception {
        Class<?> L = Class.forName("l");

        Constructor<?> c = L.getDeclaredConstructor();
        c.setAccessible(true);
        Object l = c.newInstance();

        Method auth = L.getDeclaredMethod("a", String.class);
        auth.setAccessible(true);

        for (String s : args) {
            System.out.println(s + " -> " + auth.invoke(l, s));
        }
    }
}
