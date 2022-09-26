package com.aaron.tests.aqs;

import cn.hutool.core.util.RandomUtil;

import java.util.concurrent.CountDownLatch;

/**
 * @author liurong
 * @version 1.0
 * @date 2022/9/26 15:37
 */
public class CountDownLatchTest {

    public static void main(String[] args) {
        CountDownLatch countDownLatch = new CountDownLatch(3);
        for (int i = 0; i < 3; i++) {
            new Thread(() -> {
                System.out.println(Thread.currentThread().getName() + "开始执行");
                try {
                    Thread.sleep(RandomUtil.randomInt(0,10*1000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                countDownLatch.countDown();
            }).start();
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

