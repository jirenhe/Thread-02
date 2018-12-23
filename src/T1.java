import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class T1 {

    public static int count = 0;

    public static Lock lock = new Lock();

    public static void main(String[] args) throws InterruptedException {

        CountDownLatch countDownLatch = new CountDownLatch(20);

        for (int i = 0; i < 20; i++) {
            new Thread(() -> {
                for (int j = 0; j < 100000; j++) {
                    lock.withLock(() -> count++);
                    /*lock.withLock(() -> {
                        try {
                            TimeUnit.SECONDS.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return count++;
                    });*/
//                    count++;
                }
                countDownLatch.countDown();
            }).start();
        }

        countDownLatch.await();
        System.out.println(count);

    }

    public static class Lock {

        private AtomicBoolean lockFlag = new AtomicBoolean(false);

        public void withLock(Supplier supplier) {
            this.lock();
            supplier.get();
            this.unLock();
        }

        private void lock() {
            while (!lockFlag.compareAndSet(false, true)) ;
        }

        private void unLock() {
            lockFlag.set(false);
        }

    }
}
