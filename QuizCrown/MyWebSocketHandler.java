package com.example.quizSever21;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Component
public class MyWebSocketHandler extends TextWebSocketHandler
{
    // 매칭
    private final ObjectMapper objectMapper = new ObjectMapper();

    List<WebSocketSession> playerSessionList = new ArrayList<>();
    List<ConnectData> playerConnectDataList = new ArrayList<>();

    List<Room> EmptyRoomList = new ArrayList<>(), PlayRoomList = new ArrayList<>();

    // DB
    @Autowired
    private QuizRepository quizRepository;

    @Autowired QuizService quizService;

    // Player
    @Autowired
    private PlayerService playerService;


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception
    {
        // 접속한 플레이어 추가
        playerSessionList.add(session);

        // 클라이언트로 응답
        session.sendMessage(new TextMessage(ToJson("s_connect", "connect")));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception
    {
        playerSessionList.remove(session);
        playerConnectDataList.removeIf(connectData -> connectData.session.equals(session));

        var playerRoom = findPlayerRoom(session);

        if(playerRoom != null)
        {
            for(WebSocketSession player : playerRoom.players) player.close();

            PlayRoomList.remove(playerRoom);
        }
        else
        {
            for (Room room : EmptyRoomList)
            {
                if (room.players.contains(session)) playerRoom = room;
            }

            EmptyRoomList.remove(playerRoom);
        }
    }

    // 메시지 관리
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception
    {
        // TextMessage 객체를 string 형태로 가져오기
        String msg = message.getPayload();

        // 플레이어 소속 방 확인
        var playerRoom = findPlayerRoom(session);

        // 생성된 room 및 player 초기화
        if (msg.startsWith("clear"))
        {
            ClearRoom(session);
            return;
        }

        // json 형식 읽기
        var data = objectMapper.readValue(msg, Map.class);

        if(Objects.equals(data.get("type").toString(), "c_connect"))
        {
            ConnectPlayer(session, data.get("contents").toString());
        }

        // 매칭된 플레이어
        if(playerRoom != null)
        {
            if(Objects.equals(data.get("type").toString(), "sign")) // 신호인 경우
            {
                actSign(session, data.get("contents").toString()); // 내용에 따라 액션
            }
        }
    }

    void ConnectPlayer(WebSocketSession session, String contents) throws IOException
    {
        if(playerConnectDataList.stream().anyMatch(connectData -> connectData.session.equals(session)))
        {
            return;
        }

        // json 형식 읽기
        var data = objectMapper.readValue(contents, Map.class);

        var playerDTO = SearchPlayer(data.get("name").toString());
        var dd = data.get("dd").toString();

        if(playerDTO == null)
        {
            // 없는 플레이어 닉네임으로 접속
            return;
        }
        else
        {
            // 접속한 플레이어 추가
            playerConnectDataList.add(new ConnectData(dd, session, playerDTO));

            Room playerRoom = null;

            // 난이도가 동일한 방 찾기
            for(Room room : EmptyRoomList)
            {
                if(room.dd.equals(dd))
                {
                    playerRoom = room;
                }
            }

            // 방이 없을 경우 생성
            if(playerRoom == null)
            {
                playerRoom = new Room();
                playerRoom.dd = dd;
                EmptyRoomList.add(playerRoom);
            }

            // 플레이어를 방에 추가
            playerRoom.Connect(session, playerDTO);

            // 매칭된 경우, 두 명이 모인 경우
            if(playerRoom.Check())
            {
                // 예시로 QuizRepository를 통해 count 개의 Quiz 데이터를 가져온다고 가정
                // List<Quiz> quizList = quizRepository.findRandomQuizzes(5); // 예시 메서드
                // 예시로 QuizRepository를 통해 count 개의 Quiz 데이터를 가져온다고 가정
                //List<Quiz> quizList = quizRepository.findRandomQuizzesByDdValue(5, Integer.parseInt(playerRoom.dd)); // 예시 메서드

                List<QuizDTO> quizList = quizService.getRandomQuizByDD(5, Integer.parseInt(playerRoom.dd));


                playerRoom.Set(quizList, playerService);
                PlayRoomList.add(playerRoom);
                EmptyRoomList.remove(playerRoom);
            }
        }
    }

    void ClearRoom(WebSocketSession session) throws IOException
    {
        playerConnectDataList = new ArrayList<>();
        playerSessionList = new ArrayList<>();
        EmptyRoomList = new ArrayList<>();
        PlayRoomList = new ArrayList<>();

        session.sendMessage(new TextMessage("초기화"));
    }

    PlayerDTO SearchPlayer(String player) throws IOException
    {
        var playerDataList = playerService.findByName(player);
        PlayerDTO playerData = null;

        String jsonResponse = "None";

        if(playerDataList.isEmpty())
        {
            var responseData = Map.of(
                    "type", "sign",
                    "contents", "Can't Find Player Data >:["
            );
            jsonResponse = objectMapper.writeValueAsString(responseData);
        }
        else
        {
            playerData = playerDataList.getFirst();
            var responseData = Map.of(
                    "type", "sign",
                    "name", playerData.getName(),
                    "point", playerData.getNormalPoint(),
                    "lv", playerData.getLevelId()
            );
            jsonResponse = objectMapper.writeValueAsString(responseData);
        }

        // player 데이터 반환
        return  playerData;
    }

    Room findPlayerRoom(WebSocketSession session)
    {
        for (Room room : PlayRoomList) {
            if (room.players.contains(session)) { return room; }
        }
        return null; // session이 포함된 Room이 없을 경우 null 반환
    }

    void actSign(WebSocketSession playerSession, String contents) throws IOException
    {
        // 플레이어 방 찾기
        var room = findPlayerRoom(playerSession);

        switch (contents)
        {
            case "c_correct" : room.AnswerCorrect(playerSession); break; // 정답
            case "c_ready" : room.AnswerReady(playerSession); break; // 준비
            case "c_wrong" : room.AnswerWrong(playerSession); break; // 오답
            case "c_ignore" : room.AnswerIgnore(playerSession); break; // 미입력
        }
    }

    String ToJson(String type, String contents) throws JsonProcessingException
    {

        String jsonResponse = "None";

        // player 에게 신호 보내기
        // json 형태로 매핑
        Map<String, Object> responseData = Map.of(
                "type", type,
                "contents", contents
        );
        jsonResponse = objectMapper.writeValueAsString(responseData);

        return jsonResponse;
    }
    void SendMessage(WebSocketSession playerSession, String type, String contents) throws IOException
    {
        // 클라이언트로 응답
        playerSession.sendMessage(new TextMessage(ToJson(type, contents)));

    }
}
