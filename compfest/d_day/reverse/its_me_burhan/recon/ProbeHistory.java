import java.lang.reflect.*;
import java.util.*;

public class ProbeHistory {

    public static void main(String[] args) throws Exception {

        Class<?> wandererCls = Class.forName("entities.Wanderer");
        Class<?> lCls = Class.forName("l");

        // ============================================================
        // 1. Buat object Wanderer
        // ============================================================
        Constructor<?> wc = wandererCls.getConstructor(
            int.class,
            String.class,
            String.class,
            String.class,
            double.class,
            double.class,
            double.class
        );

        Object wanderer = wc.newInstance(
            8,
            "Frieren",
            "frieren",
            "frieren",
            9003.0,
            919.0,
            474.0
        );

        System.out.println("=== WANDERER ===");
        System.out.println("class    = " + wanderer.getClass().getName());
        System.out.println("id       = " +
            wandererCls.getMethod("getId").invoke(wanderer));
        System.out.println("name     = " +
            wandererCls.getMethod("getName").invoke(wanderer));
        System.out.println("username = " +
            wandererCls.getMethod("getUsername").invoke(wanderer));

        // ============================================================
        // 2. Dapatkan singleton l
        // ============================================================
        Method lc = lCls.getDeclaredMethod("c");
        lc.setAccessible(true);

        Object l = lc.invoke(null);

        // ============================================================
        // 3. Ambil semua quest dari l.k()
        // ============================================================
        Method getQuests = lCls.getMethod("k");
        Object[] quests = (Object[]) getQuests.invoke(l);

        Object q5 = quests[4];

        System.out.println();
        System.out.println("=== QUEST Q5 ===");
        System.out.println("class = " + q5.getClass().getName());
        System.out.println("super = " +
            q5.getClass().getSuperclass().getName());
        System.out.println("value = " + q5);

        // ============================================================
        // 4. Cari method Wanderer.a(...) yang menerima Q5
        //
        // Kita jangan hardcode Class.forName("f"), karena sebelumnya
        // itulah titik yang menyebabkan argument mismatch.
        // ============================================================
        Method addHistory = null;

        for (Method m : wandererCls.getMethods()) {

            if (!m.getName().equals("a"))
                continue;

            Class<?>[] params = m.getParameterTypes();

            if (params.length != 1)
                continue;

            System.out.println();
            System.out.println("candidate method = " + m);
            System.out.println("parameter        = " + params[0].getName());
            System.out.println("accept Q5        = " +
                params[0].isAssignableFrom(q5.getClass()));

            if (params[0].isAssignableFrom(q5.getClass())) {
                addHistory = m;
            }
        }

        if (addHistory == null) {
            throw new RuntimeException(
                "Tidak menemukan Wanderer.a(...) yang menerima object Q5"
            );
        }

        // ============================================================
        // 5. Masukkan Q5 ke completedQuestHistory
        // ============================================================
        System.out.println();
        System.out.println("=== ADD HISTORY ===");
        System.out.println("using = " + addHistory);

        addHistory.invoke(wanderer, q5);

        // ============================================================
        // 6. Dump history
        // ============================================================
        Method getHistory =
            wandererCls.getMethod("getCompletedQuestHistory");

        List<?> history =
            (List<?>) getHistory.invoke(wanderer);

        System.out.println();
        System.out.println("=== COMPLETED HISTORY ===");
        System.out.println("size = " + history.size());

        for (int i = 0; i < history.size(); i++) {

            Object quest = history.get(i);

            System.out.println(
                "[" + i + "] " +
                "class=" + quest.getClass().getName() +
                " value=" + quest
            );

            // Cari getId secara langsung dari object quest
            Method getId =
                quest.getClass().getMethod("getId");

            System.out.println(
                "     getId() = " + getId.invoke(quest)
            );
        }

        // ============================================================
        // 7. Panggil l.a(Wanderer)
        //
        // Ini adalah method:
        // public static String l.a(entities.Wanderer)
        // ============================================================
        Method historyString =
            lCls.getMethod("a", wandererCls);

        String derived =
            (String) historyString.invoke(null, wanderer);

        System.out.println();
        System.out.println("=== l.a(Wanderer) ===");
        System.out.println("derived = [" + derived + "]");
        System.out.println("length  = " + derived.length());

        // ============================================================
        // 8. Panggil sigil functions
        // ============================================================
        Method battle =
            lCls.getMethod("e", String.class);

        Method archive =
            lCls.getMethod("f", String.class);

        Method export =
            lCls.getMethod("g", String.class);

        System.out.println();
        System.out.println("=== SIGILS ===");

        System.out.println(
            "battle  = " + battle.invoke(l, derived)
        );

        System.out.println(
            "archive = " + archive.invoke(l, derived)
        );

        System.out.println(
            "export  = " + export.invoke(l, derived)
        );

        // ============================================================
        // 9. Bandingkan beberapa format history
        // ============================================================
        String[] candidates = {
            derived,
            "Q5",
            "Q5>",
            "Q5>Q13",
            "Q5>Q13>Q11",
            "Q5:",
            "Q5|",
            "Q5;",
            "Q5Q13",
            "Q5Q13Q11"
        };

        System.out.println();
        System.out.println("=== CANDIDATE SIGILS ===");

        for (String input : candidates) {

            String a =
                (String) archive.invoke(l, input);

            String g =
                (String) export.invoke(l, input);

            String e =
                (String) battle.invoke(l, input);

            System.out.println(
                "[" + input + "]" +
                " battle=" + e +
                " archive=" + a +
                " export=" + g
            );
        }

        // ============================================================
        // 10. Remote values yang sudah kita punya
        // ============================================================
        System.out.println();
        System.out.println("=== REMOTE TARGET ===");
        System.out.println("battle  = 95910");
        System.out.println("archive = bd3d174e3b60");
        System.out.println("export  = 0b23aa7001b2");
    }
}
