package com.ssafy.tttocket.controller;

import com.ssafy.tttocket.dto.WaitQueEnterDto;
import com.ssafy.tttocket.service.TicketingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.*;

@Controller
@RequiredArgsConstructor
@Slf4j
public class TicketingController {
    private final SimpMessagingTemplate template; //특정 Broker로 메세지를 전달
    private final SimpMessageSendingOperations sendingOperations;
    private final TicketingService ticketingService;
    private final RedisTemplate redisTemplate;
    private static final int popAmount = 1;

    @MessageMapping(value = "/chat/enter")
    public void enter(@RequestBody WaitQueEnterDto waitQueEnterDto){
        log.info("유저 대기열 큐 입장 "+"userId : "+waitQueEnterDto.getUserId() +",  performId : "+ waitQueEnterDto.getPerformId());
        // 해당 유저를 해당 공연 대기큐에 추가
        Map<String, Object> returnData = ticketingService.addToWaitQue(waitQueEnterDto.getUserId(), waitQueEnterDto.getPerformId());
        sendingOperations.convertAndSend("/sub/chat/perform/"+waitQueEnterDto.getPerformId() ,returnData);
    }

    @Scheduled(fixedRate = 1000)
    public void QuePoll(){
        log.info("공연 대기열 관리 스케줄링 시작");
        Set<String> redisKeys = redisTemplate.keys("WaitQue*");
        Iterator<String> it = redisKeys.iterator();
        while (it.hasNext()) {
            String key = it.next();
            log.info("now key :"+ key);
            if(redisTemplate.opsForList().size(key) <= popAmount){
                int performId = Integer.parseInt(key.split("::")[1]);
                Map<String,Object> result = new HashMap<>();
                result.put("isMyTurn",true);
                result.put("myOrder",0);
                sendingOperations.convertAndSend("/sub/chat/perform/"+performId ,result);
                while(redisTemplate.opsForList().size(key) > 0){
                    redisTemplate.opsForList().leftPop(key);
                }
                log.info("공연 대기열 관리 스케줄링 종료");
                return;
            }
            List waitQue = redisTemplate.opsForList().range(key, 0, -1);
            int idx = 0;
            for (Object o : waitQue) {
                log.info("Object o.toString : {}", o.toString());
                if(idx >= popAmount){ //이번 차례 아닌 놈들
                    Map<String,Object> result = new HashMap<>();
                    result.put("isMyTurn",false);
                    result.put("myOrder",idx-popAmount);
                    result.put("que_size",redisTemplate.opsForList().size(key));
                    sendingOperations.convertAndSend("/sub/id/" + o.toString() ,result);
                }
                else{ //이번 차례인놈들!
                    Map<String,Object> result = new HashMap<>();
                    result.put("isMyTurn",true);
                    result.put("myOrder",0);
                    redisTemplate.opsForList().leftPop(key);
                    sendingOperations.convertAndSend("/sub/id/" + o.toString() ,result);
                }
                idx++;
            }
        }
        log.info("공연 대기열 관리 스케줄링 종료");
    }
}
