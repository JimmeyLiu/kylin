package org.kylin.bootstrap.service;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * RoundRobin Tester.
 *
 * @author Jim
 * @since 07/17/2015
 */
public class RoundRobinTest {

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    @Test
    public void testConcurrentModified() throws InterruptedException {
        final RoundRobin<Integer> rb = new RoundRobin<Integer>();
        final AtomicInteger seq = new AtomicInteger();

        final CountDownLatch latch = new CountDownLatch(1000);
        //add thread
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (; ; ) {
                    try {
                        latch.countDown();
                        rb.add(seq.incrementAndGet());
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
//
//        //add thread
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                for (; ; ) {
//                    try {
//                        Thread.sleep(2000);
//                        rb.remove(seq.get());
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }).start();

        for (int i = 0; i < 1000; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (; ; ) {
                        try {
                            long s = System.nanoTime();
                            rb.next();
                            System.out.println(System.nanoTime() - s);
                            Thread.sleep(10);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }
        latch.await();

    }

} 
