package com.example.quizSever21;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import java.util.Map;

@Component
public class Room
{
    PlayerService playerService;

    final ObjectMapper objectMapper = new ObjectMapper();

    // 방 정보 목록
    List<WebSocketSession> players = new ArrayList<>();
    PlayerDTO[] playerDatas = new PlayerDTO[2];

    // 두 플레이어의 입력 여부 체크
    boolean[] answers = new boolean[]{false, false};
    boolean[] readies = new boolean[]{false, false};
    boolean bothReady = false;

    int[] points = new int[]{0,0};
    int quizCount = 0;

    String dd = "";

    // 방이 찼는지 확인
    public boolean Check()
    {
        return players.size() == 2;
    }

    // 방 생성 시 세팅
    public void Set(List<QuizDTO> quizList, PlayerService playerService) throws IOException
    {
        this.playerService = playerService;

        // Quiz 리스트를 JSON 문자열로 변환
        String quizListJson = objectMapper.writeValueAsString(quizList);

        for (WebSocketSession player : players)
        {
            player.sendMessage(new TextMessage(ToJson("quiz", quizListJson)));
            SendPlayerDataBothPlayer(player);
        }
    }

    //
    public void Connect(WebSocketSession session, PlayerDTO playerDTO) throws IOException
    {
        players.add(session);

        var index = players.indexOf(session);
        playerDatas[index] = playerDTO;

    }

    // 0. 준비
    public void AnswerReady(WebSocketSession playerSession) throws IOException
    {
        if(bothReady) return;

        // 응답 여부 기록
        int index = players.indexOf(playerSession);
        readies[index] = true;

        // 둘 다 결과 입력했는지 확인
        CheckForNextQuestion();
    }

    // 1. 정답 입력
    public void AnswerCorrect(WebSocketSession playerSession) throws IOException
    {
        if(!bothReady) return;

        // 응답 여부 기록
        int index = players.indexOf(playerSession);
        answers[index] = true;

        // 데이터 보냄
        for(int i = 0; i<players.size(); i++)
        {
            if(i == index)
            {
                answers[i] = true;
                points[i] += 1;
                SendSign(players.get(i), "s_correct");// 플레이어에게 정답 신호 보내기
            }
            else if(!answers[i])
            {
                answers[i] = true;
                SendSign(players.get(i), "s_late");// 반대 플레이어에게 늦음 신호 보내기
            }
        }

        // 둘 다 결과 입력했는지 확인
        CheckSignByBothPlayer();
    }

    // 2. 오답 입력
    public void AnswerWrong(WebSocketSession playerSession) throws IOException
    {
        if(!bothReady) return;

        // 응답 여부 기록
        int index = players.indexOf(playerSession);
        answers[index] = true;

        // 데이터 보냄
        SendSign(playerSession, "s_wrong");

        // 둘 다 결과 입력했는지 확인
        CheckSignByBothPlayer();
    }

    // 3. 입력 안 함
    public void AnswerIgnore(WebSocketSession playerSession) throws IOException
    {
        if(!bothReady) return;

        // 응답 여부 기록
        int index = players.indexOf(playerSession);
        answers[index] = true;

        // 데이터 보냄
        SendSign(playerSession, "s_ignore");

        // 둘 다 결과 입력했는지 확인
        CheckSignByBothPlayer();
    }

    // 둘 다 입력한 경우 both 신호
    void CheckSignByBothPlayer() throws IOException
    {
        if(answers[0] && answers[1])
        {
            bothReady = false;

            answers[0] = false;
            answers[1] = false;

            quizCount += 1;

            for(WebSocketSession playerSession : players)
                playerSession.sendMessage(new TextMessage(ToJson("sign", "s_both")));
        }
    }

    // 준비된 경우 next 신호
    void CheckForNextQuestion() throws IOException
    {
        if(readies[0] && readies[1])
        {
            bothReady = true;

            readies[0] = false;
            readies[1] = false;

            quizCount += 1;

            for(WebSocketSession playerSession : players)
            {
                var contents = "s_next";

                if(quizCount == 1 )
                {
                    contents = "s_start";
                    playerSession.sendMessage(new TextMessage(ToJson("sign", contents)));
                }
                else if(quizCount > 10 ) // 문제수x2
                {
                    contents = "s_finish";
                    playerSession.sendMessage(new TextMessage(ToJson("sign", contents)));

                    Finish(playerSession);
                }
                else
                {
                    playerSession.sendMessage(new TextMessage(ToJson("sign", contents)));
                }
            }
        }
    }

    void Finish(WebSocketSession playerSession) throws IOException
    {
        int index = players.indexOf(playerSession);
        String result = "draw";
        String[] score = new String[2];

        int amount = 0;

        if(points[index] == points[1-index])
        {
            result = "draw";
            score[index] = "+0";
            score[1-index] = "-0";
        }
        else if(points[index] > points[1-index])
        {
            result = "win";
            score[index] = "+"+dd;
            score[1-index] = "-"+dd;

            playerService.save(playerDatas[index]);
        }
        else if(points[index] < points[1-index])
        {
            result = "lose";
            score[index] = "-"+dd;
            score[1-index] = "+"+dd;

            playerService.save(playerDatas[index]);
        }

        playerDatas[index].setNormalPoint( playerDatas[index].getNormalPoint() + amount  );

        playerDatas[index].setLevelId(
                playerService.getLevelFromPoint( playerDatas[index].getNormalPoint() )
        );
        playerService.save(playerDatas[index]);


        var responseData = Map.of(
                "playerScore", score[index],
                "opponentScore", score[1-index],
                "result", result
        );

        var resultResponse = objectMapper.writeValueAsString(responseData);

        playerSession.sendMessage(new TextMessage(ToJson("result", resultResponse)));
    }

    // 두 플레이어에게 플레이어 둘의 정보를 보내주기
    void SendPlayerDataBothPlayer(WebSocketSession playerSession) throws IOException
    {
        // 응답 여부 기록
        int index = players.indexOf(playerSession);

        SendPlayerData(players.get(index), "player", playerDatas[index]);
        SendPlayerData(players.get(index), "opponent", playerDatas[1-index]);
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

    void SendSign(WebSocketSession playerSession, String contents) throws IOException
    {
        // 클라이언트로 응답
        playerSession.sendMessage(new TextMessage(ToJson("sign", contents)));

    }

    void SendPlayerData(WebSocketSession playerSession, String type, PlayerDTO playerDTO) throws IOException
    {

        var responseData = Map.of(
                "id", playerDTO.getId(),
                "name", playerDTO.getName(),
                "area_point", playerDTO.getAreaPoint(),
                "normal_point", playerDTO.getNormalPoint(),
                "level_id", playerDTO.getLevelId()
        );
        String jsonResponse = objectMapper.writeValueAsString(responseData);

        // 클라이언트로 응답
        playerSession.sendMessage(new TextMessage(ToJson(type, jsonResponse)));

    }

    void Close(WebSocketSession session) throws IOException
    {
        session.close();
    }
}


    /*

            // 3초 후에 다음 질문 호출
            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.schedule(() ->
            {
                try
                {
                    CallNextQuestion();
                } catch (IOException e)
                {
                    System.err.println("Error during delay: " + e.getMessage());
                }
            }, 3, TimeUnit.SECONDS);
    */
