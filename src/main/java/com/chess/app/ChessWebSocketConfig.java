package com.chess.app;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class ChessWebSocketConfig implements WebSocketMessageBrokerConfigurer {

	/**
	 * Configurar message broker
	 */
	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.enableSimpleBroker("/topic");
		registry.setApplicationDestinationPrefixes("/appChessEndpoint");
		WebSocketMessageBrokerConfigurer.super.configureMessageBroker(registry);
	}
	
	/**
	 * registrar guia endpoint
	 */
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/gs-guide-websocket");
		WebSocketMessageBrokerConfigurer.super.registerStompEndpoints(registry);
	}
	
}
