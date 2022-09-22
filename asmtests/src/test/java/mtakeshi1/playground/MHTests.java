package mtakeshi1.playground;

import org.junit.Assert;
import org.junit.Test;

import java.lang.invoke.MethodHandle;
import java.util.Arrays;

import static java.lang.invoke.MethodHandles.*;
import static java.lang.invoke.MethodType.methodType;

public class MHTests extends Assert {

    @Test
    public void testCollect() throws Throwable {
        MethodHandle deepToString = publicLookup().findStatic(Arrays.class, "deepToString", methodType(String.class, Object[].class));
        MethodHandle ts1 = deepToString.asCollector(String[].class, 1);
        assertEquals("[strange]", (String) ts1.invokeExact("strange"));
        MethodHandle ts2 = deepToString.asCollector(String[].class, 2);
        assertEquals("[up, down]", (String) ts2.invokeExact("up", "down"));
        MethodHandle ts3 = deepToString.asCollector(String[].class, 3);
        MethodHandle ts3_ts2 = collectArguments(ts3, 1, ts2);
        assertEquals("[top, [up, down], strange]", (String) ts3_ts2.invokeExact("top", "up", "down", "strange"));
        MethodHandle ts3_ts2_ts1 = collectArguments(ts3_ts2, 3, ts1);
        assertEquals("[top, [up, down], [strange]]", (String) ts3_ts2_ts1.invokeExact("top", "up", "down", "strange"));
        MethodHandle ts3_ts2_ts3 = collectArguments(ts3_ts2, 1, ts3);
        assertEquals("[top, [[up, down, strange], charm], bottom]", (String) ts3_ts2_ts3.invokeExact("top", "up", "down", "strange", "charm", "bottom"));
    }

    public static void consume3(int a, int b, int c) {
        System.out.printf("a=%d,b=%d,c=%d%n", a, b, c);
    }

    @Test
    public void permute() throws Throwable {
        MethodHandle m = publicLookup().findStatic(MHTests.class, "consume3", methodType(void.class, int.class, int.class, int.class));
        MethodHandle arguments = permuteArguments(
                m, methodType(void.class, int.class), 0, 0, 0
        );
        arguments.invoke(10);
    }


}
