import java.lang.reflect.*;
import java.util.*;

public class DeepRecon {
    static void dumpClass(String name) {
        try {
            Class<?> c = Class.forName(name);

            System.out.println("\n===== CLASS " + name + " =====");

            System.out.println("-- fields --");
            for (Field f : c.getDeclaredFields()) {
                f.setAccessible(true);
                String value = "";
                if (Modifier.isStatic(f.getModifiers())) {
                    try {
                        Object v = f.get(null);
                        if (v != null && v.getClass().isArray()) {
                            if (v instanceof int[]) value = Arrays.toString((int[])v);
                            else if (v instanceof long[]) value = Arrays.toString((long[])v);
                            else if (v instanceof byte[]) value = Arrays.toString((byte[])v);
                            else if (v instanceof String[]) value = Arrays.toString((String[])v);
                            else value = String.valueOf(v);
                        } else {
                            value = String.valueOf(v);
                        }
                    } catch (Throwable ignored) {}
                }
                System.out.printf(
                    "%s %s %s = %s%n",
                    Modifier.toString(f.getModifiers()),
                    f.getType().getTypeName(),
                    f.getName(),
                    value
                );
            }

            System.out.println("-- constructors --");
            for (Constructor<?> cst : c.getDeclaredConstructors()) {
                System.out.println(cst);
            }

            System.out.println("-- methods --");
            for (Method m : c.getDeclaredMethods()) {
                System.out.println(m);
            }

        } catch (Throwable e) {
            System.out.println("ERROR " + name + ": " + e);
        }
    }

    static void decodeMainStrings() {
        try {
            Class<?> M = Class.forName("Main");

            Method decoder = M.getDeclaredMethod("a", String.class);
            decoder.setAccessible(true);

            // Strings copied directly from Main.javap by regex-friendly extraction.
            Scanner sc = new Scanner(
                java.nio.file.Files.newBufferedReader(
                    java.nio.file.Path.of("recon/Main.javap")
                )
            );

            Set<String> seen = new LinkedHashSet<>();

            while (sc.hasNextLine()) {
                String line = sc.nextLine();

                int p = line.indexOf("// String ");
                if (p < 0) continue;

                String s = line.substring(p + 9).trim();

                if (s.isEmpty()) continue;
                if (!seen.add(s)) continue;

                try {
                    Object out = decoder.invoke(null, s);

                    if (out instanceof String decoded) {
                        System.out.println(
                            "[DECODE] " + escape(s) +
                            "  =>  " + escape(decoded)
                        );
                    }
                } catch (Throwable ignored) {
                    // javap may contain malformed/truncated constants.
                }
            }

            sc.close();

        } catch (Throwable e) {
            System.out.println("decode error: " + e);
        }
    }

    static String escape(String s) {
        return s
            .replace("\\", "\\\\")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
    }

    static void traceAdminPassword() {
        try {
            Class<?> L = Class.forName("l");

            Constructor<?> ctor = L.getDeclaredConstructor();
            ctor.setAccessible(true);
            Object l = ctor.newInstance();

            Method auth = L.getDeclaredMethod("a", String.class);
            auth.setAccessible(true);

            String password = "K76LD64XY3URX4RM";

            System.out.println(
                "\nADMIN PASSWORD TEST: " +
                password + " -> " +
                auth.invoke(l, password)
            );

            // Dump every zero-argument public/private method returning String.
            for (Method m : L.getDeclaredMethods()) {
                if (m.getParameterCount() != 0) continue;
                if (m.getReturnType() != String.class) continue;

                try {
                    m.setAccessible(true);
                    Object out = m.invoke(l);
                    System.out.println(
                        "[l." + m.getName() + "()] = " + out
                    );
                } catch (Throwable ignored) {}
            }

        } catch (Throwable e) {
            System.out.println("trace error: " + e);
        }
    }

    public static void main(String[] args) {
        dumpClass("a");
        dumpClass("am");
        dumpClass("ak");
        dumpClass("entities.Wanderer");

        traceAdminPassword();

        System.out.println("\n===== DECODE MAIN STRINGS =====");
        decodeMainStrings();
    }
}
