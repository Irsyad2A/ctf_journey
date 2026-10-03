import java.lang.reflect.*;
import java.util.*;

public class DumpQuestTable {
    public static void main(String[] args) throws Exception {
        Class<?> lc = Class.forName("l");

        System.out.println("=== l METHODS ===");
        for (Method m : lc.getDeclaredMethods()) {
            if (m.getName().equals("k")) {
                m.setAccessible(true);
                System.out.println(m);
            }
        }

        Object obj;

        try {
            Constructor<?> ctor = lc.getDeclaredConstructor();
            ctor.setAccessible(true);
            obj = ctor.newInstance();
        } catch (NoSuchMethodException e) {
            System.out.println("[!] No no-arg constructor.");
            obj = null;
        }

        Method k = lc.getDeclaredMethod("k");
        k.setAccessible(true);

        Object result = k.invoke(obj);

        System.out.println("\n=== QUEST TABLE ===");
        System.out.println("class = " + result.getClass());
        System.out.println("value = " + result);

        Object[] quests = (Object[]) result;

        System.out.println("size = " + quests.length);

        for (int i = 0; i < quests.length; i++) {
            Object q = quests[i];

            Class<?> qc = q.getClass();

            Method a = qc.getDeclaredMethod("a");
            Method b = qc.getDeclaredMethod("b");
            Method c = qc.getDeclaredMethod("c");
            Method d = qc.getDeclaredMethod("d");

            a.setAccessible(true);
            b.setAccessible(true);
            c.setAccessible(true);
            d.setAccessible(true);

            System.out.printf(
                "[%02d] id=%s difficulty=%s description=%s monsterIndex=%s class=%s%n",
                i,
                a.invoke(q),
                b.invoke(q),
                c.invoke(q),
                d.invoke(q),
                qc.getName()
            );
        }
    }
}
