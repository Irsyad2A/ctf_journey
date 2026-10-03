import java.lang.reflect.Method;
import java.lang.reflect.Field;

public class DecodeGetenv {
    public static void main(String[] args) throws Exception {
        Class<?> c = Class.forName("l");

        Method dec = c.getDeclaredMethod("h", String.class);
        dec.setAccessible(true);

        /*
         * The two encrypted String constants are already visible
         * immediately before getenv() in javap. Paste the two constants here.
         *
         * We will obtain them automatically from l_verbose.javap instead,
         * so this Java program is only useful for manual verification.
         */
        System.out.println("decoder = " + dec);
    }
}
