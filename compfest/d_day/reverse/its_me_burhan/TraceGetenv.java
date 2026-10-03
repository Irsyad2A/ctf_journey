import java.lang.reflect.*;
import java.util.*;

public class TraceGetenv {
    public static void main(String[] args) throws Exception {
        Class<?> cls = Class.forName("l");

        Method[] ms = cls.getDeclaredMethods();
        System.out.println("=== L METHODS ===");
        for (Method m : ms) {
            System.out.println(m);
        }

        Method decode = cls.getDeclaredMethod("a", int[].class);
        decode.setAccessible(true);

        // Known candidate names from the constructor output.
        String[] candidates = {
            "ZB[UXRYHAN\\]SRESEPD",
            "ZB[UXRYHAN\\]FRLSAPG",
            "BURHAN_FLAG",
            "BURHAN_SEED",
            "FLAG",
            "SEED",
            "CTFD_FLAG",
            "CTFD_SEED"
        };

        System.out.println("\n=== ENVIRONMENT ===");
        for (String s : candidates) {
            System.out.printf("%-25s = %s%n", s, System.getenv(s));
        }

        System.out.println("\n=== ALL ENV KEYS ===");
        System.getenv().keySet().stream()
            .sorted()
            .forEach(k -> System.out.println(k));
    }
}
