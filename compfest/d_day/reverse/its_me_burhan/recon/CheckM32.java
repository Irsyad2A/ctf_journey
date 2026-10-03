import java.lang.reflect.*;
import java.util.*;

public class CheckM32 {

    static int[] getM() throws Exception {
        Class<?> L = Class.forName("l");

        Constructor<?> c = L.getDeclaredConstructor();
        c.setAccessible(true);

        Object obj = c.newInstance();

        Method m = L.getDeclaredMethod("m");
        m.setAccessible(true);

        return (int[]) m.invoke(obj);
    }

    public static void main(String[] args) throws Exception {

        int[] m = getM();

        System.out.println("m = " + Arrays.toString(m));

        System.out.print("mod32 = ");

        for (int x : m)
            System.out.printf("%02d ", x & 31);

        System.out.println();

        System.out.print("hex = ");

        for (int x : m)
            System.out.printf("%x", x & 31);

        System.out.println();

        System.out.print("5-bit packed = ");

        long packed = 0;

        for (int x : m)
            packed = (packed << 5) | (x & 31);

        System.out.printf(
            "%016x%n",
            packed
        );
    }
}
