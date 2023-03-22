package com.ssafy.ttocket.service;

import com.ssafy.ttocket.domain.*;
import com.ssafy.ttocket.dto.PerformanceDto;
import com.ssafy.ttocket.dto.ResponseDto;
import com.ssafy.ttocket.dto.UserlikeDto;
import com.ssafy.ttocket.repository.PerformanceLikeRepository;
import com.ssafy.ttocket.repository.PerformanceRepository;
import com.ssafy.ttocket.repository.SeatRepository;
import com.ssafy.ttocket.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class PerformanceService {

    private final PerformanceRepository performanceRepository;
    private final SeatRepository seatRepository;
    private final PerformanceLikeRepository performanceLikeRepository;
    private final UserRepository userRepository;
    private final TimeService timeService;
    private final RedisTemplate redisTemplate;
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
        
        // 사용자 수만큼 performanceLike DB 등록
        List<User> userAll = userRepository.findAll();
        for (User anUser : userAll) {
            LikeId likeId = new LikeId(performance.getId(), anUser.getId());

            PerformanceLike performanceLike = PerformanceLike.builder()
                    .likeId(likeId)
                    .performance(performance)
                    .user(anUser)
                    .isLike(false)
                    .build();

            performanceLikeRepository.save(performanceLike);
        }

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

        // responseDto
        result.put("performance_id", performance.getId());
        responseDto.setMessage("공연 등록 완료");
        responseDto.setBody(result);
        responseDto.setStatusCode(200);

        return responseDto;

    }

    public ResponseDto homeList(String userId) {
        // 활용할 자료구조 생성
        Map<String,Object> result = new HashMap<>();
        ResponseDto responseDto = new ResponseDto();
//        ValueOperations valueOperations = redisTemplate.opsForValue();
//        String key = "test";
//        valueOperations.set(key,"is the test");

        // DB에서 원하는 데이터 찾아오기
        List<Performance> openSoon = performanceRepository.findOpenSoon();  // 오픈 예정 : 상단 배너
        List<Performance> performSoon = performanceRepository.findPerformSoon();  // 공연 임박 리스트
        List<PerformanceLike> likePerform = performanceLikeRepository.findFirstListByUserId(userId);  // 유저가 좋아요 한 공연 리스트

        // 찾은 데이터 result에 입력
        result.put("open_soon", openSoon);
        result.put("perform_soon", performSoon);
        result.put("like_performance", likePerform);

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
        result.put("peformance_list", performanceDtoList);
        responseDto.setMessage("공연 목록 데이터 리턴");
        responseDto.setBody(result);
        responseDto.setStatusCode(200);
        return responseDto;
    }

    public ResponseDto userlikeList(String userId, int cursorId, int size) {
        Map<String, Object> result = new HashMap<>();
        ResponseDto responseDto = new ResponseDto();

        Pageable pageable = PageRequest.of(cursorId, size);
        Page<PerformanceLike> userlikeList = performanceLikeRepository.findByCustom_cursorPaging(pageable, cursorId);
        List<UserlikeDto> userlikeDtoList = new ArrayList<>();

        for (PerformanceLike p : userlikeList) {
            UserlikeDto userlikeDto = UserlikeDto.builder()
                    .id(userId)
                    .title(p.getPerformance().getTitle())
                    .startTime(String.valueOf(p.getPerformance().getStartTime()))
                    .endTime(String.valueOf(p.getPerformance().getEndTime()))
                    .description(p.getPerformance().getDescription())
                    .etc(p.getPerformance().getEtc())
                    .build();

            userlikeDtoList.add(userlikeDto);
        }

        result.put("user_like_list", userlikeDtoList);
        responseDto.setMessage("유저의 찜 목록 데이터 리턴");
        responseDto.setBody(result);
        responseDto.setStatusCode(200);

        return responseDto;
    }

    public ResponseDto performanceDetail(String userId, int performanceId) {
        // 활용할 자료구조 생성
        Map<String,Object> result = new HashMap<>();
        ResponseDto responseDto = new ResponseDto();

        // DB에서 원하는 데이터 찾아오기
        Performance performance = performanceRepository.findById(performanceId);
        PerformanceLike performanceLike = performanceLikeRepository.findByUserIdAndPerformanceId(userId, performanceId);
        List<Seat> seats = seatRepository.findByPerformanceId(performanceId);

        PerformanceDto performanceDto = PerformanceDto.builder()
                .id(performance.getId())
                .title(performance.getTitle())
                .startTime(timeService.LocalDateTimeToString(performance.getStartTime()))
                .endTime(timeService.LocalDateTimeToString(performance.getEndTime()))
                .location(performance.getLocation())
                .price(performance.getPrice())
                .maxSeats(performance.getMax_seats())
                .desc(performance.getDescription())
                .etc(performance.getEtc())
                .build();

        String[] seatsState = new String[performance.getMax_seats()+1];
        seatsState[0] = "Index Starts from 1";
        for (Seat seat : seats) {
            int seatNo = seat.getSeatId().getSeatNo();
            seatsState[seatNo] = String.valueOf(seat.getStatus());
        }

        // 찾은 데이터 result에 입력
        result.put("performance_dto", performanceDto);
        result.put("is_user_like", performanceLike.isLike());
        result.put("seats_state", seatsState);

        // response 형식에 맞게 메시지, result, 상태코드 리턴
        // 물론 중간중간 원하는 동작 안될시 데이터 넣지 말고 상태코드 다르게 해서 리턴
        responseDto.setMessage("티켓팅 상세보기 데이터 리턴");
        responseDto.setBody(result);
        responseDto.setStatusCode(200);
        return responseDto;
    }


    //==공연 좌석 만들고 시도==//
