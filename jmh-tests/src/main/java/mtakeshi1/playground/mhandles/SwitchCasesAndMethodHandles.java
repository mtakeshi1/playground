package mtakeshi1.playground.mhandles;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.annotations.CompilerControl.Mode;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@CompilerControl(Mode.DONT_INLINE)
@BenchmarkMode(org.openjdk.jmh.annotations.Mode.AverageTime)
public class SwitchCasesAndMethodHandles {

    @Param({"5", "50"})
    private int entries;

    private MethodHandle methodHandle;

    private MethodHandle exact;

    private Map<String, Integer> map;


    @Setup
    public void setup() {
        this.methodHandle = buildHandle();
        this.exact = methodHandle.asType(MethodType.methodType(Object.class, String.class));
        this.map = buildMap();
    }

    @Benchmark
    public void plainParse(Blackhole blackhole) {
        for (int i = 0; i < entries; i++) {
            blackhole.consume(Integer.parseInt(String.valueOf(i)));
        }
    }

    @Benchmark
    public void hashMap(Blackhole blackhole) {
        for (int i = 0; i < entries; i++) {
            blackhole.consume(map.get(String.valueOf(i)));
        }

    }

    @Benchmark
    public void methodHandle(Blackhole blackhole) throws Throwable {
        for (int i = 0; i < entries; i++) {
            String s = String.valueOf(i);
            blackhole.consume(methodHandle.invoke(s));
        }
    }

    @Benchmark
    public void methodHandleExact(Blackhole blackhole) throws Throwable {
        for (int i = 0; i < entries; i++) {
            String s = String.valueOf(i);
            blackhole.consume(exact.invokeExact(s));
        }
    }

    public MethodHandle buildHandle() {
        try {
            Map<String, Integer> map = buildMap();
            MethodHandle forMap = buildForMap(map);
            MethodHandle remmaapped1 = MethodHandles.filterArguments(forMap, 0, remainderMethodHandle(entries));
            //remmaped1 (int, String) -> int
            MethodHandle remmaapped2 = MethodHandles.filterArguments(remmaapped1, 0, MethodHandles.publicLookup().findVirtual(String.class, "hashCode", MethodType.methodType(int.class)));
            //remmaped2 (String, String) -> int
            MethodHandle reordered = MethodHandles.permuteArguments(remmaapped2, MethodType.methodType(int.class, String.class), 0, 0);
//reordered (String) -> int
            return reordered;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, Integer> buildMap() {
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < entries; i++) {
            map.put(String.valueOf(i), i);
        }
        return map;
    }

    public static int rem(int a, int b) {
        return Math.abs(a % b);
    }

    private static MethodHandle remainderMethodHandle(int entries) throws Exception {
        MethodHandle rem = MethodHandles.publicLookup().findStatic(SwitchCasesAndMethodHandles.class, "rem", MethodType.methodType(int.class, int.class, int.class));
        // (int, int) -> int
        return MethodHandles.insertArguments(rem, 1, entries);
    }

    public MethodHandle buildForMap(Map<String, Integer> map) throws NoSuchMethodException, IllegalAccessException {
        Map<Integer, Map<String, Integer>> mm = new HashMap<>();
        map.forEach((k, v) -> {
            int n = rem(k.hashCode(), entries);
            mm.computeIfAbsent(n, ignored -> new HashMap<>()).put(k, v);
        });
        MethodHandle[] switchEntries = new MethodHandle[entries];
        for (int i = 0; i < entries; i++) {
            Map<String, Integer> entries = mm.getOrDefault(i, Collections.emptyMap());
            switchEntries[i] = MethodHandles.dropArguments(buildForList("unknown entry for index: " + i, entries), 0, int.class);
            //(int, String) -> int
        }
        MethodHandle error = MethodHandles.dropArguments(throwExceptionAdapted(), 0, int.class);
        // (int, String) -> int
        MethodHandle tableSwitch = MethodHandles.tableSwitch(error, switchEntries);
        // (int, String) -> int

        return tableSwitch;
    }

    public static MethodHandle buildForList(String errorMsg, Map<String, Integer> map) throws NoSuchMethodException, IllegalAccessException {
        MethodHandle result = throwExceptionAdapted();
        for (var entry : map.entrySet()) {
            MethodHandle constant = MethodHandles.dropArguments(MethodHandles.constant(int.class, entry.getValue()), 0, String.class);
            // (String) -> int
            MethodHandle equals = MethodHandles.publicLookup().findVirtual(Object.class, "equals", MethodType.methodType(boolean.class, Object.class));
            // (Object, Object) -> boolean
            MethodHandle curried = MethodHandles.insertArguments(equals, 0, entry.getKey());
            // Object -> boolean
            MethodHandle adapted = curried.asType(MethodType.methodType(boolean.class, String.class));

            result = MethodHandles.guardWithTest(adapted, constant, result);
        }
        return result;
    }

    private static MethodHandle throwExceptionAdapted() throws NoSuchMethodException, IllegalAccessException {
        MethodHandle throwException = MethodHandles.throwException(int.class, NoSuchElementException.class);
        // (NoSuchElement -> int)
        MethodHandle ctor = MethodHandles.publicLookup().findConstructor(NoSuchElementException.class, MethodType.methodType(void.class, String.class));
        // (String -> NoSuchElement)
        // (String -> int) throwing exception

        //MethodHandles.dropArguments(throwException, 0, String.class);
        return MethodHandles.filterArguments(throwException, 0, ctor);
    }

    public static void main(String[] args) throws Throwable {
        SwitchCasesAndMethodHandles a = new SwitchCasesAndMethodHandles();
        a.entries = 50;
        a.setup();
        System.out.println(a.exact.type());
        for (int i = 0; i < a.entries; i++) {
            Object r = a.exact.invokeExact(String.valueOf(i));
            if (!r.equals(i)) {
                throw new RuntimeException();
            }
            r = a.methodHandle.invoke(String.valueOf(i));
            if (!r.equals(i)) {
                throw new RuntimeException();
            }

        }
        Options opt = new OptionsBuilder()
                .include(SwitchCasesAndMethodHandles.class.getSimpleName())
                .forks(1)
                .timeUnit(TimeUnit.NANOSECONDS)
                .build();

        new Runner(opt).run();

    }


}
