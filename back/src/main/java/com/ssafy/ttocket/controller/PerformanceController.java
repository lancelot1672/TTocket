package com.ssafy.ttocket.controller;

import com.ssafy.ttocket.dto.PerformanceDto;
import com.ssafy.ttocket.dto.ResponseDto;
import com.ssafy.ttocket.service.PerformanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.util.Optional;

@RestController
@RequestMapping("/performance")
@RequiredArgsConstructor
@Log4j2
public class PerformanceController {
    private final PerformanceService performanceService;


    @GetMapping("/home/{userId}") // 공연
    public ResponseEntity<ResponseDto> home(@PathVariable String userId){
        log.info("GET: /home/{userId}, userId:{}", userId);
        ResponseDto responseDto = performanceService.homeList(userId);
        return new ResponseEntity<ResponseDto>(responseDto, HttpStatus.OK);
    }

    @PostMapping("/create") // 파티 만들기
    public ResponseEntity<ResponseDto> performanceCreate(@RequestBody PerformanceDto performanceDto) {
        log.info("POST: /create");
        ResponseDto responseDto = performanceService.createPerformance(performanceDto);
        return new ResponseEntity<ResponseDto>(responseDto, HttpStatus.OK);
    }

    @GetMapping(value = {"/list/{cursorId}", "/list"})  // 전체 공연 목록
    public ResponseEntity<ResponseDto> performanceList(@PathVariable(required = false) Integer cursorId) {
        log.info("GET: /list/cursorId, cursorId:{}", cursorId);
        if (cursorId == null) {
            cursorId = 0;
        }
        ResponseDto responseDto = performanceService.performanceList(cursorId, 6);  // size 설정
        return new ResponseEntity<ResponseDto>(responseDto, HttpStatus.OK);
    }

    @GetMapping(value = {"/likelist/{userId}/{cursorId}", "/likelist/{userId}"})  // 찜 목록 전체 보기 (cursorId 아직 미적용)
    public ResponseEntity<ResponseDto> userlikeList(@PathVariable String userId, @PathVariable(required = false) Integer cursorId) {
        log.info("GET: /likelist/{userId}/{curosrId}, userId: {}, cursorId: {}", userId, cursorId);
        if (cursorId == null) {
            cursorId  = 0;
        }
        ResponseDto responseDto = performanceService.userlikeList(userId, cursorId, 6);  // userId 매서드 나중에 입력하기
        return new ResponseEntity<ResponseDto>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/{userId}/{performanceId}")  // 공연 설명 상세보기
    public ResponseEntity<ResponseDto> performanceDetail(@PathVariable String userId, @PathVariable Integer performanceId) {
        log.info("GET: /{userId}/{performanceId}, userId:{}, performanceId:{}", userId, performanceId);
        ResponseDto responseDto = performanceService.performanceDetail(userId, performanceId);
        return new ResponseEntity<ResponseDto>(responseDto, HttpStatus.OK);
    }

    @PutMapping("/like/{userId}/{performanceId}")  // 좋아요 클릭
    public ResponseEntity<ResponseDto> clickLike(@PathVariable String userId, @PathVariable int performanceId) {
        log.info("PUT: /like/{userId}/{performanceId}, userId:{}, performanceId:{}", userId, performanceId);
        ResponseDto responseDto = performanceService.clickLike(userId, performanceId);
        return new ResponseEntity<ResponseDto>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/reserve/{performanceId}")  // 공연 상세보기에서 예매하기 버튼 클릭
    public ResponseEntity<ResponseDto> performanceReservation(@PathVariable int performanceId) {
        log.info("GET: /reserve/{performanceId}, performanceId:{}", performanceId);
        ResponseDto responseDto = performanceService.reservationState(performanceId);
        return new ResponseEntity<ResponseDto>(responseDto, HttpStatus.OK);
    }

    @PutMapping("/{performanceId}/{seatId}/{code}")  // 좌석 상태 변경
    public ResponseEntity<ResponseDto> performanceReservation(@PathVariable int performanceId,
                                                              @PathVariable int seatId,
                                                              @PathVariable int code) {
        log.info("PUT: /{performance}/{seatId}/{code}, performance: {}, seatId:{}, code:{}", performanceId, seatId, code);
        ResponseDto responseDto = performanceService.changeReservationState(performanceId, seatId, code);
        return new ResponseEntity<ResponseDto>(responseDto, HttpStatus.OK);
    }

}
