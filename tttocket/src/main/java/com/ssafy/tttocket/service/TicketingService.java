package com.ssafy.tttocket.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
public class TicketingService {
    private final RedisTemplate redisTemplate;
    public Map<String,Object> addToWaitQue(String userId, int performId){
        Map<String,Object> result = new HashMap<>();
        String key = "WaitQue::"+performId;
        log.info("add key :"+ key);

        ListOperations<String, Object> listOperations = redisTemplate.opsForList();
        listOperations.rightPush(key,userId); // 넣을때는 rightPush 뺄때는 leftPop
        Long size = listOperations.size(key);
        result.put("que_size",size);

        return result;
    }
}
