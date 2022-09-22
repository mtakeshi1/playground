package mtakeshi1.playground;

import org.openjdk.jmh.annotations.CompilerControl;
import org.openjdk.jmh.annotations.CompilerControl.Mode;
import org.openjdk.jmh.infra.Blackhole;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

//@CompilerControl(Mode.DONT_INLINE)
public class MHInstances {


    public static boolean isEven(int x) {
        return x % 2 == 0;
    }

    public static String one() {
        return "1";
    }


    public static String zero() {
        return "0";
    }


    public static String manual(int signal) {
        if (MHInstances.isEven(signal)) {
            return (MHInstances.zero());
        } else {
            return (MHInstances.one());
        }
    }

    public static void manual(Blackhole blackhole, int signal) {
        if (MHInstances.isEven(signal)) {
            blackhole.consume(MHInstances.zero());
        } else {
            blackhole.consume(MHInstances.one());
        }
    }

    public static final MethodHandle methodHandle;

    static {
        try {
            MethodHandle test = MethodHandles.publicLookup().unreflect(MHInstances.class.getMethod("isEven", int.class));
            MethodHandle zero = MethodHandles.dropArguments(MethodHandles.publicLookup().unreflect(MHInstances.class.getMethod("zero")), 0, int.class);
            MethodHandle one = MethodHandles.dropArguments(MethodHandles.publicLookup().unreflect(MHInstances.class.getMethod("one")), 0, int.class);
            MethodType adapted = MethodType.methodType(Object.class, int.class);
            methodHandle = MethodHandles.guardWithTest(test, zero, one).asType(adapted);
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

}
