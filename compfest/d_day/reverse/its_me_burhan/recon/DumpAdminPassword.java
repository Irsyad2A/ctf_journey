import java.lang.reflect.*;
import java.util.*;

public class DumpAdminPassword {
    public static void main(String[] args) throws Exception {
        Class<?> L = Class.forName("l");
        Class<?> P = Class.forName("p");

        Constructor<?> c = L.getDeclaredConstructor();
        c.setAccessible(true);
        Object l = c.newInstance();

        Field xf = L.getDeclaredField("x");
        xf.setAccessible(true);
        int x = xf.getInt(l);

        Field yf = L.getDeclaredField("y");
        yf.setAccessible(true);
        int[] y = (int[]) yf.get(l);

        Method alphabetMethod = P.getDeclaredMethod("b", int.class);
        alphabetMethod.setAccessible(true);

        String alphabet = (String) alphabetMethod.invoke(null, x);

        StringBuilder password = new StringBuilder();
        for (int v : y) {
            if (v < 0 || v >= alphabet.length()) {
                throw new IllegalStateException(
                    "y value out of alphabet range: " + v
                );
            }
            password.append(alphabet.charAt(v));
        }

        System.out.println("x       = " + x);
        System.out.println("alphabet= " + alphabet);
        System.out.println("y       = " + Arrays.toString(y));
        System.out.println("password= " + password);
    }
}
