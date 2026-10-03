import java.lang.reflect.*;

public class DumpO {
    static String read(Object obj, String field) throws Exception {
        Class<?> c = obj.getClass();

        while (c != null) {
            try {
                Field f = c.getDeclaredField(field);
                f.setAccessible(true);
                return String.valueOf(f.get(obj));
            } catch (NoSuchFieldException e) {
                c = c.getSuperclass();
            }
        }

        return "<missing>";
    }

    public static void main(String[] args) throws Exception {
        Class<?> O = Class.forName("o");

        Constructor<?> ctor = O.getDeclaredConstructor();
        ctor.setAccessible(true);

        Object o = ctor.newInstance();

        System.out.println("class    = " + o.getClass().getName());
        System.out.println("name     = " + read(o, "a"));
        System.out.println("username = " + read(o, "b"));
        System.out.println("password = " + read(o, "c"));

        System.out.println("username getter = " +
            ((b)o).getUsername());
        System.out.println("name getter = " +
            ((b)o).getName());
    }
}
