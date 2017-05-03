import java.util.Random;

/**
 * Created by pefa1 on 18.4.2017.
 */
public class Main {

    public static void main(String[] args) {
        Thread thread1 = new Thread(new Counter(), "vlakno 1");
        Thread thread2 = new Thread(new Counter(), "vlakno 2");
        Thread thread3 = new Thread(new Counter(), "vlakno 3");

        thread1.start();
        thread2.start();
        thread3.start();
    }

    private static class Counter implements Runnable {
        private static Random pauseLengthGenerator = new Random();
        private static int i = 0;
        private static Object lock = new Object();

        @Override
        public void run() {
            while (true) {
                synchronized (lock) {
                    if (i <= 50) {
                        System.out.println(Thread.currentThread().getName() + ": " + i);
                        i++;
                    } else {
                        break;
                    }
                }

                // Wait some time without switching context
                // This is only for example purposes, active waiting loop
                // should never be used in production code, similar to:
                // for (long j = 0l; j < 1000000000l; j++) {}
                long pauseLength = pauseLengthGenerator.nextInt(1000);
                long endTime = System.currentTimeMillis() + pauseLength;
                while (System.currentTimeMillis() < endTime) {}
            }
        }
    }
}
