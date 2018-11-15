package com.cmatch.support;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.ApplicationListener;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class DisconnectEventListener implements ApplicationListener<SessionSubscribeEvent>, ApplicationEventPublisherAware{

    private ApplicationEventPublisher eventPublisher;
    
    @Override
    public void onApplicationEvent(SessionSubscribeEvent event) {
        log.error("{}", event.getSource());
        log.error("{}", event.getMessage());
        log.error("{}", event.getUser());
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
}
