package com.luyou.bi.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author 鹿又笑
 * @create 2024/7/23-10:34
 * @description
 */
@Configuration
public class ThreadPoolExecutorConfig {

    private final int corePoolSize = 2;
    private final int maximumPoolSize = 4;
    private final long keepAliveTime = 10;
    private final String threadNamePrefix = "luyou-bi-thread-pool-";

    @Bean
    public ThreadPoolExecutor threadPoolExecutor () {
        ThreadFactory threadFactory = new ThreadFactory() {
            int count = 1;
            @Override
            public Thread newThread(@NotNull Runnable r) {

                Thread thread = new Thread(r);
                thread.setName("线程" + count);
                count++;
                return thread;
            }
        };

        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(2), threadFactory);
    }

}
