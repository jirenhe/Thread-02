import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class T4 {

    public static void main(String[] args) throws InterruptedException {

        CDPlayer cdPlayer = new CDPlayer();

        cdPlayer.start();
        TimeUnit.MICROSECONDS.sleep(10);
        cdPlayer.suspend();
        TimeUnit.MICROSECONDS.sleep(10);
        cdPlayer.resume();
        TimeUnit.MICROSECONDS.sleep(10);
        cdPlayer.suspend();
        TimeUnit.MICROSECONDS.sleep(10);
        cdPlayer.stop();
    }

    public static class CDPlayer implements Runnable {

        private volatile int flag = 1;

        private final Lock lock = new ReentrantLock();

        private final Condition condition = lock.newCondition();

        Thread thread = new Thread(this, "CDPlayer-thread");

        public void start() {
            thread.start();
        }

        @Override
        public void run() {
            while (!thread.isInterrupted()) {
                lock.lock();
                try {
                    while (flag == 2) {
                        try {
                            condition.await();
                        } catch (InterruptedException e) {
                            return;
                        }
                    }
                    singing();
                } finally {
                    lock.unlock();
                }
            }
        }

        public void stop() {
            thread.interrupt();
            System.out.println("cdPlayer.stop");
        }

        public void resume() {
            lock.lock();
            try {
                flag = 1;
                condition.signalAll();
            } finally {
                lock.unlock();
            }
            System.out.println("cdPlayer.resume");
        }

        public void suspend() {
            flag = 2;
            System.out.println("cdPlayer.suspend");
        }

        private void singing() {
            System.out.println("singing.....");
        }

    }

}
