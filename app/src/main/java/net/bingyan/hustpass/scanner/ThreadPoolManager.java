package net.bingyan.hustpass.scanner;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by lwenkun on 2016/12/17.
 */

public class ThreadPoolManager {

    private Handler mainThread = new Handler(Looper.getMainLooper());

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors() + 1;
    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));
    private static final int MAX_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final long KEEP_ALIVE_SECONDS = 30;

    private static final BlockingDeque<Runnable> queue = new LinkedBlockingDeque<>(128);

    private ThreadPoolExecutor threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE,
            KEEP_ALIVE_SECONDS, TimeUnit.SECONDS, queue);

    private ThreadPoolManager() {
    }

    public static ThreadPoolManager getInstance() {
        return InstanceHolder.instance;
    }

    private static class InstanceHolder {
        private static ThreadPoolManager instance = new ThreadPoolManager();
    }

    public void submit(Runnable task) {
        threadPool.submit(task);
    }

    public <T> void submit(Callable<T> task, final OnTaskListener<T> listener) {

        final Callable<?> c = task;

        if (listener != null)
            listener.onPreTask();

        threadPool.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    final T result = (T) c.call();
                    mainThread.post(new Runnable() {
                        @Override
                        public void run() {
                            if (listener != null) {
                                Log.d("ThreadPoolManager", " --> onFinish is going to be called");
                                listener.onFinish(result);
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
