package com.horuslugo.damagetp;

public class Util {
    public static void setTimeout(Runnable runnable, int delay){
        new Thread(() -> {
            try {
                Thread.sleep(delay);
                runnable.run();
            }
            catch (Exception e){
                System.err.println(e);
            }
        }).start();
    }

    public static double center(double value) {
        int intValue = (int) value;

        return intValue + 0.5D;
    }
}
