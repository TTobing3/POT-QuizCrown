package com.example.quizSever21;

import lombok.*;
import org.springframework.web.socket.WebSocketSession;

@ToString
@Builder
@Getter
@Setter
@NoArgsConstructor
public class ConnectData
{
    public String dd;
    public WebSocketSession session;
    public PlayerDTO playerDTO;

    public ConnectData(String dd, WebSocketSession session, PlayerDTO playerDTO) {
        this.dd = dd;
        this.session = session;
        this.playerDTO = playerDTO;
    }
}
