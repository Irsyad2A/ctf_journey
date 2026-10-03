import java.lang.reflect.*;
import java.util.*;

public class DumpRealQuests {

    static Object invoke(Object obj, String name, Class<?>... types) throws Exception {
        Method m = obj.getClass().getDeclaredMethod(name, types);
        m.setAccessible(true);
        return m.invoke(obj);
    }

    static Object field(Object obj, String name) throws Exception {
        Field f = obj.getClass().getDeclaredField(name);
        f.setAccessible(true);
        return f.get(obj);
    }

    static void dumpQuest(Object q) throws Exception {
        Class<?> c = q.getClass();

        Method getId = c.getMethod("getId");
        Method getName = c.getMethod("getName");
        Method getType = c.getMethod("getQuestType");
        Method getDiff = c.getMethod("getDifficulty");
        Method getMonster = c.getMethod("getMonster");
        Method getExp = c.getMethod("getExpReward");
        Method getCoin = c.getMethod("getCoinReward");

        Object diff = getDiff.invoke(q);
        Object monster = getMonster.invoke(q);

        System.out.println(
            getId.invoke(q)
            + " | "
            + getName.invoke(q)
            + " | type="
            + getType.invoke(q)
            + " | diff="
            + diff
            + " | monster="
            + monster
            + " | exp="
            + getExp.invoke(q)
            + " | coin="
            + getCoin.invoke(q)
            + " | class="
            + c.getName()
        );
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== CONSTRUCT t ===");

        Class<?> T = Class.forName("t");
        Object t = T.getConstructor().newInstance();

        System.out.println("t = " + t);

        System.out.println("\n=== t.e() QUEST LIST ===");

        Method listMethod = T.getMethod("e");
        Object result = listMethod.invoke(t);

        System.out.println("result class = " + result.getClass());
        System.out.println("size = " + ((List<?>) result).size());

        for (Object q : (List<?>) result) {
            dumpQuest(q);
        }

        System.out.println("\n=== DIRECT LOOKUP THROUGH t.n() ===");

        for (int i = 1; i <= 18; i++) {
            String id = "Q" + i;

            try {
                Method n = T.getMethod("n", String.class);
                Object q = n.invoke(t, id);
                System.out.print(id + " -> ");
                dumpQuest(q);
            } catch (InvocationTargetException e) {
                System.out.println(id + " -> " +
                    e.getTargetException());
            }
        }

        System.out.println("\n=== INTERNAL v ===");

        Object v = field(t, "d");
        System.out.println("v = " + v);

        Method vm = v.getClass().getMethod("a");
        Object quests = vm.invoke(v);

        System.out.println("v.a() class = " + quests.getClass());
        System.out.println("v.a() size = " + ((List<?>) quests).size());

        for (Object q : (List<?>) quests) {
            dumpQuest(q);
        }
    }
}
