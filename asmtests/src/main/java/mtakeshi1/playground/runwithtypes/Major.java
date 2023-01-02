package mtakeshi1.playground.runwithtypes;

import java.util.function.Consumer;

public class Major {

    static class Container<E> {


    }

    static Number inverse(Number num) {
        return 1.0 / num.doubleValue();
    }

    static Consumer<? super CharSequence> getSuper() { return a -> {}; }
    static Consumer<? extends CharSequence> getExtends() { return a -> {}; }


    static class Grandpa {
    }

    static class Parent  extends Grandpa {}

    static class Child extends Parent {}

    public static void main(String[] args) {
        Consumer<CharSequence> stringCons = null;
        Consumer<? super String> bla = stringCons;
    }

}
