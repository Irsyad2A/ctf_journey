import java.lang.reflect.*;
import java.util.Arrays;

public class DecodeEnvKeys {

    private static Method findDecoder(Class<?> cls) {
        for (Method m : cls.getDeclaredMethods()) {
            if (m.getParameterCount() == 1
                    && m.getParameterTypes()[0] == int[].class
                    && m.getReturnType() == String.class) {

                // Cari method static String(int[])
                if (Modifier.isStatic(m.getModifiers())) {
                    return m;
                }
            }
        }

        throw new RuntimeException("String(int[]) decoder not found");
    }

    public static void main(String[] args) throws Exception {

        Class<?> cls = Class.forName("l");

        Method decoder = findDecoder(cls);
        decoder.setAccessible(true);

        int[] key1 = {
            0, 1, 7, 9, 8, 9, 10, 30
        };

        int[] key2 = {
            0, 1, 2, 8, 3, 18, 27, 20,
            6, 7, 8, 22, 9, 27, 10, 29
        };

        String env1 = (String) decoder.invoke(null, (Object) key1);
        String env2 = (String) decoder.invoke(null, (Object) key2);

        System.out.println("decoder = " + decoder);
        System.out.println();

        System.out.println("KEY 1 array = " + Arrays.toString(key1));
        System.out.println("KEY 1 decoded = [" + env1 + "]");

        System.out.println();

        System.out.println("KEY 2 array = " + Arrays.toString(key2));
        System.out.println("KEY 2 decoded = [" + env2 + "]");
    }
}
