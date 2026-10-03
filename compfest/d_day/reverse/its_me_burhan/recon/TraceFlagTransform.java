import java.lang.reflect.*;
import java.util.*;

public class TraceFlagTransform {
    static String hex(byte[] b) {
        StringBuilder s = new StringBuilder();
        for (byte x : b) s.append(String.format("%02x", x & 255));
        return s.toString();
    }

    static Object get(Object o, String field) throws Exception {
        Field f = o.getClass().getDeclaredField(field);
        f.setAccessible(true);
        return f.get(o);
    }

    static void setEnv(String key, String value) {
        try {
            Class<?> pe = Class.forName("java.lang.ProcessEnvironment");
            Field theEnvironment = pe.getDeclaredField("theEnvironment");
            theEnvironment.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String,String> env =
                (Map<String,String>) theEnvironment.get(null);
            env.put(key, value);

            Field cienv = pe.getDeclaredField("theCaseInsensitiveEnvironment");
            cienv.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String,String> cienvMap =
                (Map<String,String>) cienv.get(null);
            cienvMap.put(key, value);
        } catch (Exception ignored) {
            // On newer JVMs, environment replacement may be restricted.
            // Set BURHAN_FLAG from the shell before launching instead.
        }
    }

    public static void main(String[] args) throws Exception {
        String flag = args.length > 0 ? args[0] : "AAAAAAAAAAAAAAAA";

        // IMPORTANT:
        // The constructor reads the environment only when l is first created.
        // Therefore BURHAN_FLAG must normally be exported before java starts.

        Class<?> lc = Class.forName("l");

        Method singleton = lc.getDeclaredMethod("c");
        singleton.setAccessible(true);

        Object l = singleton.invoke(null);

        Field f = lc.getDeclaredField("f");
        f.setAccessible(true);

        Field u = lc.getDeclaredField("u");
        u.setAccessible(true);

        System.out.println("seed = " + get(l, "e"));
        System.out.println("flag = " + f.get(l));
        System.out.println("u    = " + Arrays.toString((int[])u.get(l)));

        Method out = lc.getDeclaredMethod("a");
        out.setAccessible(true);

        byte[] transformed = (byte[])out.invoke(l);

        System.out.println("l.a() length = " + transformed.length);
        System.out.println("l.a() hex    = " + hex(transformed));
    }
}