//    @Transactional  // 확신이 없으니깐 공부해보고 필요할 때 사용
    public ResponseDto clickLike(String userId, int performanceId) {
        // 활용할 자료구조 생성
        boolean result;
        ResponseDto responseDto = new ResponseDto();

        // 데이터 있는 지 확인
        PerformanceLike checkPerformanceLike = performanceLikeRepository.findByPerformanceIdAndUserId(performanceId, userId);
        if (checkPerformanceLike.isLike() == false) {
            checkPerformanceLike.setLike(true);
        } else {
            checkPerformanceLike.setLike(false);
        }

        performanceLikeRepository.save(checkPerformanceLike);

        result = checkPerformanceLike.isLike();
        responseDto.setMessage("공연 좋아요 데이터 리턴");
        responseDto.setBody(result);
        responseDto.setStatusCode(200);
        return responseDto;
    }

    public ResponseDto reservationState(int performanceId) {
        Map<String,Object> result = new HashMap<>();
        ResponseDto responseDto = new ResponseDto();
        String key = "seatStatus::" + performanceId;
        ListOperations listOperations = redisTemplate.opsForList();

        Performance performance = performanceRepository.findById(performanceId);

        if(listOperations.size(key) == 0){
            List<Seat> seats = seatRepository.findByPerformanceId(performanceId);
            for(int i=0;i<seats.size();i++){
                listOperations.rightPush(key,String.valueOf(seats.get(i).getStatus()));
            }
        }
        List range = listOperations.range(key, 0, -1);

        // 찾은 데이터 result에 입력
        result.put("seats_state", range);

        responseDto.setMessage("공연 좌석보기 데이터 리턴");
        responseDto.setBody(result);
        responseDto.setStatusCode(200);
        return responseDto;
    }

    public ResponseDto changeReservationState(int performanceId, int seatId, int code) {
        Map<String,Object> result = new HashMap<>();
        ResponseDto responseDto = new ResponseDto();

        String key = "seatStatus::" + performanceId;
        ListOperations listOperations = redisTemplate.opsForList();
        if(listOperations.size(key) == 0){
            List<Seat> seats = seatRepository.findByPerformanceId(performanceId);
            for(int i=0;i<seats.size();i++){
                listOperations.rightPush(key,String.valueOf(seats.get(i).getStatus()));
            }
        }

        if(code == 3){ // 좌석상태 PURCHASING으로 변경
            String status = (String) listOperations.index(key, seatId - 1);
            if(status.equals(String.valueOf(SeatStatus.EMPTY)) || status.equals(SeatStatus.PURCHASING_CANCEL)){
                listOperations.set(key,seatId - 1,String.valueOf(SeatStatus.PURCHASING));
                result.put("isSuccess", true);
                result.put("beforeStatus","EMPTY");
                responseDto.setMessage(performanceId+"번 공연 "+ seatId+ "번 좌석 PURCHASING으로 변경 완료");
                responseDto.setStatusCode(200);
            }
            else if(status.equals(SeatStatus.PURCHASED_CANCEL)){
                result.put("isSuccess", true);
                result.put("beforeStatus","CANCEL");
                responseDto.setMessage(performanceId+"번 공연 "+ seatId+ " 취소티켓 구매시도");
            }
            else{
                result.put("isSuccess", false);
                responseDto.setMessage("이미 선택된 좌석입니다.");
                responseDto.setStatusCode(400);
            }
            return responseDto;
        }
        else if(code == 2){ // 좌석상태 RESERVED으로 변경
            listOperations.set(key,seatId - 1,String.valueOf(SeatStatus.RESERVED));
            result.put("isSuccess", true);
            responseDto.setMessage(performanceId+"번 공연 "+ seatId+ "번 좌석 RESERVED으로 변경 완료");
            responseDto.setStatusCode(200);
            return responseDto;
        }
        else if(code == 5){ // 좌석상태 CANCEL으로 변경
            listOperations.set(key,seatId - 1,String.valueOf(SeatStatus.PURCHASED_CANCEL));
            result.put("isSuccess", true);
            responseDto.setMessage(performanceId+"번 공연 "+ seatId+ "번 좌석 CANCEL으로 변경 완료");
            responseDto.setStatusCode(200);
            return responseDto;
        }
        responseDto.setMessage("code 확인해주세요");
        responseDto.setStatusCode(400);
        return responseDto;
    }
}
