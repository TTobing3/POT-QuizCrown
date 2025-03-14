package com.example.quizSever21;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.Map;

@Controller
public class MainController
{
    // 매칭
    private final ObjectMapper objectMapper = new ObjectMapper();

    @RequestMapping("/")
    @ResponseBody
    public String home()
    {
        return "Home-Page";
    }

    @Autowired
    QuizRepository quizRepository;
    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    MyWebSocketHandler myWebSocketHandler;

    @Autowired
    PlayerService playerService;

    @RequestMapping("/showQuiz")
    @ResponseBody
    public String showQuiz(){

        String result = null;

        result = quizRepository.findAll().toString(); //Read

        return result.toString();
    }

    @RequestMapping("/showPlayer")
    @ResponseBody
    public String showPlayer(){

        String result = null;

        result =  playerRepository.findAll().toString(); //Read

        return result.toString();
    }

    @RequestMapping("/update")
    @ResponseBody
    public String update(){

        List<Quiz> result = null;

        result = quizRepository.findAll(); //Read


        for(int i = 0; i < 10; i++)
        {
            Quiz update = new Quiz
                    (
                            (int)i,
                            "문제"+i,
                            i%2,
                            "설명",
                            i+"번 문제 정답",
                            "선지 1", "선지 2", "선지 3",
                            i%2
                    );
            quizRepository.save(update);
        }

        return result.toString();
    }

    @RequestMapping("/rand/{num}")
    @ResponseBody
    public String rand(@PathVariable("num") int num){

        List<Quiz> result = null;

        // 예시로 QuizRepository를 통해 count 개의 Quiz 데이터를 가져온다고 가정
        result = quizRepository.findRandomQuizzes(num); // 예시 메서드

        return result.toString();
    }

    @RequestMapping("/room")
    @ResponseBody
    public String room()
    {

        StringBuilder result = new StringBuilder();

        result.append("[ current connected player ]")
                .append("<br>");

        for(WebSocketSession player : myWebSocketHandler.playerSessionList)
        {
            result.append(player.getId())
                    .append("<br>");
        }

        result.append("[ current connectData ]")
                .append("<br>");

        for(ConnectData connectData : myWebSocketHandler.playerConnectDataList)
        {
            result.append("닉네임 : ")
                    .append(connectData.playerDTO.getName()).append(" / ")
                    .append("난이도 : ")
                    .append(connectData.getDd()).append(" / ")
                    .append("session ID : ")
                    .append("<br>");
        }

        result.append("[ current empty room ]")
                .append("<br>");

        for(int i = 0; i<myWebSocketHandler.EmptyRoomList.size(); i++)
        {
            var room = myWebSocketHandler.EmptyRoomList.get(i);
            result.append(i)
                    .append(". room : ")
                    .append("<br>player 1 - ")
                    .append(room.players.getFirst().getId())
                    .append(" : ")
                    .append(room.playerDatas[0] != null ? room.playerDatas[0].getName() : "플레이어 데이터 연동 X")
                    .append("<br>");
        }

        result.append("[ current battle room ]")
                .append("<br>");

        for(int i = 0; i<myWebSocketHandler.PlayRoomList.size(); i++)
        {
            var room = myWebSocketHandler.PlayRoomList.get(i);
            result.append(i)
                    .append(". room : ")
                    .append("<br>player 1 - ")
                    .append(room.players.getFirst().getId())
                    .append(" : ")
                    .append(room.playerDatas[0] != null ? room.playerDatas[0].getName() : "플레이어 데이터 연동 X")
                    .append("<br>player 2 - ")
                    .append(room.players.get(1).getId())
                    .append(" : ")
                    .append(room.playerDatas[1] != null ? room.playerDatas[1].getName() : "플레이어 데이터 연동 X")
                    .append("<br>");
        }
        return result.toString();
    }

    @RequestMapping("/delete/{num}")
    @ResponseBody
    public String delete(@PathVariable("num") int num){

        List<Quiz> result = null;

        result = quizRepository.findAll(); //Read

        quizRepository.deleteById(num);

        return result.toString();
    }

    @RequestMapping("/checkNameValid/{name}")
    @ResponseBody
    public String checkNameValid(@PathVariable("name") String name){
        return playerService.checkVaild(name) ? "vaild" : "invaild";
    }

    @RequestMapping("/registerName/{name}")
    @ResponseBody
    public String register(@PathVariable("name") String name) throws JsonProcessingException {
        var check = playerService.checkVaild(name);

        if(check)
        {
            PlayerDTO newPlayer = PlayerDTO.builder()
                    .name(name)
                    .areaPoint(0)
                    .normalPoint(0)
                    .levelId(1)
                    .build();

            var responseData = Map.of(
                    "name", newPlayer.getName(),
                    "area_point", newPlayer.getAreaPoint(),
                    "normal_point", newPlayer.getNormalPoint(),
                    "level_id", newPlayer.getLevelId()
            );

            playerService.save(newPlayer);

            return objectMapper.writeValueAsString(responseData);
        }
        else
        {
            return "Register Failed";
        }
    }

    @RequestMapping("/login/{name}")
    @ResponseBody
    public String login(@PathVariable("name") String name) throws JsonProcessingException {
        var playerDTO = playerService.findByName(name);

        if( !playerDTO.isEmpty() )
        {
            var newPlayer = playerDTO.getFirst();
            var responseData = Map.of(
                    "name", newPlayer.getName(),
                    "area_point", newPlayer.getAreaPoint(),
                    "normal_point", newPlayer.getNormalPoint(),
                    "level_id", newPlayer.getLevelId()
            );

            return objectMapper.writeValueAsString(responseData);
        }
        else
        {
            return "Login Failed";
        }
    }
}
