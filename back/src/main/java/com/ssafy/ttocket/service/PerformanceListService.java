package com.ssafy.ttocket.service;

import com.ssafy.ttocket.domain.*;
import com.ssafy.ttocket.dto.PerformanceDto;
import com.ssafy.ttocket.dto.ResponseDto;
import com.ssafy.ttocket.repository.PerformanceLikeRepository;
import com.ssafy.ttocket.repository.PerformanceRepository;
import com.ssafy.ttocket.repository.SeatRepository;
import com.ssafy.ttocket.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class PerformanceListService {

    private final PerformanceRepository performanceRepository;
    private final SeatRepository seatRepository;
    private final PerformanceLikeRepository performanceLikeRepository;
    private final UserRepository userRepository;
    private final TimeService timeService;

    @Transactional
    public ResponseDto createPerformance(PerformanceDto performanceDto) {
        Map<String, Object> result = new HashMap<>();
        ResponseDto responseDto = new ResponseDto();
        // String -> Date
        String startTimeStr = performanceDto.getStartTime();
        String endTimeStr = performanceDto.getEndTime();
        LocalDateTime startTime = timeService.StringToLocalDateTime(startTimeStr);
        LocalDateTime endTime = timeService.StringToLocalDateTime(endTimeStr);
        Optional<User> byId = userRepository.findById(performanceDto.getUserId());
        if (byId.isEmpty()) {
            responseDto.setStatusCode(400);
            responseDto.setMessage("유저아이디 가입 안되어 있음");
            return responseDto;
        }
        User user = byId.get();
        // DB
        Performance performance = Performance.builder()
                .title(performanceDto.getTitle())
                .user(user)
                .startTime(startTime)
                .endTime(endTime)
                .location(performanceDto.getLocation())
                .max_seats(performanceDto.getMaxSeats())
                .description(performanceDto.getDesc())
                .poster(performanceDto.getPoster())
                .etc(performanceDto.getEtc())
                .price(performanceDto.getPrice())
                .build();
        performanceRepository.save(performance);

        // 빈 좌석 만들기
        int maxSeats = performanceDto.getMaxSeats();
        for (int i = 1; i <= maxSeats; i++) {
            Seat seat = Seat.builder()
                    .seatId(new SeatId(performance.getId(), i))
                    .seatNo(i)
                    .performance(performance)  // save할 때 performance를 리턴받아서 그걸 넣자
                    .status(SeatStatus.EMPTY)
                    .build();
            seatRepository.save(seat);
        }
        //공연까지 몇분 남았는지 계산
        LocalDateTime nowTime = LocalDateTime.now();
        Duration duration = Duration.between(nowTime,endTime);
        log.info("공연까지 남은 분 : ",duration.toMinutes());
        // responseDto
        result.put("performance_id", performance.getId());
        result.put("left_minute_perform",duration.toMinutes());
        responseDto.setMessage("공연 등록 완료");
        responseDto.setBody(result);
        responseDto.setStatusCode(200);
        return responseDto;
    }

    public ResponseDto homeList(String userId) {
        // 활용할 자료구조 생성
        Map<String,Object> result = new HashMap<>();
        ResponseDto responseDto = new ResponseDto();

        // DB에서 원하는 데이터 찾아오기
        List<Performance> openSoon = performanceRepository.findOpenSoon();  // 오픈 예정 : 상단 배너
        List<Performance> performSoon = performanceRepository.findPerformSoon();  // 공연 임박 리스트
        List<PerformanceLike> likePerform = performanceLikeRepository.findFirstListByUserId(userId);  // 유저가 좋아요 한 공연 리스트

        Optional<User> byId = userRepository.findById(userId);
        if(byId.isEmpty()){
            responseDto.setStatusCode(400);
            responseDto.setMessage("유저아이디 가입 안되어 있음");
            return responseDto;
        }
        User user = byId.get();
        String nickname = user.getNickname();

        List<PerformanceDto> likePerforms = new ArrayList<>();
        List<PerformanceDto> openSoonPerforms = new ArrayList<>();
        List<PerformanceDto> commingSoonPerforms = new ArrayList<>();

        for (PerformanceLike lp : likePerform) {
            PerformanceDto performanceDto = PerformanceDto.builder()
                    .title(lp.getPerformance().getTitle())
                    .location(lp.getPerformance().getLocation())
                    .price(lp.getPerformance().getPrice())
                    .desc(lp.getPerformance().getDescription())
                    .etc(lp.getPerformance().getEtc())
                    .poster(lp.getPerformance().getPoster())
                    .id(lp.getPerformance().getId())
                    .userId(lp.getPerformance().getUser().getId())
                    .startTime(String.valueOf(lp.getPerformance().getStartTime()))
                    .endTime(String.valueOf(lp.getPerformance().getEndTime()))
                    .maxSeats(lp.getPerformance().getMax_seats())
                    .build();
            likePerforms.add(performanceDto);
        }
        for (Performance p : openSoon) {
            PerformanceDto performanceDto = PerformanceDto.builder()
                    .title(p.getTitle())
                    .location(p.getLocation())
                    .price(p.getPrice())
                    .desc(p.getDescription())
                    .etc(p.getEtc())
                    .poster(p.getPoster())
                    .id(p.getId())
                    .userId(p.getUser().getId())
                    .startTime(String.valueOf(p.getStartTime()))
                    .endTime(String.valueOf(p.getEndTime()))
                    .maxSeats(p.getMax_seats())
                    .build();
            openSoonPerforms.add(performanceDto);
        }
        for (Performance p : performSoon) {
            PerformanceDto performanceDto = PerformanceDto.builder()
                    .title(p.getTitle())
                    .location(p.getLocation())
                    .price(p.getPrice())
                    .desc(p.getDescription())
                    .etc(p.getEtc())
                    .poster(p.getPoster())
                    .id(p.getId())
                    .userId(p.getUser().getId())
                    .startTime(String.valueOf(p.getStartTime()))
                    .endTime(String.valueOf(p.getEndTime()))
                    .maxSeats(p.getMax_seats())
                    .build();
            commingSoonPerforms.add(performanceDto);
        }
        // 찾은 데이터 result에 입력
        result.put("user_nickname", nickname);
        result.put("open_soon", openSoonPerforms);
        result.put("perform_soon", commingSoonPerforms);
        result.put("like_performance", likePerforms);

        responseDto.setMessage("홈 화면 데이터 리턴");
        responseDto.setBody(result);
        responseDto.setStatusCode(200);
        return responseDto;
    }

    public ResponseDto performanceList(int cursorId, int size) {
        Map<String, Object> result = new HashMap<>();
        ResponseDto responseDto = new ResponseDto();

        // 페이징
        Pageable pageable = PageRequest.of(cursorId, size);
        Page<Performance> performanceList = performanceRepository.findByCustom_cursorPaging(pageable, cursorId);
        List<PerformanceDto> performanceDtoList = new ArrayList<>();

        // DTO 변환
        for (Performance p : performanceList) {
            PerformanceDto performanceDto = PerformanceDto.builder()
                    .id(p.getId())
                    .userId(String.valueOf(p.getUser()))
                    .title(p.getTitle())
                    .startTime(String.valueOf(p.getStartTime()))
                    .endTime(String.valueOf(p.getEndTime()))
                    .location(p.getLocation())
                    .price(p.getPrice())
                    .maxSeats(p.getMax_seats())
                    .desc(p.getDescription())
                    .poster(p.getPoster())
                    .etc(p.getEtc())
                    .build();

            performanceDtoList.add(performanceDto);
        }

        // 반환 값 설정
        result.put("cursor_id", cursorId);
        result.put("performance_list", performanceDtoList);
        responseDto.setMessage("공연 목록 데이터 리턴");
        responseDto.setBody(result);
        responseDto.setStatusCode(200);
        return responseDto;
    }

    public ResponseDto userlikeList(String userId, int cursorId, int size) {
        Map<String, Object> result = new HashMap<>();
        ResponseDto responseDto = new ResponseDto();

        Pageable pageable = PageRequest.of(cursorId, size);
//        List<PerformanceLike> userlikeList = performanceLikeRepository.findByUserId(userId);
        Page<PerformanceLike> userlikePage = performanceLikeRepository.findByCustom_cursorPaging(pageable, cursorId, userId);
        List<PerformanceDto> userlikeDtoList = new ArrayList<>();

        for (PerformanceLike p : userlikePage) {
            PerformanceDto performanceDto = PerformanceDto.builder()
                    .id(p.getPerformance().getId())
                    .title(p.getPerformance().getTitle())
                    .startTime(String.valueOf(p.getPerformance().getStartTime()))
                    .location(p.getPerformance().getLocation())
                    .price(p.getPerformance().getPrice())
                    .endTime(String.valueOf(p.getPerformance().getEndTime()))
                    .etc(p.getPerformance().getEtc())
                    .poster(p.getPerformance().getPoster())
                    .desc(p.getPerformance().getDescription())
                    .maxSeats(p.getPerformance().getMax_seats())
                    .build();


            userlikeDtoList.add(performanceDto);
        }

        result.put("user_like_list", userlikeDtoList);
        responseDto.setMessage("유저의 찜 목록 데이터 리턴");
        responseDto.setBody(result);
        responseDto.setStatusCode(200);

        return responseDto;
    }

}
