import java.lang.reflect.*;

public class ProbeQuestFactory {
    public static void main(String[] args) throws Exception {
        Class<?> T = Class.forName("t");

        System.out.println("=== METHODS OF t ===");
        for (Method m : T.getDeclaredMethods()) {
            System.out.println(m);
        }

        System.out.println();
        System.out.println("=== CONSTRUCTORS OF t ===");
        for (Constructor<?> c : T.getDeclaredConstructors()) {
            System.out.println(c);
        }

        Object tObj = null;

        for (Constructor<?> ctor : T.getDeclaredConstructors()) {
            if (ctor.getParameterCount() == 0) {
                ctor.setAccessible(true);
                tObj = ctor.newInstance();
                break;
            }
        }

        if (tObj == null) {
            System.out.println("Tidak ada constructor t() tanpa argumen.");
            return;
        }

        Method factory = T.getDeclaredMethod("n", String.class);
        factory.setAccessible(true);

        String[] ids = {
            "Q1", "Q2", "Q3", "Q4", "Q5",
            "Q6", "Q10", "Q11", "Q13", "Q15", "Q18"
        };

        System.out.println();
        System.out.println("=== FACTORY t.n(String) ===");

        for (String id : ids) {
            try {
                Object q = factory.invoke(tObj, id);

                System.out.println();
                System.out.println(id + " -> " + q);

                if (q == null) {
                    System.out.println("  class = null");
                    continue;
                }

                System.out.println("  class = " + q.getClass().getName());

                Class<?> cls = q.getClass();

                System.out.println("  superclass = " +
                        cls.getSuperclass().getName());

                System.out.println("  interfaces:");
                for (Class<?> iface : cls.getInterfaces()) {
                    System.out.println("    " + iface.getName());
                }

                for (String methodName : new String[]{
                        "getId",
                        "getName",
                        "getQuestType",
                        "getDescription",
                        "getDifficulty",
                        "getMonster",
                        "getBonusExp",
                        "getBonusCoin"
                }) {
                    try {
                        Method m = cls.getMethod(methodName);
                        Object v = m.invoke(q);
                        System.out.println("  " + methodName + " = " + v);
                    } catch (Exception ignored) {
                    }
                }

            } catch (InvocationTargetException e) {
                System.out.println();
                System.out.println(id + " -> EXCEPTION");
                Throwable cause = e.getCause();
                if (cause != null) {
                    cause.printStackTrace(System.out);
                } else {
                    e.printStackTrace(System.out);
                }
            }
        }
    }
}
