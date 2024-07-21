package com.luyou.bi.manager;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
/**
 *@Author: 鹿又笑
 *@Create: 2024/7/16 15:52
 *@description: 
*/
@SpringBootTest
class AIManagerTest {

    @Resource
    private AIManager aiManager;
// 1813497699319611393L
    @Test
    void doChat() {
        String ans = aiManager.doChat(1813497699319611393L, "你是谁");
        System.out.println(ans);
    }

}