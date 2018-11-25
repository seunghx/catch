package com.cmatch.stage;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.broker.SimpleBrokerMessageHandler;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ExecutorSubscribableChannel;
import org.springframework.messaging.support.AbstractSubscribableChannel;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.messaging.support.MessageBuilder;

import com.cmatch.controller.AppController;
import com.cmatch.support.code.SubscriptionMode;

import lombok.extern.slf4j.Slf4j;


/**
 * 
 * InboundChannel에 등록될 것을 고려하여 작성된 {@link ChannelInterceptor} 구현.<br><br>
 * 
 * 현재 catch 어플리케이션에는 STAGE라는 개념이 존재한다. stage는 하나의 서버 노드 내부에서만 유일하며 
 * 클러스터 전체적으로 유일한 개념은 아니기 때문에 catch 어플리케이션에는 stage 전용 메세지 핸들러로 
 * {@link SimpleBrokerMessageHandler}를 사용하며 그 외 팔로우 관계가 성립된 사이에 진행하는 채팅은
 * RabbitMQ를 이용하여 클러스터 내의 모든 서버 노드에서 통신 가능하게 되어 있다. <br><br>
 * 
 * 나중에 보니 stage를 나갈 경우(stage unsubscribe) unsubscribe 메세지 처리 과정에서 오류가 발생하여 커넥션이 
 * 끊기는 문제가 발생하였다. 원인은 다음과 같다. 특정 목적지 엔드포인트를 인자로 전달하여 구독 메세지를 보내는 
 * {@code stomp.js}의 subscribe() 함수와 달리 unsubscribe() 함수의 경우 특정 엔드포인트로 전달되는 것이아니고
 * 기존 subscription id를 서버에 전달하는 방식으로 동작한다. <br><br>
 * 
 * 특정 목적지 정보가 없기 때문에 unsubscribe 메세지를 받은 {@link AbstractSubscribableChannel} 기본 구현 
 * 클래스인 (추상 클래스 상속이나 구현이라고 하였음) {@link ExecutorSubscribableChannel}은 s
 * {@link SimpleBrokerMessageHandler}뿐만아니라 stage와 관련 없는 {@link StompBrokerRelayMessageHandler}
 * 에도 해당 메세지를 전달한다. <br><br>
 * 
 * 메세지를 최종적으로 전달 받은 RabbitMQ는 존재않는 subscription id라며 에러 메세지를 반환하고 클라이언트와 서버간의 
 * 커넥션이 끊겨 버렸다. 물론 재연결이 가능하지만 이건 임시방편일 뿐이므로 아래와 같은 Inbound Channel에 적용될 
 * {@link ChannelInterceptor}를 구현하여 RabbitMQ로 unsubscription message가 전달되지 않도록 하였다.
 * 
 * 
 * @author leeseunghyun
 *
 */
@Slf4j
public class StageUnsubscribeInterceptor extends ChannelInterceptorAdapter {
    
    /**
     * stage 관련 unsubscription 메세지가 전달될 경우 이를 오직 {@link SimpleBrokerMessageHandler}에만
     * 전달한다.<br>
     * 
     * 또한 메서드의 반환 타입을 고려하여 임의로 만든 길이 0의 메세지를 생성하여 특정 엔드포인트({@link MessageMapping)
     * 로 전달하게 하였다.이 엔드 포인트로의 메세지를 처리하는 핸들러 메서드는 전달 받은 메세지를 어디에도 전달하지 않기 때문에 
     * 결국 메세지는 무시된다.
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();
                
        if(command == StompCommand.UNSUBSCRIBE) {
            
            String subscriptionId = accessor.getSubscriptionId();
            
            if(subscriptionId == null || 
                                !subscriptionId.startsWith(SubscriptionMode.STAGE.getCode())){
                log.debug("Received non stage related unsubscribe message. So just pssing this message to channel.");
                return message;
            }
            
            log.info("Received stage unsubscription message.");
            
            if(channel instanceof AbstractSubscribableChannel) {
                AbstractSubscribableChannel inboundChannel = (AbstractSubscribableChannel)channel;
                
                inboundChannel.getSubscribers()
                              .forEach(subscriber -> {
                                  if(subscriber instanceof SimpleBrokerMessageHandler) {
                                      log.debug("Passing stage unsubscribe message to {}.", subscriber);
                                      subscriber.handleMessage(message);
                                  }
                              });
                
                log.info("Stage unsubscribe message is processed successfully.");
                log.debug("Now, Passing newly created and ignorable message to channel.");
                
                StompHeaderAccessor newHeaderAccessor = 
                                                StompHeaderAccessor.create(StompCommand.MESSAGE);
                
                newHeaderAccessor.setSessionId(accessor.getSessionId());
                newHeaderAccessor.setSessionAttributes(accessor.getSessionAttributes());
                newHeaderAccessor.setDestination(AppController.IGNORABLE_MESSAGE_HANDLER_END_POINT);
                newHeaderAccessor.setContentLength(0);
                
                return MessageBuilder.createMessage(new byte[0]
                                                  , newHeaderAccessor.getMessageHeaders());
            }else {
                log.error("Unknown inbound message channel detected."
                        + "Checking version or channel configuration code required.");
                
                throw new IllegalStateException("Unknown inbound channel detected");
            }
        }else {
            return message;
        }
    }
}
