import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class ListLMethods {
    public static void main(String[] args) throws Exception {
        Class<?> cls = Class.forName("l");

        for (Method m : cls.getDeclaredMethods()) {
            if (m.getReturnType() == String.class) {
                System.out.println(
                    Modifier.toString(m.getModifiers()) +
                    " " + m.getName() +
                    " (" +
                    java.util.Arrays.toString(m.getParameterTypes()) +
                    ") -> " +
                    m.getReturnType().getName()
                );
            }
        }
    }
}
