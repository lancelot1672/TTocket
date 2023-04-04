package com.ssafy.tttocket.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker  //stomp 사용하겠다
public class StompWebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ticket") //이주소로 SockJS 연결 ex)var sock = new SockJS("/ticket");
                //에서 새로운 핸드쉐이크 커넥션을 생성할 때 사용됨.
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    /*어플리케이션 내부에서 사용할 path를 지정할 수 있음*/
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/pub"); //클라이언트가 메시지를 보낼 때 경로 맨앞에 "/pub"이 붙어있으면 Broker로 보내짐.
        registry.enableSimpleBroker("/sub", "/user"); //해당경로 브로커 등록, 이 경로 SUB하는 클라이언트에게 메시지 전달
        registry.setUserDestinationPrefix("/user");
    }
}


