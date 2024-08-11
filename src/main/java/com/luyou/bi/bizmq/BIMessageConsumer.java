package com.luyou.bi.bizmq;

import com.luyou.bi.common.ErrorCode;
import com.luyou.bi.constant.AIModelIDConstant;
import com.luyou.bi.constant.BIMQConstant;
import com.luyou.bi.constant.GenChartStatusConstant;
import com.luyou.bi.exception.BusinessException;
import com.luyou.bi.exception.ThrowUtils;
import com.luyou.bi.manager.AIManager;
import com.luyou.bi.model.entity.Chart;
import com.luyou.bi.service.ChartService;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author 鹿又笑
 * @create 2024/7/26-14:35
 * @description
 */
@Component
@Slf4j
public class BIMessageConsumer {

    @Resource
    private ChartService chartService;

    @Resource
    private AIManager aiManager;

    @SneakyThrows
    @RabbitListener(queues = {BIMQConstant.QUEUENAME}, ackMode = "MANUAL")
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG)long deliveryTag) {
        log.info("receiveMessage message = {}", message);
        if (StringUtils.isBlank(message)) {
            // 如果更新失败，拒绝当前消息，让消息重新进入队列
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "消息为空");
        }
        long charId = Long.parseLong(message);
        Chart chart = chartService.getById(charId);
        if (chart == null) {
            // 如果消息为空，拒绝消息并会抛出业务异常
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "图表为空");
        }

        Chart updateChart = new Chart();
        updateChart.setId(chart.getId());
        updateChart.setStatus(GenChartStatusConstant.RUNNING);
        boolean b = chartService.updateById(updateChart);
        if (!b) {
            handleChartUpdateError(chart.getId(), "更新图表执行中状态失败");
            return;
        }

        // 调用AI
        // 拿到返回结果
        String result = aiManager.doChat(AIModelIDConstant.CHART_MODEL_ID, buildUserInput(chart).toString());
        // 对返回结果做拆分
        String[] splits = result.split("【【【【【");
        if (splits.length < 3) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI生成错误");
        }
        String genChart = splits[1].trim();
        String genResult = splits[2].trim();
        // 将生成的图表json和分析信息保存到数据库，更新状态信息
        Chart resChart = new Chart();
        resChart.setId(chart.getId());
        resChart.setGenChart(genChart);
        resChart.setGenResult(genResult);
        resChart.setStatus(GenChartStatusConstant.SUCCEED);
        boolean updatedById = chartService.updateById(resChart);
        if (!updatedById) {
            handleChartUpdateError(chart.getId(), "更新图表成功状态失败");
        }

        // 手动确认消息的接收，向RabbitMQ发送确认消息
        channel.basicAck(deliveryTag, false);

    }

    /**
     * 更新图表状态失败处理
     * @param id
     * @param execMessage
     */
    private void handleChartUpdateError(Long id, String execMessage) {
        Chart handleErrorChart = new Chart();
        handleErrorChart.setId(id);
        handleErrorChart.setExecMessage(execMessage);
        handleErrorChart.setStatus(GenChartStatusConstant.FAILED);
        boolean b = chartService.updateById(handleErrorChart);
        ThrowUtils.throwIf(b, ErrorCode.SYSTEM_ERROR, "更新图表状态失败处理失败");
    }

    private StringBuilder buildUserInput(Chart chart) {
        StringBuilder userInput = new StringBuilder();
        userInput.append("分析需求:").append("\n");
        // 拼接分析目标
        String userGoal = chart.getGoal();
        String chartType = chart.getChartType();
        if (StringUtils.isNotBlank(chartType)) {
            // 将分析目标拼接上 ”请使用“ + 图表类型
            userGoal = "请使用" + chartType + userGoal;
        }
        userInput.append(userGoal).append("\n");
        userInput.append("原始数据:").append("\n");
        // 压缩后的数据（CSV）
        String csvData = chart.getChartData();
        userInput.append(csvData).append("\n");
        return userInput;
    }

}
