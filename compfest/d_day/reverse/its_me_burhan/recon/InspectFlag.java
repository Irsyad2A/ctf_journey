import java.lang.reflect.*;

public class InspectFlag {
    public static void main(String[] args) throws Exception {
        Class<?> cls = Class.forName("l");

        Constructor<?> ctor = cls.getDeclaredConstructor();
        ctor.setAccessible(true);

        Object obj = ctor.newInstance();

        System.out.println("=== FLAG FIELD ===");

        Field f = cls.getDeclaredField("f");
        f.setAccessible(true);

        Object value = f.get(obj);

        System.out.println("l.f = " + value);

        System.out.println();
        System.out.println("=== ENVIRONMENT ===");

        String env = System.getenv("BURHAN_FLAG");

        if (env == null) {
            System.out.println("BURHAN_FLAG = <not set>");
        } else {
            System.out.println("BURHAN_FLAG = " + env);
        }
    }
}
