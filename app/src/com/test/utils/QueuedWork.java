package com.test.utils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @Copyright © 2015 Umeng Inc. All rights reserved.
 * @Description: 单例线程工作池
 * @Version: 1.0
 * @Create: 2015年11月19日 上午12:32:24
 * @Author: sanbo
 */
public class QueuedWork {
    private static List<WeakReference<ScheduledFuture<?>>> queue = new ArrayList<WeakReference<ScheduledFuture<?>>> ();
    private static ExecutorService executor = Executors.newSingleThreadExecutor ();
    private static long MAX_WAIT_SECONDS = 5;

    /**
     * Single thread pool for trivial file operation
     *
     * @param command
     */
    public static void execute (Runnable command) {
        if(executor.isShutdown ()) {
            executor = Executors.newSingleThreadExecutor ();
        }

        executor.execute (command);
    }

    public static void waitForAsyncTask () {
        try {
            for (WeakReference<ScheduledFuture<?>> reference : queue) {
                ScheduledFuture<?> f = reference.get ();
                if(f != null) {
                    f.cancel (false);
                }
            }
            queue.clear ();

            if(!executor.isShutdown ())
                executor.shutdown ();
            if(!executor2.isShutdown ())
                executor2.shutdown ();

            executor.awaitTermination (MAX_WAIT_SECONDS, TimeUnit.SECONDS);
            executor2.awaitTermination (MAX_WAIT_SECONDS, TimeUnit.SECONDS);
        } catch (Exception ignore) {
        }
    }

    private static ScheduledExecutorService executor2 = Executors.newSingleThreadScheduledExecutor ();

    /**
     * Thread pool for caching service, file or net operation
     *
     * @param command
     */
    public synchronized static void post (Runnable command) {

        if(executor2.isShutdown ()) {
            executor2 = Executors.newSingleThreadScheduledExecutor ();
        }
        executor2.execute (command);
    }

    public synchronized static void postDelayed (Runnable command, long delay) {
        if(executor2.isShutdown ()) {
            executor2 = Executors.newSingleThreadScheduledExecutor ();
        }

        queue.add (new WeakReference<ScheduledFuture<?>> (executor2.schedule (command, delay, TimeUnit.MILLISECONDS)));
    }

    public synchronized static void postSync (Runnable command) {
        if(executor2.isShutdown ()) {
            executor2 = Executors.newSingleThreadScheduledExecutor ();
        }

        Future<?> f = executor2.submit (command);
        try {
            f.get (5, TimeUnit.SECONDS);
        } catch (Exception ignore) {
        }
    }
}
