package com.luyou.bi.manager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author 鹿又笑
 * @create 2024/7/19-15:22
 * @description
 */
@SpringBootTest
class RedisLimiterManagerTest {

    @Resource
    private RedisLimitManager redisLimiterManager;

    @Test
    void doRateLimit() throws InterruptedException {
        // 模拟一下操作
        String userId = "1";
        // 瞬间执行2次,每成功一次,就打印'成功'
        for (int i = 0; i < 2; i++) {
            redisLimiterManager.doRateLimit(userId);
            System.out.println("成功");
        }
        // 睡1秒
        Thread.sleep(1000);
        // 瞬间执行5次,每成功一次,就打印'成功'
        for (int i = 0; i < 5; i++) {
            redisLimiterManager.doRateLimit(userId);
            System.out.println("成功");
        }
    }
}