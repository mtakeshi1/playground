package mtakeshi1.playground;

import org.openjdk.jmh.annotations.CompilerControl;
import org.openjdk.jmh.annotations.CompilerControl.Mode;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

@CompilerControl(Mode.DONT_INLINE)
public class DoNothingInstances implements DoNothing {

    public static int doNothingHere(int x) {
        return x;
    }

    public static DoNothing lambda = x -> x;
    public static DoNothing methodReference = x -> doNothingHere(x);
    public static DoNothing anonymousClass = new DoNothing() {
        @Override
        public Object doNothing(Integer x) {
            return x;
        }
    };

    public static DoNothing regularInstanceInvokeInterface = new DoNothingInstances();

    public static DoNothingInstances regularInstanceInvokeVirtual = new DoNothingInstances();

    public static Method doNothingReflected;
    public static MethodHandle doNothingMethodHandle;

    static {
        try {
            doNothingReflected = DoNothing.class.getMethod("doNothing", Integer.class);
            doNothingMethodHandle = MethodHandles.publicLookup().unreflect(doNothingReflected);
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }


    @Override
    public Object doNothing(Integer x) {
        return x;
    }
}
