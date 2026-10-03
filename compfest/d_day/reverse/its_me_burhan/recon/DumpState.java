import java.lang.reflect.*;
import java.util.*;

public class DumpState {
    public static void main(String[] args) throws Exception {
        Class<?> cls = Class.forName("l");

        Constructor<?> ctor = cls.getDeclaredConstructor();
        ctor.setAccessible(true);

        Object obj = ctor.newInstance();

        for (Field f : cls.getDeclaredFields()) {
            f.setAccessible(true);
            Object v = f.get(obj);

            if (v instanceof int[]) {
                System.out.println(f.getName() + " = " +
                    Arrays.toString((int[]) v));
            } else {
                System.out.println(f.getName() + " = " + v);
            }
        }

        Method m = cls.getDeclaredMethod("m");
        m.setAccessible(true);
        System.out.println("m() = " +
            Arrays.toString((int[])m.invoke(obj)));
    }
}
