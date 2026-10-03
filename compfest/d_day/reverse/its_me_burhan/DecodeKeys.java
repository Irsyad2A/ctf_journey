import java.lang.reflect.Method;

public class DecodeKeys {
    public static void main(String[] args) throws Exception {
        Class<?> c = Class.forName("l");

        Method m = c.getDeclaredMethod("a", int[].class);
        m.setAccessible(true);

        int[] k1 = {
            0,24,1,15,2,8,3,18,27,20,6,7,9,8,31,9,31,10,30
        };

        int[] k2 = {
            0,24,1,15,2,8,3,18,27,20,6,7,28,8,22,9,27,10,29
        };

        String s1 = (String)m.invoke(null, (Object)k1);
        String s2 = (String)m.invoke(null, (Object)k2);

        System.out.println("KEY1 = [" + s1 + "]");
        System.out.println("KEY2 = [" + s2 + "]");

        System.out.println("\nHEX:");
        for (char x : s1.toCharArray())
            System.out.printf("%04x ", (int)x);
        System.out.println();

        for (char x : s2.toCharArray())
            System.out.printf("%04x ", (int)x);
        System.out.println();
    }
}
