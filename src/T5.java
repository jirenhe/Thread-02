import java.util.concurrent.TimeUnit;

public class T5 {

    private static volatile int x, y = 0;

    public static void main(String[] args) throws InterruptedException {

        startRead();
        startWrite();
        TimeUnit.SECONDS.sleep(5);
        System.exit(0);
    }

    private static void startWrite() {
        new Thread(() -> {
            while (true) {
                x++;
                y++;
            }
        }).start();
    }

    private static void startRead() {
        int c = 5;
        for (int i = 0; i < c; i++) {
            new Thread(() -> {
                while (true) {
                    int lx = x, ly = y;
                    if (lx != ly) {
                        System.out.println("Thread-" + Thread.currentThread().getName() + " read :" + lx + "," + ly);
                    }
                }
            }).start();
        }
    }


}
