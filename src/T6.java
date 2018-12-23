import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class T6 {

    private static ReadWriteLock lock = new ReentrantReadWriteLock();

    private static Lock readWriteLock = lock.readLock();

    private static Lock writeLock = lock.writeLock();

    private static volatile int x, y = 0;

    public static void main(String[] args) throws InterruptedException {

        startRead();
        startWrite();
        TimeUnit.SECONDS.sleep(2);
        writeLock.lock();
        try {
            x++;
            y++;
            TimeUnit.SECONDS.sleep(5);
        } finally {
            writeLock.unlock();
        }
        TimeUnit.SECONDS.sleep(2);
        System.exit(0);
    }

    private static void startWrite() {
        new Thread(() -> {
            while (true) {
                writeLock.lock();
                try {
                    x++;
                    y++;
                    System.out.println("Thread-" + Thread.currentThread().getName() + " write");
                } finally {
                    writeLock.unlock();
                }
            }
        }).start();
    }

    private static void startRead() {
        int c = 5;
        for (int i = 0; i < c; i++) {
            new Thread(() -> {
                while (true) {
                    readWriteLock.lock();
                    try {
                        int lx = x, ly = y;
                        if (lx != ly) {
                            System.out.println("Thread-" + Thread.currentThread().getName() + " read :" + lx + "," + ly);
                        }
                    } finally {
                        readWriteLock.unlock();
                    }
                }
            }).start();
        }
    }


}
