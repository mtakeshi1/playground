package mtakeshi1.playground.jude;

import java.util.List;

public record LinkedList(double[] val, LinkedList next) {
    LinkedList(double[] a) {
        this(a, null);
    }

    boolean hasNext() {
        return next != null;
    }

    public static LinkedList fromList(List<double[]> list) {
        return fromList(list, 0);

    }

    public static LinkedList fromList(List<double[]> list, int index) {
        if (index == list.size()) {
            return null;
        } else {
            return new LinkedList(list.get(index), fromList(list, index + 1));
        }
    }

}
