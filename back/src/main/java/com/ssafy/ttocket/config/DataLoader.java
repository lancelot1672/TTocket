package com.ssafy.ttocket.config;

import com.ssafy.ttocket.domain.Performance;
import com.ssafy.ttocket.domain.User;
import com.ssafy.ttocket.repository.PerformanceLikeRepository;
import com.ssafy.ttocket.repository.PerformanceRepository;
import com.ssafy.ttocket.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataLoader implements CommandLineRunner {

    private final PerformanceRepository performanceRepository;
    private final PerformanceLikeRepository performanceLikeRepository;
    private final UserRepository userRepository;

    public DataLoader(final PerformanceRepository performanceRepository,
                      final PerformanceLikeRepository performanceLikeRepository,
                      final UserRepository userRepository) {
        this.performanceRepository = performanceRepository;
        this.performanceLikeRepository = performanceLikeRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(final String... args) {

        User user = new User("바둑을 두면서", "그런 숨막히는 순간도");
        userRepository.save(user);

        Performance performance = Performance.builder()
                .title("처음엔 호기심이었고")
                .poster("한동안 안 보였을 땐 기다려졌고")
                .user(user)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now())
                .price(34000)
                .max_seats(10)
                .description("다시봤을 땐 이기고 싶었는데")
                .etc("주도권도 다 뺏기고 허둥거렸어")
                .location("그런 순간도 갖고 싶었어")
                .build();

        performanceRepository.save(performance);
    }
}
