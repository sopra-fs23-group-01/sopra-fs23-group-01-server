// package ch.uzh.ifi.hase.soprafs23.controller;

// import org.springframework.context.annotation.Configuration;
// import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
// import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

// @Configuration
// @EnableWebSocketMessageBroker
// public class Chat implements WebSocketMessageBrokerConfigurer {
//   @Override
//   public void configureMessageBroker(MessageBrokerRegistry config) {
//     config.enableSimpleBroker("/topic");
//     config.setApplicationDestinationPrefixes("/app");
//   }

//   @Override
//   public void registerStompEndpoints(StompEndpointRegistry registry) {
//     registry.addEndpoint("/chat").setAllowedOrigins("*").withSockJS();
//   }
// }
