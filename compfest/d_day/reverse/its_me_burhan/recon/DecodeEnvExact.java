import java.lang.reflect.Method;
import java.util.Arrays;

public class DecodeEnvExact {

    public static void main(String[] args) throws Exception {

        Class<?> L = Class.forName("l");

        /*
         * l.a(int[]) adalah decoder private static yang dipanggil
         * tepat sebelum System.getenv().
         */
        Method decoder = L.getDeclaredMethod("a", int[].class);
        decoder.setAccessible(true);

        /*
         * Array pertama diambil LANGSUNG dari constructor l().
         *
         * Dari pola:
         *
         *   dup
         *   <expr>        -> index
         *   <expr>        -> value
         *   iastore
         *
         * hasil akhirnya adalah:
         *
         * [24, 15, 8, 18, 27, 20, 5, 9, 31, 31, 30]
         */
        int[] key1 = {
            24, 15, 8, 18, 27, 20,
            5, 9, 31, 31, 30
        };

        /*
         * Array kedua juga direkonstruksi dari constructor.
         *
         * Hasil yang benar dari blok bytecode yang kamu kirim:
         *
         * [24, 15, 8, 18, 27, 20, 5, 28, 22, 27, 29]
         */
        int[] key2 = {
            24, 15, 8, 18, 27, 20,
            5, 28, 22, 27, 29
        };

        String decoded1 =
            (String) decoder.invoke(null, (Object) key1);

        String decoded2 =
            (String) decoder.invoke(null, (Object) key2);

        System.out.println("=== EXACT ENVIRONMENT KEYS ===");
        System.out.println("key1 = " + Arrays.toString(key1));
        System.out.println("key1 decoded = " + repr(decoded1));

        System.out.println();

        System.out.println("key2 = " + Arrays.toString(key2));
        System.out.println("key2 decoded = " + repr(decoded2));

        System.out.println();
        System.out.println("=== SYSTEM ENV LOOKUP ===");
        System.out.println(
            decoded1 + " = " +
            (System.getenv(decoded1) == null
                ? "<not set>"
                : System.getenv(decoded1))
        );

        System.out.println(
            decoded2 + " = " +
            (System.getenv(decoded2) == null
                ? "<not set>"
                : System.getenv(decoded2))
        );
    }

    static String repr(String s) {
        StringBuilder out = new StringBuilder();

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c >= 32 && c <= 126) {
                out.append(c);
            } else {
                out.append(String.format("\\u%04x", (int)c));
            }
        }

        return out.toString();
    }
}
