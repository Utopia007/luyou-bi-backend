package com.luyou.bi.manager;

import com.luyou.bi.common.ErrorCode;
import com.luyou.bi.exception.BusinessException;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author 鹿又笑
 * @create 2024/7/19-11:06
 * @description redisson限流管理器
 */
@Service
public class RedisLimitManager {

    @Resource
    private RedissonClient redissonClient;

    /**
     * 限流
     * @param key key 区分不同的限流器，比如不同的用户 id 应该分别统计
     */
    public void doRateLimit(String key) {
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        // 限流器的统计规则(每秒2个请求;连续的请求,最多只能有1个请求被允许通过)
        // RateType.OVERALL表示速率限制作用于整个令牌桶,即限制所有请求的速率, 每 1 秒允许 2 个请求拿到令牌通过
        rateLimiter.trySetRate(RateType.OVERALL, 2, 1, RateIntervalUnit.SECONDS);
        // 一个操作来了后请求一个令牌
        boolean canOperate = rateLimiter.tryAcquire(1);
        if (!canOperate) {
            throw new BusinessException(ErrorCode.TOO_MANY_REQUEST);
        }
    }

}
