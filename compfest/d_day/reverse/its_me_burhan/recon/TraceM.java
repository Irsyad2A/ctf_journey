import java.lang.reflect.*;
import java.util.*;

public class TraceM {

    public static void main(String[] args) throws Exception {

        Class<?> L = Class.forName("l");

        Constructor<?> ctor = L.getDeclaredConstructor();
        ctor.setAccessible(true);

        Object obj = ctor.newInstance();

        /*
         * Ambil hasil l()
         */
        Method lm = L.getDeclaredMethod("l");
        lm.setAccessible(true);

        byte[][] blobs = (byte[][]) lm.invoke(obj);

        /*
         * Ambil w[]
         */
        Field wf = L.getDeclaredField("w");
        wf.setAccessible(true);

        int[] w = (int[]) wf.get(obj);

        System.out.println("=== INPUT BLOBS ===");

        for (int i = 0; i < blobs.length; i++) {
            System.out.printf(
                "blob[%d] = %s%n",
                i,
                hex(blobs[i])
            );
        }

        System.out.println();
        System.out.println("w = " + Arrays.toString(w));

        /*
         * Ambil class p.
         */
        Class<?> P = Class.forName("p");

        /*
         * p.b(byte[], byte[])
         */
        Method pb = P.getDeclaredMethod(
            "b",
            byte[].class,
            byte[].class
        );
        pb.setAccessible(true);

        /*
         * p.a(byte[], int)
         *
         * Ini adalah method yang dipanggil
         * di akhir m().
         */
        Method pai = P.getDeclaredMethod(
            "a",
            byte[].class,
            int.class
        );
        pai.setAccessible(true);

        /*
         * Kita reproduksi bagian penting m()
         * secara eksplisit.
         *
         * Start dari blob yang dipilih oleh w[0].
         */
        byte[] acc = blobs[w[0]];

        System.out.println();
        System.out.println("=== CHAIN ===");

        System.out.printf(
            "step 0 : w[0]=%d -> %s%n",
            w[0],
            hex(acc)
        );

        /*
         * Untuk sementara kita cek chain sederhana
         * acc = p.b(acc, blobs[w[i]])
         *
         * Ini memungkinkan kita melihat apakah
         * output m() memang berasal dari permutation w.
         */
        for (int i = 1; i < w.length; i++) {

            int idx = w[i];

            byte[] next = blobs[idx];

            byte[] old = acc;

            acc = (byte[]) pb.invoke(
                null,
                old,
                next
            );

            System.out.printf(
                "step %d : w[%d]=%d%n",
                i,
                i,
                idx
            );

            System.out.println(
                "         input A = " + hex(old)
            );

            System.out.println(
                "         input B = " + hex(next)
            );

            System.out.println(
                "         output  = " + hex(acc)
            );
        }

        /*
         * Coba berbagai panjang output.
         *
         * m() ternyata memanggil p.a([BI)[I,
         * sehingga kemungkinan integer kedua
         * adalah jumlah karakter/word yang dihasilkan.
         */
        System.out.println();
        System.out.println("=== p.a(byte[], int) ===");

        for (int n = 1; n <= 32; n++) {

            try {

                int[] out = (int[]) pai.invoke(
                    null,
                    acc,
                    n
                );

                System.out.printf(
                    "n=%2d -> %s%n",
                    n,
                    Arrays.toString(out)
                );

            } catch (InvocationTargetException e) {
                break;
            }
        }

        /*
         * Panggil m() asli sebagai pembanding.
         */
        Method mm = L.getDeclaredMethod("m");
        mm.setAccessible(true);

        int[] real = (int[]) mm.invoke(obj);

        System.out.println();
        System.out.println("=== REAL m() ===");
        System.out.println(
            Arrays.toString(real)
        );
    }

    static String hex(byte[] data) {

        StringBuilder sb = new StringBuilder();

        for (byte b : data) {
            sb.append(
                String.format("%02x", b & 0xff)
            );
        }

        return sb.toString();
    }
}
