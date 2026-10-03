import java.lang.reflect.Method;
import java.util.Arrays;

public class DecodeKeysExact {
    public static void main(String[] args) throws Exception {
        Class<?> cls = Class.forName("l");

        Method decoder = cls.getDeclaredMethod("a", int[].class);
        decoder.setAccessible(true);

        /*
         * ARRAY 1 dan ARRAY 2 HARUS berasal langsung
         * dari constructor l pada bytecode.
         *
         * Jangan menebak nilainya.
         */
        int[][] arrays = {
            {0, 1, 7, 9, 8, 9, 10, 30},
            {0, 1, 2, 8, 3, 18, 27, 20,
             6, 7, 8, 22, 9, 27, 10, 29}
        };

        for (int i = 0; i < arrays.length; i++) {
            String decoded =
                (String) decoder.invoke(null, arrays[i]);

            System.out.println(
                "KEY " + (i + 1) +
                " array   = " + Arrays.toString(arrays[i])
            );

            System.out.println(
                "KEY " + (i + 1) +
                " decoded = " + printable(decoded)
            );
        }
    }

    private static String printable(String s) {
        StringBuilder out = new StringBuilder();

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (Character.isISOControl(c)) {
                out.append(String.format("\\u%04x", (int)c));
            } else {
                out.append(c);
            }
        }

        return out.toString();
    }
}
