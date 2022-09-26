package com.aaron.tests.aqs;

import cn.hutool.core.util.RandomUtil;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * @author liurong
 * @version 1.0
 * @date 2022/9/26 15:45
 */
public class CyclicBarrierTest {

    public static void main(String[] args) throws InterruptedException {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(3, () -> {
            System.out.println("越过屏障");
        });
        for (int i = 0; i < 9; i++) {
            new Thread(() -> {
                try {
                    Thread.sleep(RandomUtil.randomInt(0, 10 * 1000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    cyclicBarrier.await();
                } catch (InterruptedException e) {
                } catch (BrokenBarrierException e) {
                }
            }).start();
        }
        // 主线程等待，不对的，getNumberWaiting返回parties-count，初始化时，parties=count，直接就等于0,while了个寂寞 可能压根就没想让你在主线程里面阻塞
        while (cyclicBarrier.getNumberWaiting() != 0) {
            System.out.println("等待中");
        }
//        try {
//            cyclicBarrier.await();
//        } catch (BrokenBarrierException e) {
//            throw new RuntimeException(e);
//        }
        // 如果想要阻塞，可以在主线程里调用一次        cyclicBarrier.await();,这样就会阻塞,这样要new CyclicBarrier(4)，比线程数多加一个主线程就可以，但是这样直接用CountDownLatch就可以了
        // 或者new CyclicBarrier(4, () -> {}); 到达屏障时，执行这个runnable，每越过一次屏障都会执行这个runnable
        System.out.println("end");
        // new CyclicBarrier(4, () -> {});
    }
}

/*
    note:
    CyclicBarrier 跟 CountDownLatch不一样，CountDownLatch是一次性的，CyclicBarrier是可以循环使用的；CountDownLatch主线程可以await，CyclicBarrier主线程不能await（要 while (cyclicBarrier.getNumberWaiting()!=0){）
    调用CyclicBarrier.await()方法，当前线程被暂停，当最后一个线程调用CyclicBarrier.await()方法时，会使得使用当前实例的暂停的所有线程唤醒。与CountDownLatch的不同点是，当所有线程被唤醒之后，下一次调用await()方法又会暂停，又需要等待最后的线程都执行之后才能唤醒，是可以重复使用的。
    1. CyclicBarrier的构造函数中的parties参数表示屏障拦截的线程数量，每个线程调用await方法告诉CyclicBarrier我已经到达了屏障，然后当前线程被阻塞。
    Number of parties still waiting. Counts down from parties to 0 on each generation. It is reset to parties on each new generation or when broken.
        this.parties = parties;
        this.count = parties;
    调用await方法：（每个线程调用await表示已经准备好了，count一开始等于屏障数，每个线程准备好了屏障数就会-1，屏障数=0重置屏障数）
        int index = --count;
        if (index == 0) {  // tripped
            nextGeneration();
            ...
        }
    getNumberWaiting：
        parties - count
 */