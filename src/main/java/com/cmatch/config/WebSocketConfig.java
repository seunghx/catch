package com.cmatch.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.session.Session;
import org.springframework.session.web.socket.config.annotation.AbstractSessionWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

import com.cmatch.stage.StageUnsubscribeInterceptor;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractSessionWebSocketMessageBrokerConfigurer<Session> {

    @Value("${spring.rabbitmq.host}")
    private String rabbitHost;
    @Value("${spring.rabbitmq.port}")
    private int rabbitPort;
    @Value("${spring.rabbitmq.username}")
    private String rabbitUser;
    @Value("${spring.rabbitmq.password}")
    private String rabbitPass;
    
    /**
     * Spring STOMP support의 endpoint는 관습상 /topic, /queue를 사용하긴 하나 특별히 정해진 것은 없는 반면,
     * rabbitMQ의 STOMP support는 정해진 endpoint를 사용해야 한다. 
     * 
     * - ex) "/topic" : non-durable, auto-deleted exchange
     * 
     * rabbitmq로의 메세지와 stage로의 메세지를 분리하기 위해 아래 {@code config.enableSimpleBroker()}
     * 메서드 호출에서 stage로의 엔드포인트를 "/stage"와 같이 정의하였다.
     * 
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {

        config.enableSimpleBroker("/stage", "/queue");
        config.enableStompBrokerRelay("/topic").setRelayHost(rabbitHost)
                                               .setRelayPort(rabbitPort)
                                               .setClientLogin(rabbitUser)
                                               .setClientPasscode(rabbitPass);
        config.setApplicationDestinationPrefixes("/");

    }

    @Override
    protected void configureStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/connect").setAllowedOrigins("*").withSockJS();
    }
    
    /**
     * {@link StageUnsubscribeInterceptor}
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new StageUnsubscribeInterceptor());
    }

}