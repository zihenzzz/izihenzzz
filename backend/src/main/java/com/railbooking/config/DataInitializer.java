package com.railbooking.config;

import com.railbooking.entity.Seat;
import com.railbooking.entity.Train;
import com.railbooking.entity.User;
import com.railbooking.mapper.SeatMapper;
import com.railbooking.mapper.TrainMapper;
import com.railbooking.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 数据初始化器 - 确保测试用户和座位数据存在
 */
@Slf4j
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserMapper userMapper;
    private final TrainMapper trainMapper;
    private final SeatMapper seatMapper;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserMapper userMapper, TrainMapper trainMapper, 
                          SeatMapper seatMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.trainMapper = trainMapper;
        this.seatMapper = seatMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // 检查并创建管理员用户
        initUser("admin", "admin123", "ADMIN", "管理员", "admin@busbooking.com");
        // 检查并创建测试用户
        initUser("test", "test123", "USER", "测试用户", "test@busbooking.com");
        
        // 初始化座位数据
        initSeats();
        
        log.info("汽车票预订系统数据初始化完成");
    }

    private void initUser(String username, String password, String role, String realName, String email) {
        User existingUser = userMapper.selectByUsername(username);
        if (existingUser == null) {
            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(role);
            user.setRealName(realName);
            user.setEmail(email);
            user.setPhone("13800000000");
            user.setIdCard("110101199001011234");
            userMapper.insert(user);
            log.info("创建用户: {} ({})", username, role);
        } else {
            // 更新密码为BCrypt格式
            String encodedPassword = passwordEncoder.encode(password);
            existingUser.setPassword(encodedPassword);
            userMapper.updateById(existingUser);
            log.info("更新用户密码: {}", username);
        }
    }
    
    /**
     * 初始化座位数据 - 为未来7天的所有班次生成座位
     */
    private void initSeats() {
        List<Train> trains = trainMapper.selectList(null);
        if (trains == null || trains.isEmpty()) {
            log.warn("没有班次数据，跳过座位初始化");
            return;
        }
        
        LocalDate today = LocalDate.now();
        int daysToGenerate = 7; // 生成未来7天的座位
        
        for (Train train : trains) {
            for (int day = 0; day < daysToGenerate; day++) {
                LocalDate travelDate = today.plusDays(day);
                generateSeatsForTrain(train, travelDate);
            }
        }
        
        log.info("座位数据初始化完成，共处理 {} 个班次", trains.size());
    }
    
    /**
     * 为指定班次和日期生成座位
     */
    private void generateSeatsForTrain(Train train, LocalDate travelDate) {
        // 检查是否已有座位数据
        Long existingCount = seatMapper.selectCount(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Seat>()
                .eq(Seat::getTrainId, train.getId())
                .eq(Seat::getTravelDate, travelDate)
        );
        
        if (existingCount > 0) {
            return; // 已有座位数据，跳过
        }
        
        String trainType = train.getTrainType();
        int totalSeats = train.getTotalSeats() != null ? train.getTotalSeats() : 45;
        
        if ("SLEEPER".equals(trainType)) {
            // 卧铺车：生成上铺和下铺
            generateSleeperSeats(train, travelDate, totalSeats);
        } else {
            // 普通车/快客：生成普通座和商务座
            generateNormalSeats(train, travelDate, totalSeats);
        }
    }
    
    /**
     * 生成普通座位（普通座 + 商务座）
     */
    private void generateNormalSeats(Train train, LocalDate travelDate, int totalSeats) {
        int businessSeats = 8; // 商务座数量
        int normalSeats = totalSeats - businessSeats; // 普通座数量
        
        // 生成商务座
        if (train.getFirstClassPrice() != null && train.getFirstClassPrice().compareTo(BigDecimal.ZERO) > 0) {
            for (int i = 1; i <= businessSeats; i++) {
                createSeat(train, travelDate, "FIRST_CLASS", "A" + i, train.getFirstClassPrice());
            }
        }
        
        // 生成普通座
        if (train.getSecondClassPrice() != null && train.getSecondClassPrice().compareTo(BigDecimal.ZERO) > 0) {
            for (int i = 1; i <= normalSeats; i++) {
                String seatNo = String.format("%02d", i);
                createSeat(train, travelDate, "SECOND_CLASS", seatNo, train.getSecondClassPrice());
            }
        }
    }
    
    /**
     * 生成卧铺座位（上铺 + 下铺）
     */
    private void generateSleeperSeats(Train train, LocalDate travelDate, int totalSeats) {
        int halfSeats = totalSeats / 2;
        
        // 生成下铺
        if (train.getHardSleeperPrice() != null && train.getHardSleeperPrice().compareTo(BigDecimal.ZERO) > 0) {
            for (int i = 1; i <= halfSeats; i++) {
                createSeat(train, travelDate, "HARD_SLEEPER", "下" + i, train.getHardSleeperPrice());
            }
        }
        
        // 生成上铺
        if (train.getSoftSleeperPrice() != null && train.getSoftSleeperPrice().compareTo(BigDecimal.ZERO) > 0) {
            for (int i = 1; i <= halfSeats; i++) {
                createSeat(train, travelDate, "SOFT_SLEEPER", "上" + i, train.getSoftSleeperPrice());
            }
        }
    }
    
    /**
     * 创建单个座位
     */
    private void createSeat(Train train, LocalDate travelDate, String seatType, String seatNo, BigDecimal price) {
        Seat seat = new Seat();
        seat.setTrainId(train.getId());
        seat.setTrainCode(train.getTrainCode());
        seat.setCarriageNo(1);
        seat.setSeatNo(seatNo);
        seat.setSeatType(seatType);
        seat.setStatus(0); // 空闲
        seat.setTravelDate(travelDate);
        seat.setPrice(price);
        seatMapper.insert(seat);
    }
}
