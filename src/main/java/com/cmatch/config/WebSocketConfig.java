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

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new StageUnsubscribeInterceptor());
    }

}