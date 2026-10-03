import java.lang.reflect.*;
import java.util.*;

public class ProbeState {

    public static void main(String[] args) throws Exception {

        // Ambil class "l" dari burhanquest.jar
        Class<?> L = Class.forName("l");

        // Ambil constructor private/default
        Constructor<?> ctor = L.getDeclaredConstructor();
        ctor.setAccessible(true);

        // Buat object l
        Object obj = ctor.newInstance();

        System.out.println("=== PRIVATE STATE ===");

        /*
         * Baca beberapa field internal class l.
         *
         * Ini penting karena sebelumnya kita menemukan:
         *
         * x = 0
         * w = [5,3,1,6,4,2,7,8,0]
         * u = [4,2,6,3,5,0,1]
         * t = 3232
         * g = 3042
         * h = 8
         * p = Q15
         * q = Q15>Q13
         * r = Q15>Q13>Q11
         * s = 4041
         */

        for (String name : new String[] {
                "x", "w", "u", "t", "g",
                "h", "p", "q", "r", "s"
        }) {

            try {
                Field f = L.getDeclaredField(name);
                f.setAccessible(true);

                Object v = f.get(obj);

                if (v instanceof int[]) {
                    System.out.println(
                        name + " = " +
                        Arrays.toString((int[]) v)
                    );

                } else if (v instanceof long[]) {
                    System.out.println(
                        name + " = " +
                        Arrays.toString((long[]) v)
                    );

                } else if (v instanceof byte[]) {
                    System.out.println(
                        name + " = " +
                        Arrays.toString((byte[]) v)
                    );

                } else {
                    System.out.println(name + " = " + v);
                }

            } catch (NoSuchFieldException ignored) {
                // Field tidak ada, lanjut
            }
        }

        System.out.println();
        System.out.println("=== l() OUTPUT ===");

        /*
         * Panggil private method:
         *
         * private byte[][] l()
         *
         * Method ini membuat 9 byte-array.
         */
        Method lm = L.getDeclaredMethod("l");
        lm.setAccessible(true);

        byte[][] blobs = (byte[][]) lm.invoke(obj);

        for (int i = 0; i < blobs.length; i++) {

            System.out.printf(
                "[%d] len=%d hex=%s ascii=%s%n",
                i,
                blobs[i].length,
                hex(blobs[i]),
                ascii(blobs[i])
            );
        }

        System.out.println();
        System.out.println("=== m() OUTPUT ===");

        /*
         * Panggil:
         *
         * private int[] m()
         *
         * Sebelumnya kita salah cast ke Object[].
         * Sekarang kita cast ke int[].
         */
        Method mm = L.getDeclaredMethod("m");
        mm.setAccessible(true);

        int[] result = (int[]) mm.invoke(obj);

        System.out.println(
            "m() = " +
            Arrays.toString(result)
        );

        System.out.println();
        System.out.println("=== W -> BLOB MAPPING ===");

        /*
         * Ambil permutation w.
         */
        int[] w = getIntArray(L, obj, "w");

        /*
         * Tampilkan blob mana yang dipilih oleh w.
         */
        for (int i = 0; i < w.length; i++) {

            int idx = w[i];

            System.out.printf(
                "w[%d] = %d -> hex=%s ascii=%s%n",
                i,
                idx,
                hex(blobs[idx]),
                ascii(blobs[idx])
            );
        }
    }

    /*
     * Helper untuk mengambil int[] field private.
     */
    static int[] getIntArray(
            Class<?> c,
            Object obj,
            String name
    ) throws Exception {

        Field f = c.getDeclaredField(name);
        f.setAccessible(true);

        return (int[]) f.get(obj);
    }

    /*
     * byte[] -> hexadecimal.
     */
    static String hex(byte[] x) {

        StringBuilder sb = new StringBuilder();

        for (byte b : x) {
            sb.append(
                String.format(
                    "%02x",
                    b & 0xff
                )
            );
        }

        return sb.toString();
    }

    /*
     * byte[] -> ASCII yang mudah dibaca.
     * Byte printable ditampilkan sebagai karakter.
     * Yang lainnya menjadi '.'.
     */
    static String ascii(byte[] x) {

        StringBuilder sb = new StringBuilder();

        for (byte b : x) {

            int v = b & 0xff;

            if (v >= 32 && v <= 126) {
                sb.append((char) v);
            } else {
                sb.append('.');
            }
        }

        return sb.toString();
    }
}
