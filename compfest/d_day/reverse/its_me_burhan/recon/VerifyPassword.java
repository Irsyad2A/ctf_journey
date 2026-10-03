import java.lang.reflect.*;

public class VerifyPassword {
    public static void main(String[] args) throws Exception {
        Class<?> c = Class.forName("l");

        Constructor<?> ctor = c.getDeclaredConstructor();
        ctor.setAccessible(true);
        Object obj = ctor.newInstance();

        Method m = c.getDeclaredMethod("a", String.class);
        m.setAccessible(true);

        String pw = args.length > 0 ? args[0] : "4EED3JDBHBTLYV3B";

        System.out.println("password = " + pw);
        System.out.println("length   = " + pw.length());
        System.out.println("result   = " + m.invoke(obj, pw));

        Field y = c.getDeclaredField("y");
        y.setAccessible(true);

        int[] target = (int[]) y.get(obj);

        System.out.print("y        = [");
        for (int i = 0; i < target.length; i++) {
            if (i != 0) System.out.print(", ");
            System.out.print(target[i]);
        }
        System.out.println("]");
    }
}
