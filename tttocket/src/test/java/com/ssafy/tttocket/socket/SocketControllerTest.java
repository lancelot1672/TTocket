package com.ssafy.tttocket.socket;

import com.ssafy.tttocket.dto.WaitQueEnterDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@ActiveProfiles("local")
public class SocketControllerTest {
    final String TARGET_URI = "http://localhost:8081/wait/ticket";
    final String SENDMESSAGE_URI = "/pub/chat/enter";
    WebSocketStompClient stompClient;
    ArrayList<StompSession> socketSessions;

    CountDownLatch lock = new CountDownLatch(5000);
    private List<Transport> createTransportClient(){
        List<Transport> transports = new ArrayList<>();
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        return transports;
    }

    @BeforeEach
    public void setup() throws InterruptedException{
        stompClient = new WebSocketStompClient(new SockJsClient(createTransportClient()));

        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        socketSessions = new ArrayList<>();
    }

    @Test
    public void contextLoad() throws InterruptedException {
        StompSession stompSession = null;

        for(int i=0; i<3000; i++){
            try{
                WebSocketHttpHeaders httpHeaders = new WebSocketHttpHeaders();
                httpHeaders.add("jwt" , "test");
                StompHeaders stompHeaders = new StompHeaders();
                stompSession = stompClient.connect(TARGET_URI, httpHeaders, stompHeaders, new StompSessionHandlerAdapter() {
                }).get(1, TimeUnit.SECONDS);

                // Send
                //stompSession.send(SENDMESSAGE_URI, new WaitQueEnterDto("t",1));
                socketSessions.add(stompSession);

            }catch(Exception e){
                e.printStackTrace();
            }
        }

        for(int i=0; i<socketSessions.size(); i++){
            Thread t = new Thread(new ThreadTest(i));
            t.start();
        }
        lock.await();
    }
    class ThreadTest implements Runnable {
        int index;
        ThreadTest(int i){
            this.index = i;
        }

        @Override
        public void run(){
            System.out.println("run 들어옴 : " + index);

            //
            socketSessions.get(index).send(SENDMESSAGE_URI, new WaitQueEnterDto("1",15));
            while(true){

            }
        }
    }
}

