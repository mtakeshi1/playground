package mtakeshi1.playground;

import mtakeshi1.playground.mhandles.MHInstances;

public class Useless {

    public static void main(String[] args) throws Throwable {
        int c = 0;
        for (int i = 0; i < 500000; i++) {
//            c += MHInstances.manual(i).charAt(0);
            c += invokeMH(i).charAt(0);
        }
        System.out.println(c);
    }

    private static String invokeMH(int i) throws Throwable {
        return (String) MHInstances.methodHandle.invoke(i);
    }


}
