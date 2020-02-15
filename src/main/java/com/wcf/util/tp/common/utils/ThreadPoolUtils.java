package com.wcf.util.tp.common.utils;

import java.util.concurrent.*;

public class ThreadPoolUtils {

    private static ExecutorService executor;

    /**
     * 创建线程池
     */
    public static void createThreadPool(){
        BlockingDeque<Runnable> workQueue=new LinkedBlockingDeque<>(8);
        executor=new ThreadPoolExecutor(5,8,60,
                TimeUnit.SECONDS,workQueue,new ThreadPoolExecutor.AbortPolicy());
    }

    /**
     * 获取线程池
     * @return
     */
    public static ExecutorService getExecutor(){
        return executor;
    }

    /**
     * 关闭线程池
     */
    public static void shutdown(){
        executor.shutdown();
    }
}
