package com.ssafy.ttocket.service;

import com.querydsl.core.types.dsl.EnumPath;
import com.ssafy.ttocket.domain.Seat;
import com.ssafy.ttocket.domain.SeatStatus;
import com.ssafy.ttocket.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
@Service
@Slf4j
@RequiredArgsConstructor
public class SchedulerService {

    private final RedisTemplate redisTemplate;
    private final SeatRepository seatRepository;

    @Scheduled(fixedRate = 180000)
    @Transactional
    public void changeSeatsStatus() {
        log.debug("매 3분마다 스케줄러 실행");
        ListOperations listOperations = redisTemplate.opsForList();
        // 람다식 적용 방법 찾아서 적용하기
        Set<String> keys = redisTemplate.keys("seatStatus::*");
        for (String key : keys) {
            String keyIdx = key.substring(12);  // performanceId 추출
            if (!redisTemplate.type(key).equals(DataType.NONE)) {
                List<Seat> targetSeats = seatRepository.findByPerformanceId(Integer.parseInt(keyIdx));  // 좌석 정보를 가져오기 위해 repository 탐색
                List<String> list = listOperations.range("seatStatus::" + keyIdx, 0, -1);  // 레디스

                // 아래 조건에 걸리지 않는 경우 탐색 필요 -> 개발과정에서 mysql 개별 조작 가능성
                if (list.size() == targetSeats.size()) {
                    for (int i = 0; i < list.size(); i++) {
                        SeatStatus s = SeatStatus.valueOf(list.get(i));
                        targetSeats.get(i).setStatus(s);
                        seatRepository.save(targetSeats.get(i));
                    }
                }
            } else {
                log.debug("리스트 타입이 아님");
            }
        }
    }
}
