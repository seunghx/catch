package com.cmatch.support;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WebsocketConnectedListener implements ApplicationListener<SessionConnectedEvent> {

    // Methods
    // ==========================================================================================================================
    
    @Override
    public void onApplicationEvent(SessionConnectedEvent event) {
        if(log.isInfoEnabled()) {
            
            log.info("New session connected. Message : {}.", event.getMessage());

            if(event.getUser() != null) {
                log.info("Connected user : {} ", event.getUser().getName());
            }else {
                log.error("Unknown connected user detected."
                        + "It might be security or precending interceptor problem.");
                
                throw new IllegalStateException("Unknown connected user detected.");
            }
        }
    }
}
