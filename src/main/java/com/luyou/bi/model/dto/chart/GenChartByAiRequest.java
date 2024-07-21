package com.luyou.bi.model.dto.chart;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: 鹿又笑
 * @Create: 2024/7/15 20:58
 * @description:
 */
@Data
public class GenChartByAiRequest implements Serializable {

    /**
     * 图表名称
     */
    private String name;

    /**
     * 分析目标
     */
    private String goal;

    /**
     * 图表类型
     */
    private String chartType;

}
