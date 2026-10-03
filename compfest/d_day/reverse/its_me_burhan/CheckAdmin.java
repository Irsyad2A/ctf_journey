import java.lang.reflect.*;

public class CheckAdmin {
    public static void main(String[] args) throws Exception {
        Class<?> oc = Class.forName("o");
        Object o = oc.getDeclaredConstructor().newInstance();

        Method user = oc.getMethod("getUsername");
        Method name = oc.getMethod("getName");

        System.out.println("username = " + user.invoke(o));
        System.out.println("name     = " + name.invoke(o));

        for (Field f : oc.getDeclaredFields()) {
            f.setAccessible(true);
            Object v = f.get(o);

            if (v instanceof long[]) {
                System.out.println(f.getName() + " = " +
                    java.util.Arrays.toString((long[])v));
            } else {
                System.out.println(f.getName() + " = " + v);
            }
        }

        Class<?> lc = Class.forName("l");
        for (Field f : lc.getDeclaredFields()) {
            f.setAccessible(true);
            Object v = f.get(null);

            if (v instanceof long[]) {
                System.out.println("l." + f.getName() + " = " +
                    java.util.Arrays.toString((long[])v));
            } else if (v instanceof int[]) {
                System.out.println("l." + f.getName() + " = " +
                    java.util.Arrays.toString((int[])v));
            } else if (v != null) {
                System.out.println("l." + f.getName() + " = " + v);
            }
        }
    }
}
