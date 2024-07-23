package com.luyou.bi.controller;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;


/**
 * @author 鹿又笑
 * @create 2024/7/23-10:33
 * @description
 */
@RestController
@RequestMapping("/queue")
@Slf4j
@Profile({"dev", "test"})
public class QueueController {

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    @GetMapping("/add")
    public void add(String name) {
        CompletableFuture.runAsync(() -> {
            log.info("执行者=" + Thread.currentThread().getName() + ", 任务=" + name);
            try {
                Thread.sleep(600000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, threadPoolExecutor);
    }

    @GetMapping("get")
    public String get() {
        HashMap<String, Object> hashMap = new HashMap<>();
        int size = threadPoolExecutor.getQueue().size();
        hashMap.put("核心线程数", threadPoolExecutor.getCorePoolSize());
        hashMap.put("最大线程数", threadPoolExecutor.getMaximumPoolSize());
        hashMap.put("队列长度", size);
        hashMap.put("活跃线程数/正在工作的线程数", threadPoolExecutor.getActiveCount());
        hashMap.put("任务总数", threadPoolExecutor.getTaskCount());
        hashMap.put("已完成任务数", threadPoolExecutor.getCompletedTaskCount());
        // 将map转换为JSON字符串并返回
        return JSONUtil.toJsonStr(hashMap);
    }


}
