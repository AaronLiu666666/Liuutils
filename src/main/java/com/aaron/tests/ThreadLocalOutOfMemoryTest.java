package com.aaron.tests;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * ThreadLocal 内存泄露测试
 * <a href="https://juejin.cn/post/6982121384533032991">ThreadLocal内存泄漏案例分析实战</a>
 *
 * @author liurong
 * @version 1.0
 * @date 2022/9/22 14:50
 */
public class ThreadLocalOutOfMemoryTest {

    static class LocalVariable {
        // 5M
        private byte[] local = new byte[1024 * 1024 * 5];
    }

    final static ThreadPoolExecutor POOL_EXECUTOR = new ThreadPoolExecutor(6, 6, 1, TimeUnit.MINUTES, new LinkedBlockingDeque<>());

    static ThreadLocal<LocalVariable> localVariable = new ThreadLocal<LocalVariable>();

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 50; i++) {
            POOL_EXECUTOR.execute(new Runnable() {
                @Override
                public void run() {
                    LocalVariable localVariable = new LocalVariable();
                    ThreadLocalOutOfMemoryTest.localVariable.set(localVariable);
                    // (5) 手动清理ThreadLocal
                    System.out.println("thread name end：" + Thread.currentThread().getName() + ", value:"+ ThreadLocalOutOfMemoryTest.localVariable.get());
                    ThreadLocalOutOfMemoryTest.localVariable.remove();

                }
            });
            Thread.sleep(1000);
        }
        // (6)是否让key失效，都不影响。只要持有的线程存在，都无法回收。
        //ThreadLocalOutOfMemoryTest.localVariable = null;
        System.out.println("pool execute over");
    }

}

