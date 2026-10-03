import java.lang.reflect.*;

public class TestOLogin {
    public static void main(String[] args) throws Exception {
        t db = new t();

        Class<?> O = Class.forName("o");
        Constructor<?> ctor = O.getDeclaredConstructor();
        ctor.setAccessible(true);

        Object obj = ctor.newInstance();

        Field a = t.class.getDeclaredField("a");
        a.setAccessible(true);

        Object q = a.get(db);

        Method add = q.getClass().getMethod("a", b.class);
        add.invoke(q, obj);

        String user = ((b)obj).getUsername();
        String pass = ((b)obj).a();

        System.out.println("TARGET:");
        System.out.println("username = " + user);
        System.out.println("password = " + pass);

        b result = db.a(user, pass);

        System.out.println("lookup   = " + result);

        if (result != null) {
            System.out.println("class    = " + result.getClass().getName());
            System.out.println("name     = " + result.getName());
            System.out.println("username = " + result.getUsername());
        }
    }
}
