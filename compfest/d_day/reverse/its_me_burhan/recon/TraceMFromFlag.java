import java.lang.reflect.*;
import java.util.*;

public class TraceMFromFlag {

    static String hex(byte[] x) {
        StringBuilder s = new StringBuilder();
        for (byte b : x)
            s.append(String.format("%02x", b & 0xff));
        return s.toString();
    }

    static void dump(Object obj, String field) throws Exception {
        Field f = obj.getClass().getDeclaredField(field);
        f.setAccessible(true);

        Object v = f.get(obj);

        if (v instanceof int[]) {
            System.out.println(field + " = " +
                Arrays.toString((int[]) v));
        }
        else if (v instanceof byte[]) {
            System.out.println(field + " = " +
                hex((byte[]) v));
        }
        else {
            System.out.println(field + " = " + v);
        }
    }

    public static void main(String[] args) throws Exception {

        Class<?> L = Class.forName("l");

        Constructor<?> ctor = L.getDeclaredConstructor();
        ctor.setAccessible(true);

        Object obj = ctor.newInstance();

        System.out.println("=== STATE BEFORE ===");
        dump(obj, "e");
        dump(obj, "f");
        dump(obj, "u");
        dump(obj, "w");
        dump(obj, "y");

        Method ll = L.getDeclaredMethod("l");
        ll.setAccessible(true);

        byte[][] blobs = (byte[][]) ll.invoke(obj);

        System.out.println();
        System.out.println("=== l.l() ===");

        for (int i = 0; i < blobs.length; i++) {
            System.out.printf(
                "[%d] %s%n",
                i,
                hex(blobs[i])
            );
        }

        Method mm = L.getDeclaredMethod("m");
        mm.setAccessible(true);

        int[] m = (int[]) mm.invoke(obj);

        System.out.println();
        System.out.println("=== l.m() ===");
        System.out.println(Arrays.toString(m));

        System.out.println();
        System.out.println("=== m AS HEX ===");

        for (int x : m)
            System.out.printf("%02x ", x);

        System.out.println();

        System.out.println();
        System.out.println("=== m AS 0-9A-V ===");

        final String alphabet =
            "0123456789ABCDEFGHIJKLMNOPQRSTUV";

        StringBuilder a = new StringBuilder();

        for (int x : m) {
            if (x >= 0 && x < alphabet.length())
                a.append(alphabet.charAt(x));
            else
                a.append('?');
        }

        System.out.println(a);

        System.out.println();
        System.out.println("=== m AS A-Z (mod 26) ===");

        StringBuilder az = new StringBuilder();

        for (int x : m)
            az.append((char)('A' + (x % 26)));

        System.out.println(az);

        System.out.println();
        System.out.println("=== m AS a-z (mod 26) ===");

        StringBuilder az2 = new StringBuilder();

        for (int x : m)
            az2.append((char)('a' + (x % 26)));

        System.out.println(az2);
    }
}
