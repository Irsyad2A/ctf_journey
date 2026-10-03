import java.lang.reflect.*;

public class InspectQ5 {
    public static void main(String[] args) throws Exception {
        Class<?> T = Class.forName("t");
        Object t = T.getConstructor().newInstance();

        Method n = T.getMethod("n", String.class);
        Object q = n.invoke(t, "Q5");

        Class<?> f = q.getClass().getSuperclass();

        System.out.println("class        = " + q.getClass());
        System.out.println("super        = " + f);

        for (Method m : f.getMethods()) {
            if (m.getName().startsWith("get")) {
                try {
                    System.out.printf("%-25s = %s%n",
                        m.getName(),
                        m.invoke(q));
                } catch (Throwable ignored) {}
            }
        }

        for (Field x : q.getClass().getDeclaredFields()) {
            x.setAccessible(true);
            System.out.println(
                "FIELD " + x.getName() + " = " + x.get(q)
            );
        }
    }
}
