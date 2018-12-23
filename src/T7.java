import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.StampedLock;

public class T7 {

    private static StampedLock lock = new StampedLock();

    private static int x, y = 0;

    public static void main(String[] args) throws InterruptedException {

        startRead();
        startWrite();
        TimeUnit.SECONDS.sleep(5);
        System.exit(0);
    }

    private static void startWrite() {
        new Thread(() -> {
            while (true) {
                long stamp = lock.writeLock();
                try {
                    x++;
                    y++;
                } finally {
                    lock.unlock(stamp);
                }
            }
        }).start();
    }

    private static void startRead() {
        int c = 5;
        for (int i = 0; i < c; i++) {
            new Thread(() -> {
                while (true) {
                    long stamp = lock.tryOptimisticRead();
                    int lx = x, ly = y;
                    if (!lock.validate(stamp)) {
                        stamp = lock.readLock();
                        try {
                            lx = x;
                            ly = y;
                        } finally {
                            lock.unlockRead(stamp);
                        }
                    }
                    if (lx != ly) {
                        System.out.println("Thread-" + Thread.currentThread().getName() + " read :" + lx + "," + ly);
                    }
                }
            }).start();
        }
    }


}
