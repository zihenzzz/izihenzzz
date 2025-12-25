package com.railbooking.scheduled;

import com.railbooking.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时任务服务
 */
@Component
public class ScheduledTasks {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    private final OrderService orderService;

    public ScheduledTasks(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 每5分钟检查一次过期订单
     */
    @Scheduled(fixedRate = 5 * 60 * 1000) // 5分钟
    public void cleanupExpiredOrders() {
        try {
            log.info("开始清理过期订单...");
            orderService.releaseExpiredLockedSeats();
            log.info("过期订单清理完成");
        } catch (Exception e) {
            log.error("清理过期订单失败", e);
        }
    }

    /**
     * 每天凌晨2点初始化次日车次座位
     */
    @Scheduled(cron = "0 0 2 * * ?") // 每天凌晨2点
    public void initNextDaySeats() {
        try {
            log.info("开始初始化次日车次座位...");
            // 这里可以添加逻辑来初始化明天的车次座位
            // 实际实现中需要根据运营计划来创建座位
            log.info("次日车次座位初始化完成");
        } catch (Exception e) {
            log.error("初始化次日车次座位失败", e);
        }
    }

    /**
     * 每周日凌晨3点生成周报数据
     */
    @Scheduled(cron = "0 0 3 ? * SUN") // 每周日凌晨3点
    public void generateWeeklyReport() {
        try {
            log.info("开始生成周报数据...");
            // 这里可以添加生成周报的逻辑
            // 比如统计本周的订单量、收入、热门线路等
            log.info("周报数据生成完成");
        } catch (Exception e) {
            log.error("生成周报数据失败", e);
        }
    }
}
