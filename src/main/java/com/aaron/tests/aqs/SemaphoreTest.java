package com.aaron.tests.aqs;

import cn.hutool.core.date.DateUtil;

import java.util.Date;
import java.util.concurrent.Semaphore;

/**
 * @author liurong
 * @version 1.0
 * @date 2022/9/26 15:30
 */
public class SemaphoreTest {

    public static void main(String[] args) {
        // 默认非公平锁
        Semaphore semaphore = new Semaphore(3, true);
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                try {
                    semaphore.acquire();
                    System.out.println(DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss")+":"+ Thread.currentThread().getName() + "抢到了");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {

                } finally {
                    semaphore.release();
                }
            }).start();
        }
    }


}

