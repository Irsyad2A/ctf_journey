import java.lang.reflect.*;
import java.util.*;

public class TraceAuth {
    public static void main(String[] args) throws Exception {
        String password = args.length > 0
            ? args[0]
            : "K76LD64XY3URX4RM";

        Class<?> L = Class.forName("l");

        Constructor<?> ctor = L.getDeclaredConstructor();
        ctor.setAccessible(true);
        Object obj = ctor.newInstance();

        Field uF = L.getDeclaredField("u");
        uF.setAccessible(true);

        Field yF = L.getDeclaredField("y");
        yF.setAccessible(true);

        Field xF = L.getDeclaredField("x");
        xF.setAccessible(true);

        int[] u = (int[]) uF.get(obj);
        int[] y = (int[]) yF.get(obj);
        int x = xF.getInt(obj);

        System.out.println("password = " + password);
        System.out.println("x        = " + x);
        System.out.println("u        = " + Arrays.toString(u));
        System.out.println("y        = " + Arrays.toString(y));

        Method auth = L.getDeclaredMethod("a", String.class);
        auth.setAccessible(true);

        System.out.println("AUTH     = " + auth.invoke(obj, password));
    }
}
