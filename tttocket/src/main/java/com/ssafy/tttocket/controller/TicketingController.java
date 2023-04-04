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
    private static final int popAmount = 2;

    @MessageMapping(value = "/chat/enter")
    public void enter(@RequestBody WaitQueEnterDto waitQueEnterDto){
        log.info("userId : "+waitQueEnterDto.getUserId() +",  performId : "+ waitQueEnterDto.getPerformId());
        // 해당 유저를 해당 공연 대기큐에 추가
        Map<String, Object> returnData = ticketingService.addToWaitQue(waitQueEnterDto.getUserId(), waitQueEnterDto.getPerformId());
        sendingOperations.convertAndSend("/sub/chat/perform/"+waitQueEnterDto.getPerformId() ,returnData);
    }

    @Scheduled(fixedRate = 30000)
    public void QuePoll(){
        log.info("앙 실행");
        Set<String> redisKeys = redisTemplate.keys("WaitQue*");
        Iterator<String> it = redisKeys.iterator();
        while (it.hasNext()) {
            String key = it.next();
            List waitQue = redisTemplate.opsForList().range(key, 0, -1);
            int idx = 0;
            for (Object o : waitQue) {
                log.info("Object o.toString : {}", o.toString());
                if(idx >= popAmount){ //이번 차례 아닌 놈들
                    Map<String,Object> result = new HashMap<>();
                    result.put("isMyTurn",false);
                    result.put("myOrder",idx-9);
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
    }
}
