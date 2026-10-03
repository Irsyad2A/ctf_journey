public class ProbeLValues {
    public static void main(String[] args) {
        l x = l.c();

        System.out.println("=== L BASIC ===");
        System.out.println("d() = " + x.d());
        System.out.println("e() = " + x.e());
        System.out.println("f() = " + x.f());
        System.out.println("g() = " + x.g());
        System.out.println("h() = " + x.h());
        System.out.println("i() = " + x.i());

        System.out.println();
        System.out.println("=== l.a() BYTE ARRAY ===");
        try {
            byte[] data = x.a();
            System.out.println("length = " + data.length);
            System.out.println("hex    = " + hex(data));
            System.out.println("ascii  = " + printable(data));
        } catch (Throwable t) {
            t.printStackTrace();
        }

        String[] quests = {
            "Pembersihan Hutan",
            "Reruntuhan Batu",
            "Teror Rawa",
            "Badai Beku",
            "Segel Kuno",
            "Perburuan Kelabu",
            "Ekspedisi Gua",
            "Sarang Racun",
            "Besi Berkarat",
            "Kobaran Abadi",
            "Bisikan Puing",
            "Jalur Berbisa",
            "Puncak Gunung",
            "Tebing Berangin",
            "Jembatan Tua",
            "Ritual Beku",
            "Padang Terlarang",
            "Kedalaman Danau"
        };

        System.out.println();
        System.out.println("=== QUEST CHECKS ===");

        for (String q : quests) {
            System.out.println();
            System.out.println("[" + q + "]");

            try {
                System.out.println("a(q) = " + x.a(q));
            } catch (Throwable t) {
                System.out.println("a(q) ERROR = " + t);
            }

            try {
                System.out.println("b(q) = " + x.b(q));
            } catch (Throwable t) {
                System.out.println("b(q) ERROR = " + t);
            }

            try {
                System.out.println("c(q) = " + x.c(q));
            } catch (Throwable t) {
                System.out.println("c(q) ERROR = " + t);
            }

            try {
                System.out.println("d(q) = " + x.d(q));
            } catch (Throwable t) {
                System.out.println("d(q) ERROR = " + t);
            }

            try {
                System.out.println("e(q) = " + x.e(q));
            } catch (Throwable t) {
                System.out.println("e(q) ERROR = " + t);
            }

            try {
                System.out.println("f(q) = " + x.f(q));
            } catch (Throwable t) {
                System.out.println("f(q) ERROR = " + t);
            }

            try {
                System.out.println("g(q) = " + x.g(q));
            } catch (Throwable t) {
                System.out.println("g(q) ERROR = " + t);
            }
        }

        System.out.println();
        System.out.println("=== m[] ===");
        try {
            m[] ms = x.j();
            System.out.println("size = " + ms.length);
            for (int i = 0; i < ms.length; i++) {
                System.out.println("m[" + i + "] = " + ms[i]);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }

        System.out.println();
        System.out.println("=== n[] ===");
        try {
            n[] ns = x.k();
            System.out.println("size = " + ns.length);
            for (int i = 0; i < ns.length; i++) {
                System.out.println("n[" + i + "] = " + ns[i]);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    static String hex(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (byte x : b) {
            sb.append(String.format("%02x", x & 0xff));
        }
        return sb.toString();
    }

    static String printable(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (byte x : b) {
            int c = x & 0xff;
            sb.append(c >= 32 && c <= 126 ? (char)c : '.');
        }
        return sb.toString();
    }
}
