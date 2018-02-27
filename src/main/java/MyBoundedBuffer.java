import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class MyBoundedBuffer {
    //做个队列，数据结构用数组，需要两个指针记录存值和取值的指针，需要锁，保证存取可以并发，需要两个临界状态代表满了和空了，满了就不能存值，
    //空了就不能取值，还需要一个当前库存变量表示存了多少值了
    Object arr[] = new Object[100];
    int putPtr, takePtr;
    int count;
    Lock lock = new ReentrantLock();
    Condition notFull = lock.newCondition();
    Condition notEmpty = lock.newCondition();

    public void put(Object x) throws InterruptedException {
        lock.lock();
        try {
            while (count == arr.length) {
                notFull.await();
            }
            arr[putPtr] = x;
            notEmpty.signal();
            if (++putPtr == arr.length) {
                putPtr = 0;
            }
            count++;
        } finally {
            lock.unlock();
        }
    }

    public Object take() throws InterruptedException {
        lock.lock();
        try {
            while (count == 0) {
                notEmpty.await();
            }
            Object x = arr[takePtr];
            count--;
            notFull.signal();
            if (++takePtr == count) {
                takePtr = 0;
            }
            return x;
        } finally {
            lock.unlock();
        }


    }
}
