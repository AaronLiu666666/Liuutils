package com.aaron.tests.aqs;

import java.util.concurrent.atomic.AtomicReference;

/**
 * CLH锁
 * <a href="https://funzzz.fun/2021/05/19/CLH%E9%94%81/">CLH lock</a>
 *
 * @author liurong
 * @version 1.0
 * @date 2022/9/23 9:55
 */
public class CLHLock {

    // 尾节点指针
    private final AtomicReference<QNode> tail;

    private final ThreadLocal<QNode> myPredThreadLocal;

    private final ThreadLocal<QNode> myNodeThreadLocal;

    class QNode {
        // 自旋 cas
        volatile boolean locked;
    }

    // myNode

    public CLHLock() {
        this.tail = new AtomicReference<>(new QNode());
        /*
            withInitial:
                ThreadLocal.get 先从ThreadLocalMap中获取，获取到则返回
                    获取不到则运行 return setInitialValue()
                    setInitialValue调用T value = initialValue();
                        如果是ThreadLocal类直接返回null，而withInitial方法返回的是new SuppliedThreadLocal<>(supplier)
            即：ThreadLocal.withInitial(Supplier)，lazy，调用get时才会supplier.get()
         */
        this.myNodeThreadLocal = ThreadLocal.withInitial(QNode::new);
        this.myPredThreadLocal = new ThreadLocal<>();
    }

    public void lock() {
        // 获取当前线程的节点
        QNode node = this.myNodeThreadLocal.get();
        // 将当前节点的锁状态设置为true
        node.locked = true;
        // 将自己设为尾节点，并返回上一个尾节点
        QNode pred = this.tail.getAndSet(node);
        // 设置前驱节点（上一个尾节点作为前驱节点）
        this.myPredThreadLocal.set(pred);
        // 自旋等待前驱节点释放锁
        while (pred.locked) {
            // 自旋
        }
    }

    public void unlock() {
        // 获取当前线程的节点
        QNode qNode = this.myNodeThreadLocal.get();
        // 将当前节点的锁状态设置为false
        qNode.locked = false;

        // 防止死锁。如果没有下一句，若当前线程unlock后迅速竞争到锁，由于当前线程还保存着自己的node,所以`QNode node = this.myNodeThreadLocal.get();`获取的依旧是该线程的node(此时该node还被链表的下一个节点引用)，执行lock后把自己的locked = true然后把自己又加在尾部，然而链表的下一个节点还在等该线程的locked = false而当前节点还在等自己之前的节点locked = false，1->3->2 1在等2执行,2在等3执行,3又必须让1先执行完。
        // 所以防止上述事情的发生，释放锁时不能允许当前线程还保存自己的node，防止该线程再次抢占线程发生死锁。
        this.myNodeThreadLocal.set(this.myPredThreadLocal.get());
    }

    static class kfc {
        private final CLHLock lock = new CLHLock();
        private int i = 0;
        public void takeout() {
            try {
                lock.lock();
                System.out.println(Thread.currentThread().getName() + " takeout"+ ++i);
            } finally {
                lock.unlock();
            }
        }
    }

    // cas 公平锁
    public static void main(String[] args) {
        final kfc kfc = new kfc();
        for (int i = 0; i <= 3; i++) {
            new Thread(kfc::takeout).start();
        }
    }


}
/*
    note:
        CLH: CLH是一个基于 链表（队列） 非线程饥饿的 自旋（公平）锁。

        饥饿：
            一个或者多个线程因为种种原因无法获得所需要的资源，导致一直无法执行的状态。一直有线程级别高的暂用资源，线程低的一直处在饥饿状态。
            比如ReentrantLock锁里提供的不公平锁机制，不公平锁能够提高吞吐量但不可避免的会造成某些线程的饥饿（自己老是抢不到锁就被饿死了）

        lock&&unlock分析：
            myNode: 当前线程节点&&ThreadLocal&&SupplierThreadLocal lazy
            predNode: 前驱节点&&ThreadLocal&&初始为ThreadLocal()
            tail: 记录尾节点&&QNode cas变量&&初始为new QNode()
 */

