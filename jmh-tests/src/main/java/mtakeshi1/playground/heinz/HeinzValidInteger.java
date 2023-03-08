package mtakeshi1.playground.heinz;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Measurement(timeUnit = TimeUnit.NANOSECONDS, iterations = 150)
@BenchmarkMode(org.openjdk.jmh.annotations.Mode.AverageTime)
@State(Scope.Benchmark)
public class HeinzValidInteger {

    public enum Dataset {
        DATASET1, DATASET2, DATASET3
    }

    public static final int ROWS_IN_DATASET = 1000 * 1000;

    List<String> input;

    @Param
    private Dataset dataset;

    @Setup
    public void initData() {
        input = switch (dataset) {
            case DATASET1 -> generateinput();
            case DATASET2 -> generateDataset2();
            case DATASET3 -> generateDataset3();
        };
    }

    public static List<String> generateinput() {
        List<String> list = new ArrayList<>(ROWS_IN_DATASET);
        Random rand = new Random(12346);
        for (int i = 0; i < ROWS_IN_DATASET; i++) {
            list.add("3" + Math.abs(rand.nextInt(10000)));
        }
        return list;
    }

    public static List<String> generateDataset2() {
        List<String> list = new ArrayList<>(ROWS_IN_DATASET);
        // dataset 2, about half true
        Random rand = new Random(12347);
        for (int j = 0; j < ROWS_IN_DATASET / 10; j++) {
            list.add("3" + Math.abs(rand.nextInt(10000)));
            list.add("-" + Math.abs(rand.nextInt(10000)));
            list.add("3" + Math.abs(rand.nextInt(10000)));
            int i = Math.abs(rand.nextInt(100000));
            while (Integer.toString(i).charAt(0) == '3') {
                i = Math.abs(rand.nextInt(100000));
            }
            list.add("" + i);
            list.add("3" + Math.abs(rand.nextInt(10000)));
            list.add("-" + Math.abs(rand.nextInt(10000)));
            list.add("3" + Math.abs(rand.nextInt(10000)));
            i = Math.abs(rand.nextInt(100000));
            while (Integer.toString(i).charAt(0) == '3') {
                i = Math.abs(rand.nextInt(100000));
            }
            list.add("" + i);
            list.add("3" + Math.abs(rand.nextInt(10000)));
            list.add("-" + Math.abs(rand.nextInt(10000)));
        }
        return list;
    }

    // dataset 2, about 1/3 true, 1/3 false, over 1/3 not a number
    public static List<String> generateDataset3() {
        List<String> list = new ArrayList<>(ROWS_IN_DATASET);
        Random rand = new Random(12348);
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < ROWS_IN_DATASET / 10; j++) {
            list.add("3" + Math.abs(rand.nextInt(10000)));
            list.add("-" + Math.abs(rand.nextInt(10000)));
            sb.setLength(0);
            sb.append(rand.nextInt(1000));
            sb.insert(0, 'w');
            list.add(sb.toString());
            list.add("3" + Math.abs(rand.nextInt(10000)));
            int i = Math.abs(rand.nextInt(100000));
            while (Integer.toString(i).charAt(0) == '3') {
                i = Math.abs(rand.nextInt(100000));
            }
            list.add("" + i);
            sb.setLength(0);
            sb.append(rand.nextInt(1000));
            sb.insert(1, 'q');
            list.add(sb.toString());
            list.add("3" + Math.abs(rand.nextInt(10000)));
            list.add("-" + Math.abs(rand.nextInt(10000)));
            sb.setLength(0);
            sb.append(rand.nextInt(1000));
            sb.append('s');
            list.add(sb.toString());
            list.add("NOTNUM");
        }
        return list;
    }

    public static boolean validatorImplFor(String testInteger) {
        if (testInteger == null)
            return false;
        int len = testInteger.length();
        if (len < 2 || len > 5 || testInteger.charAt(0) != '3')
            return false;
        for (int i = 1; i < len; i++) {
            int c = testInteger.charAt(i);
            if (c < '0' || c > '9') return false;
        }
        return true;
    }

    public static boolean julienValidatorImpl(String testInteger) {
        int length = testInteger == null ? 0 : testInteger.length();
        return length > 1
                && testInteger.charAt(0) == '3'
                && isDigit(testInteger.charAt(1))
                && (length <= 2 || isDigit(testInteger.charAt(2)))
                && (length <= 3 || isDigit(testInteger.charAt(3)))
                && (length <= 4 || isDigit(testInteger.charAt(4)))
                && (length <= 5);
    }

    private static boolean isDigit(char c) {
        return '0' <= c && c <= '9';
    }

    public static boolean validatorImplSwitch(String testInteger) {
        if (testInteger == null)
            return false;
        int length = testInteger.length();

        if (length < 2)
            return false;
        if (testInteger.charAt(0) != '3')
            return false;

        char temp;
        switch (length) {
            default:
                return false;
            case 5:
                if ((temp = testInteger.charAt(4)) < '0' || temp > '9')
                    return false;
            case 4:
                if ((temp = testInteger.charAt(3)) < '0' || temp > '9')
                    return false;
            case 3:
                if ((temp = testInteger.charAt(2)) < '0' || temp > '9')
                    return false;
            case 2:
                if ((temp = testInteger.charAt(1)) < '0' || temp > '9')
                    return false;
        }

        return true;
    }

    public static boolean validatorImplSwitchReorder(String testInteger) {
        if (testInteger == null)
            return false;
        int length = testInteger.length();

        if (length < 2)
            return false;
        if (length > 5)
            return false;

        if (testInteger.charAt(0) != '3')
            return false;

        char temp;
        switch (length) {
            case 5:
                if ((temp = testInteger.charAt(4)) < '0' || temp > '9')
                    return false;
            case 4:
                if ((temp = testInteger.charAt(3)) < '0' || temp > '9')
                    return false;
            case 3:
                if ((temp = testInteger.charAt(2)) < '0' || temp > '9')
                    return false;
            case 2:
                if ((temp = testInteger.charAt(1)) < '0' || temp > '9')
                    return false;
        }

        return true;
    }

    @Benchmark
    public long naiveFor() {
        long c = 0;
        for (String s : input) {
            if (validatorImplFor(s)) c++;
        }
        return c;
    }

    @Benchmark
    public long fast() {
        long c = 0;
        for (String s : input) {
            if (julienValidatorImpl(s)) c++;
        }
        return c;
    }

    @Benchmark
    public long switchInOrder() {
        long c = 0;
        for (String s : input) {
            if (validatorImplSwitch(s)) c++;
        }
        return c;
    }

    @Benchmark
    public long switchReverse() {
        long c = 0;
        for (String s : input) {
            if (validatorImplSwitchReorder(s)) c++;
        }
        return c;
    }

    public static void main(String[] args) throws Throwable {
        Options opt = new OptionsBuilder()
                .include(HeinzValidInteger.class.getSimpleName())
                .forks(1)
                .timeUnit(TimeUnit.MICROSECONDS)
                .build();

        new Runner(opt).run();
    }

}
