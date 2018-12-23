import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.function.Supplier;

public class T2 {

    private static Lock lock = new Lock();

    private static int count = 0;

    public static void main(String[] args) throws InterruptedException {

        int c = 20;
        CountDownLatch countDownLatch = new CountDownLatch(c);
        for (int i = 0; i < c; i++) {
            new Thread(() -> {
                for (int j = 0; j < 1000000; j++) {
                    lock.withLock(() -> count++);
                   /* lock.withLock(() -> {
                        try {
                            TimeUnit.SECONDS.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return count++;
                    });*/
                }
                countDownLatch.countDown();
            }).start();
        }

        countDownLatch.await();
        System.out.println(count);


    }

    public static class Lock {

        private Sync sync = new Sync();

        public void withLock(Supplier supplier) {
            this.lock();
            try {
                supplier.get();
            } finally {
                this.unlock();
            }
        }

        private void lock() {
            sync.acquire(1);
        }

        private void unlock() {
            sync.release(1);
        }


        private static class Sync extends AbstractQueuedSynchronizer {

            protected boolean tryAcquire(int acquires) {
                if (this.compareAndSetState(0, 1)) {
                    this.setExclusiveOwnerThread(Thread.currentThread());
                    return true;
                } else {
                    return false;
                }
            }

            protected boolean tryRelease(int releases) {
                if (Thread.currentThread() != this.getExclusiveOwnerThread()) {
                    throw new IllegalMonitorStateException();
                } else {
                    this.setExclusiveOwnerThread(null);
                    this.setState(0);
                    return true;
                }
            }

            protected boolean isHeldExclusively() {
                return this.getState() != 0 && this.getExclusiveOwnerThread() == Thread.currentThread();
            }

        }
    }
}
