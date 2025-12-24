-- 火车订票系统数据库初始化脚本
-- 创建日期: 2025-12-24
-- 数据库: railbooking

-- 创建数据库
CREATE DATABASE IF NOT EXISTS railbooking DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE railbooking;

-- 用户表
CREATE TABLE IF NOT EXISTS `users` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码',
    `email` VARCHAR(100) COMMENT '邮箱',
    `phone` VARCHAR(20) COMMENT '手机号',
    `real_name` VARCHAR(50) COMMENT '真实姓名',
    `id_card` VARCHAR(18) COMMENT '身份证号',
    `role` VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '角色: USER-普通用户, ADMIN-管理员',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` INT NOT NULL DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 车站表
CREATE TABLE IF NOT EXISTS `stations` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '车站ID',
    `name` VARCHAR(50) NOT NULL COMMENT '车站名称',
    `city` VARCHAR(50) NOT NULL COMMENT '所在城市',
    `abbreviation` VARCHAR(10) COMMENT '车站简称',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车站表';

-- 车次表
CREATE TABLE IF NOT EXISTS `trains` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '车次ID',
    `train_code` VARCHAR(20) NOT NULL COMMENT '车次编号',
    `train_type` VARCHAR(10) NOT NULL COMMENT '车次类型: G-高铁, D-动车, K-普快',
    `departure_station_id` BIGINT COMMENT '出发站ID',
    `departure_station_name` VARCHAR(50) NOT NULL COMMENT '出发站名称',
    `arrival_station_id` BIGINT COMMENT '到达站ID',
    `arrival_station_name` VARCHAR(50) NOT NULL COMMENT '到达站名称',
    `departure_time` TIME NOT NULL COMMENT '出发时间',
    `arrival_time` TIME NOT NULL COMMENT '到达时间',
    `run_days` INT NOT NULL DEFAULT 0 COMMENT '运行天数: 0-当日到达, 1-次日到达',
    `duration` INT COMMENT '运行时长(分钟)',
    `first_class_price` DECIMAL(10,2) COMMENT '一等座价格',
    `second_class_price` DECIMAL(10,2) COMMENT '二等座价格',
    `hard_sleeper_price` DECIMAL(10,2) COMMENT '硬卧价格',
    `soft_sleeper_price` DECIMAL(10,2) COMMENT '软卧价格',
    `total_seats` INT DEFAULT 100 COMMENT '座位总数',
    `operate_date_start` DATE COMMENT '运营开始日期',
    `operate_date_end` DATE COMMENT '运营结束日期',
    `status` INT NOT NULL DEFAULT 1 COMMENT '状态: 0-停运, 1-运营中',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_train_code` (`train_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车次表';

-- 座位表
CREATE TABLE IF NOT EXISTS `seats` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '座位ID',
    `train_id` BIGINT NOT NULL COMMENT '车次ID',
    `train_code` VARCHAR(20) NOT NULL COMMENT '车次编号',
    `carriage_no` INT NOT NULL COMMENT '车厢号',
    `seat_no` VARCHAR(10) NOT NULL COMMENT '座位号',
    `seat_type` VARCHAR(20) NOT NULL COMMENT '座位类型',
    `status` INT NOT NULL DEFAULT 0 COMMENT '状态: 0-空闲, 1-已锁定, 2-已售出',
    `travel_date` DATE NOT NULL COMMENT '乘车日期',
    `price` DECIMAL(10,2) COMMENT '价格',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_train_date` (`train_id`, `travel_date`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='座位表';

-- 订单表
CREATE TABLE IF NOT EXISTS `orders` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `order_no` VARCHAR(50) NOT NULL COMMENT '订单号',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `train_id` BIGINT NOT NULL COMMENT '车次ID',
    `train_code` VARCHAR(20) NOT NULL COMMENT '车次编号',
    `departure_station` VARCHAR(50) NOT NULL COMMENT '出发站',
    `arrival_station` VARCHAR(50) NOT NULL COMMENT '到达站',
    `departure_time` VARCHAR(20) NOT NULL COMMENT '出发时间',
    `arrival_time` VARCHAR(20) NOT NULL COMMENT '到达时间',
    `travel_date` VARCHAR(20) NOT NULL COMMENT '乘车日期',
    `passenger_name` VARCHAR(50) NOT NULL COMMENT '乘客姓名',
    `passenger_id_card` VARCHAR(18) NOT NULL COMMENT '乘客身份证号',
    `seat_type` VARCHAR(20) NOT NULL COMMENT '座位类型',
    `carriage_no` INT COMMENT '车厢号',
    `seat_no` VARCHAR(10) COMMENT '座位号',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '订单金额',
    `status` INT NOT NULL DEFAULT 0 COMMENT '状态: 0-待支付, 1-已支付, 2-已取消, 3-已退票',
    `pay_deadline` DATETIME COMMENT '支付截止时间',
    `pay_time` DATETIME COMMENT '支付时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark` VARCHAR(500) COMMENT '备注',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 插入管理员用户（密码: admin123）
-- BCrypt 加密后的密码
INSERT INTO `users` (`username`, `password`, `email`, `phone`, `real_name`, `id_card`, `role`) VALUES
('admin', '$2a$10$EqKcp1WFKVQISheBxnGRqOQMjDGMwLpLBx5GKPBHZLF5xvGvKq5Aq', 'admin@railbooking.com', '13800000000', '管理员', '110101199001011234', 'ADMIN'),
('test', '$2a$10$EqKcp1WFKVQISheBxnGRqOQMjDGMwLpLBx5GKPBHZLF5xvGvKq5Aq', 'test@railbooking.com', '13900000000', '测试用户', '110101199001011235', 'USER');

-- 插入车站数据
INSERT INTO `stations` (`name`, `city`, `abbreviation`) VALUES
('北京南', '北京', 'BJN'),
('上海虹桥', '上海', 'SHH'),
('广州南', '广州', 'GZN'),
('深圳北', '深圳', 'SZB'),
('杭州东', '杭州', 'HZD'),
('南京南', '南京', 'RNH'),
('武汉站', '武汉', 'WHZ'),
('成都东', '成都', 'CDD'),
('重庆北', '重庆', 'CBB'),
('西安北', '西安', 'XAB');

-- 插入车次数据
INSERT INTO `trains` (`train_code`, `train_type`, `departure_station_name`, `arrival_station_name`, `departure_time`, `arrival_time`, `run_days`, `duration`, `first_class_price`, `second_class_price`, `hard_sleeper_price`, `soft_sleeper_price`, `total_seats`, `operate_date_start`, `operate_date_end`, `status`) VALUES
('G101', 'G', '北京南', '上海虹桥', '08:00:00', '12:36:00', 0, 276, 599.00, 553.00, 0.00, 0.00, 100, '2025-01-01', '2025-12-31', 1),
('G103', 'G', '北京南', '上海虹桥', '09:00:00', '13:30:00', 0, 270, 599.00, 553.00, 0.00, 0.00, 100, '2025-01-01', '2025-12-31', 1),
('G105', 'G', '北京南', '上海虹桥', '10:00:00', '14:20:00', 0, 260, 579.00, 533.00, 0.00, 0.00, 100, '2025-01-01', '2025-12-31', 1),
('D201', 'D', '武汉站', '成都东', '07:30:00', '14:00:00', 0, 390, 0.00, 0.00, 180.00, 200.00, 80, '2025-01-01', '2025-12-31', 1),
('G301', 'G', '广州南', '深圳北', '08:30:00', '09:20:00', 0, 50, 99.50, 82.00, 0.00, 0.00, 100, '2025-01-01', '2025-12-31', 1),
('K501', 'K', '西安北', '北京西', '18:00:00', '08:30:00', 1, 870, 0.00, 0.00, 200.00, 300.00, 60, '2025-01-01', '2025-12-31', 1);

-- 插入更多车次数据
INSERT INTO `trains` (`train_code`, `train_type`, `departure_station_name`, `arrival_station_name`, `departure_time`, `arrival_time`, `run_days`, `duration`, `first_class_price`, `second_class_price`, `hard_sleeper_price`, `soft_sleeper_price`, `total_seats`, `operate_date_start`, `operate_date_end`, `status`) VALUES
('G102', 'G', '上海虹桥', '北京南', '08:00:00', '12:36:00', 0, 276, 599.00, 553.00, 0.00, 0.00, 100, '2025-01-01', '2025-12-31', 1),
('G104', 'G', '上海虹桥', '北京南', '09:00:00', '13:30:00', 0, 270, 599.00, 553.00, 0.00, 0.00, 100, '2025-01-01', '2025-12-31', 1),
('G302', 'G', '深圳北', '广州南', '08:30:09', '20:00', 0, 50, 99.50, 82.00, 0.00, 0.00, 100, '2025-01-01', '2025-12-31', 1),
('D202', 'D', '成都东', '武汉站', '07:30:00', '14:00:00', 0, 390, 0.00, 0.00, 180.00, 200.00, 80, '2025-01-01', '2025-12-31', 1),
('G401', 'G', '杭州东', '南京南', '07:00:00', '08:30:00', 0, 90, 117.00, 98.00, 0.00, 0.00, 100, '2025-01-01', '2025-12-31', 1),
('G403', 'G', '杭州东', '南京南', '09:00:00', '10:25:00', 0, 85, 117.00, 98.00, 0.00, 0.00, 100, '2025-01-01', '2025-12-31', 1),
('K502', 'K', '北京西', '西安北', '18:00:00', '08:30:00', 1, 870, 0.00, 0.00, 200.00, 300.00, 60, '2025-01-01', '2025-12-31', 1);

-- 座位配置表（用于初始化座位时参考）
CREATE TABLE IF NOT EXISTS `carriage_config` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '配置ID',
    `train_type` VARCHAR(10) NOT NULL COMMENT '车次类型',
    `carriage_no` INT NOT NULL COMMENT '车厢号',
    `seat_count` INT NOT NULL COMMENT '座位数',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车厢配置表';

CREATE TABLE IF NOT EXISTS `seat_config` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '配置ID',
    `seat_type` VARCHAR(20) NOT NULL COMMENT '座位类型',
    `seat_prefix` VARCHAR(10) COMMENT '座位前缀',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='座位配置表';

-- 插入车厢配置
INSERT INTO `carriage_config` (`train_type`, `carriage_no`, `seat_count`) VALUES
('G', 1, 40), ('G', 2, 40), ('G', 3, 40), ('G', 4, 40), ('G', 5, 40),
('D', 1, 60), ('D', 2, 60), ('D', 3, 60),
('K', 1, 60), ('K', 2, 60), ('K', 3, 60);

-- 插入座位配置
INSERT INTO `seat_config` (`seat_type`, `seat_prefix`) VALUES
('SECOND_CLASS', '2'),
('FIRST_CLASS', '1'),
('HARD_SLEEPER', '3'),
('SOFT_SLEEPER', '4');
