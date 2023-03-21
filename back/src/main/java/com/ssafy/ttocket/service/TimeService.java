package com.ssafy.ttocket.service;

import net.bytebuddy.asm.Advice;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class TimeService {

    public String LocalDateTimeToString (LocalDateTime localDateTime) {
        String strLocalDateTime = localDateTime.toString();
        return strLocalDateTime;
    }

    public LocalDateTime StringToLocalDateTime (String strTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(strTime, formatter);
        return localDateTime;
    }
}
