package com.example.quizSever21;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer
{
    private final MyWebSocketHandler myWebSocketHandler;

    public WebSocketConfig(MyWebSocketHandler myWebSocketHandler) {
        this.myWebSocketHandler = myWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry)
    {
        registry.addHandler(myWebSocketHandler, "/chat")// "/chat" 경로로 웹소켓 핸들러를 등록
                .setAllowedOrigins("*");  // CORS 허용 설정, 모든 오리진에서의 접근을 허용
    }
}
