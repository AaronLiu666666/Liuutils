package com.aaron.tests.aqs;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * 自定义同步器
 * 自定义同步器时需要重写下面几个AQS提供的模板方法
 * isHeldExclusively()//该线程是否正在独占资源。只有用到condition才需要去实现它。
 * tryAcquire(int)//独占方式。尝试获取资源，成功则返回true，失败则返回false。
 * tryRelease(int)//独占方式。尝试释放资源，成功则返回true，失败则返回false。
 * tryAcquireShared(int)//共享方式。尝试获取资源。负数表示失败；0表示成功，但没有剩余可用资源；正数表示成功，且有剩余资源。
 * tryReleaseShared(int)//共享方式。尝试释放资源，成功则返回true，失败则返回false。
 *
 * @author liurong
 * @version 1.0
 * @date 2022/9/26 13:39
 */
public class MyAQSLock extends AbstractQueuedSynchronizer {

    public static void main(String[] args) {
        MyAQSLock myAQSLock = new MyAQSLock();
//        myAQSLock.acquire();
//        myAQSLock.release();
    }

    @Override
    protected boolean tryAcquire(int arg) {
        return super.tryAcquire(arg);
    }

    @Override
    protected boolean tryRelease(int arg) {
        return super.tryRelease(arg);
    }

    @Override
    protected int tryAcquireShared(int arg) {
        return super.tryAcquireShared(arg);
    }

    @Override
    protected boolean tryReleaseShared(int arg) {
        return super.tryReleaseShared(arg);
    }

    @Override
    protected boolean isHeldExclusively() {
        return super.isHeldExclusively();
    }
}

/*
    note:
        笑了，抽象类AQS没有抽象方法，但是那几个方法你不重写，直接super就直接抛异常
 */