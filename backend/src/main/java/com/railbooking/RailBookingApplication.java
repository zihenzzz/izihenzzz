package com.railbooking;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 火车订票系统启动类
 */
@SpringBootApplication
@MapperScan("com.railbooking.mapper")
@EnableScheduling
public class RailBookingApplication {

    public static void main(String[] args) {
        SpringApplication.run(RailBookingApplication.class, args);
        System.out.println("========================================");
        System.out.println("    火车订票系统启动成功!");
        System.out.println("    API文档: http://localhost:8080/swagger-ui.html");
        System.out.println("========================================");
    }
}
