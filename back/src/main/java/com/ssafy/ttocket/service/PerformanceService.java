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
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
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
    static int seatRowNums = 8;
    public ArrayList<String> canceledSeatList = new ArrayList<>();

    public ResponseDto performanceDetail(String userId, int performanceId) {
        // 활용할 자료구조 생성
        Map<String,Object> result = new HashMap<>();
        ResponseDto responseDto = new ResponseDto();
        boolean isLike;

        // DB에서 원하는 데이터 찾아오기
        Performance performance = performanceRepository.findById(performanceId);
        PerformanceLike performanceLike = performanceLikeRepository.findByUserIdAndPerformanceId(userId, performanceId);

        // 공연 좋아요 체크
        if (performanceLike == null || performanceLike.isLike() == false) {
            isLike = false;
        } else {
            isLike = true;
        }

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
                .poster(performance.getPoster())
                .userId(performance.getUser().getId())
                .build();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = LocalDateTime.parse(performanceDto.getStartTime());
        LocalDateTime endTime = LocalDateTime.parse(performanceDto.getEndTime());

        Duration beforeDuration = Duration.between(startTime, now);
        Duration afterDuration = Duration.between(now, endTime);

        boolean canReserve;
        if (beforeDuration.compareTo(Duration.ZERO) > 0 && afterDuration.compareTo(Duration.ZERO) > 0) {
            canReserve = true;
        } else {
            canReserve = false;
        }

        // 찾은 데이터 result에 입력
        result.put("canReserve", canReserve);
        result.put("performance_dto", performanceDto);
        result.put("is_user_like", isLike);

        // response 형식에 맞게 메시지, result, 상태코드 리턴
        // 물론 중간중간 원하는 동작 안될시 데이터 넣지 말고 상태코드 다르게 해서 리턴
        responseDto.setMessage("티켓팅 상세보기 데이터 리턴");
        responseDto.setBody(result);
        responseDto.setStatusCode(200);
        return responseDto;
    }
    //==공연 좌석 만들고 시도==//
    public ResponseDto clickLike(String userId, int performanceId) {

        // 활용할 자료구조 생성
        boolean result;
        ResponseDto responseDto = new ResponseDto();

        // 데이터 있는 지 확인
        Optional<PerformanceLike> checkPerformanceLike = performanceLikeRepository.findByPerformanceIdAndUserId(performanceId, userId);
        Optional<User> user = userRepository.findById(userId);
        Performance performance = performanceRepository.findById(performanceId);

        if (checkPerformanceLike.isPresent()) {
            PerformanceLike performanceLike = checkPerformanceLike.get();
            boolean isLiked = performanceLike.isLike();
            performanceLike.setLike(!isLiked);
            performanceLikeRepository.save(performanceLike);
        }
        else {
            PerformanceLike performanceLike = new PerformanceLike();
            performanceLike.setUser(new User(user.get().getId(), user.get().getNickname()));
            performanceLike.setPerformance(performance);
            performanceLike.setLikeId(new LikeId(performanceId, userId));
            performanceLike.setLike(true);
            performanceLikeRepository.save(performanceLike);
        }

        result = performanceLikeRepository.findByPerformanceIdAndUserId(performanceId, userId).get().isLike();
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

        if(listOperations.size(key) == 0){
            List<Seat> seats = seatRepository.findByPerformanceId(performanceId);
            for(int i = 0; i < seats.size(); i++){
                listOperations.rightPush(key,String.valueOf(seats.get(i).getStatus()));
            }
        }
        List range = listOperations.range(key, 0, -1);

        int idx = 0;
        Long listSize = listOperations.size(key)/8;
        ArrayList<ArrayList> arrayList1 = new ArrayList<>();

        for (int i = 0; i < listSize+1; i++) {
            if (i == listSize) {
                if (listOperations.size(key) % seatRowNums != 0) {
                    ArrayList<String> arrayList2 = new ArrayList<>();
                    for (int j = 0; j < listOperations.size(key) % seatRowNums; j++) {
                        arrayList2.add((String) range.get(idx++));
                    }
                    arrayList1.add(arrayList2);
                }
            } else {
                ArrayList<String> arrayList2 = new ArrayList<>();
                for (int j = 0; j < seatRowNums; j++) {
                    arrayList2.add((String) range.get(idx++));
                }
                arrayList1.add(arrayList2);
            }
        }

        // 공연 정보 가져오기
        Performance perform = performanceRepository.findById(performanceId);
        log.debug("reservationState - performance : {}", perform.toString());

        // 찾은 데이터 result에 입력
        result.put("seats_state", arrayList1);
        result.put("perform", perform);

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
            for(int i=0; i<seats.size(); i++){
                listOperations.rightPush(key,String.valueOf(seats.get(i).getStatus()));
            }
        }

        // cf. if절 정리 필요 //
        if (code == 7) {
            listOperations.set(key,seatId - 1,String.valueOf(SeatStatus.EMPTY));
            result.put("isSuccess", true);
            responseDto.setMessage(performanceId+"번 공연 "+ seatId+ "번 좌석 EMPTY으로 변경 완료");
            responseDto.setStatusCode(200);

            return responseDto;
        }

        // code 2: 예약완료
        else if(code == 2){ // 좌석상태 RESERVED으로 변경
            listOperations.set(key,seatId - 1,String.valueOf(SeatStatus.RESERVED));
            result.put("isSuccess", true);
            responseDto.setMessage(performanceId+"번 공연 "+ seatId+ "번 좌석 RESERVED으로 변경 완료");
            responseDto.setStatusCode(200);
            return responseDto;
        }
        // code 3: PURCHASING (사용자가 자리 선택 시)
        if(code == 3){ // 예매 중으로 변경 <- {비어있음, 예매후 취소, 예매 중 취소}인 좌석 선택
            String status = (String) listOperations.index(key, seatId - 1);
            log.debug("changeReservationState - SeatStatus : {}", status);

            // {비어있음, 예매 중 취소}
            if(status.equals(String.valueOf(SeatStatus.EMPTY)) || status.equals(String.valueOf(SeatStatus.PURCHASING_CANCEL))){
                listOperations.set(key,seatId - 1,String.valueOf(SeatStatus.PURCHASING));
                result.put("isSuccess", true);
                result.put("beforeStatus","EMPTY");  // EMPTY & PURCHASING_CANCEL
                responseDto.setMessage(performanceId+"번 공연 "+ seatId+ "번 좌석 PURCHASING으로 변경 완료");
                responseDto.setStatusCode(200);

            }
            // {예매 후 취소}
            else if(status.equals(String.valueOf(SeatStatus.PURCHASED_CANCEL))){
                listOperations.set(key,seatId - 1,String.valueOf(SeatStatus.PURCHASING));
                result.put("isSuccess", true);
                result.put("beforeStatus","PURCHASED_CANCEL");
                responseDto.setMessage(performanceId+"번 공연 "+ seatId+ " 취소티켓 구매시도");
                responseDto.setStatusCode(200);
            }
            else{
                result.put("isSuccess", false);
                responseDto.setMessage("이미 선택된 좌석입니다.");
                responseDto.setStatusCode(400);
            }
            return responseDto;
        }
        // code 5: 구매완료 후 취소
        else if(code == 5){ // 좌석상태 CANCEL으로 변경
            canceledSeatList.add(Arrays.toString(new int[]{performanceId, seatId}));
            listOperations.set(key,seatId - 1,String.valueOf(SeatStatus.PURCHASED_CANCEL));
            result.put("isSuccess", true);
            responseDto.setMessage(performanceId+"번 공연 "+ seatId+ "번 좌석 CANCEL으로 변경 완료");
            responseDto.setStatusCode(200);

            // canceledSeatsList에 기록
            listOperations.rightPush("canceledSeatsList", performanceId+":"+seatId);
            log.debug("구매완료 후 취소, canceledSeatslist에 취소된 '공연::좌석' 등록");

            return responseDto;
        }
        responseDto.setMessage("code 확인해주세요");
        responseDto.setStatusCode(400);
        return responseDto;
    }

    public ResponseDto userCreatedList(String userId) {
        Map<String,Object> result = new HashMap<>();
        ResponseDto responseDto = new ResponseDto();
        List<Performance> userCreatedList = performanceRepository.findByCreatedUserId(userId);

        result.put("user_created_list", userCreatedList);
        responseDto.setBody(result);
        responseDto.setMessage("유저가 생성한 공연 목록");
        responseDto.setStatusCode(200);
        return responseDto;
    }
}
