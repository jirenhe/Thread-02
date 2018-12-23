import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public class T3 {

    private static Lock lock = new Lock();

    private static int count = 0;

    public static void main(String[] args) throws InterruptedException {

        int c = 20;
        CountDownLatch countDownLatch = new CountDownLatch(c);
        for (int i = 0; i < c; i++) {
            new Thread(() -> {
                for (int j = 0; j < 1000000; j++) {
                    lock.withLock(() -> count++);
                }
                countDownLatch.countDown();
            }).start();
        }

        countDownLatch.await();
        System.out.println(count);


    }

    public static class Lock {

        private java.util.concurrent.locks.Lock reentrantLock = new ReentrantLock();

        public void withLock(Supplier supplier) {
            reentrantLock.lock(); //这里要注意，获取锁一定是在try-finally块外面
            try {
                supplier.get();
            } finally {
                reentrantLock.unlock();
            }
        }
    }
}
