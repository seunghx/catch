package com.cmatch.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;


/**
 * 
 * @author leeseunghyun
 *
 */
@EnableRedisHttpSession
public class SessionConfig extends AbstractHttpSessionApplicationInitializer {
     private final String redisPassword; 
     private final int redisPort; 
     private final String redisHost;
      
     public SessionConfig(@Value("${spring.redis.host}") String redisHost
                         , @Value("${spring.redis.port}") int redisPort
                         , @Value("${spring.redis.password}") String redisPassword){
          super(SessionConfig.class);
      
          this.redisHost = redisHost; 
          this.redisPort = redisPort; 
          this.redisPassword = redisPassword;
      }
      
      @Bean 
      public LettuceConnectionFactory connectionFactory() { 
          return new LettuceConnectionFactory(redisClusterConfiguration()); 
      }
      
      private RedisStandaloneConfiguration redisClusterConfiguration() {
          RedisStandaloneConfiguration redisClusterConfig = 
                                   new RedisStandaloneConfiguration(redisHost, redisPort);
          redisClusterConfig.setPassword(redisPassword()); 
          
          return redisClusterConfig; 
      }
      
      private RedisPassword redisPassword() { 
          RedisPassword password = RedisPassword.of(redisPassword); 
          return password; 
      }
}
