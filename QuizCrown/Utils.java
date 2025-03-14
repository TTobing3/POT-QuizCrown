package com.example.quizSever21;

public class Utils {
    public static PlayerDTO toDTO(Player entity)
    {
        return  PlayerDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .areaPoint(entity.getAreaPoint())
                .normalPoint(entity.getNormalPoint())
                .levelId(entity.getLevelId())
                .build();
    }

    public static  Player toEntity(PlayerDTO dto)
    {
        return Player.builder()
                .id(dto.getId())
                .name(dto.getName())
                .areaPoint(dto.getAreaPoint())
                .normalPoint(dto.getNormalPoint())
                .levelId(dto.getLevelId())
                .build();
    }

    public static QuizDTO toQuizDTO(Quiz entity)
    {
        return  QuizDTO.builder()
                .num(entity.getNum())
                .quiz(entity.getQuiz())
                .area(entity.getArea())
                .explain(entity.getExplain())
                .correct(entity.getCorrect())
                .option1(entity.getOption1())
                .option2(entity.getOption2())
                .option3(entity.getOption3())
                .DD(entity.getDD())
                .build();
    }

    public static  Quiz toQuizEntity(QuizDTO dto)
    {
        return Quiz.builder()
                .num(dto.getNum())
                .quiz(dto.getQuiz())
                .area(dto.getArea())
                .explain(dto.getExplain())
                .correct(dto.getCorrect())
                .option1(dto.getOption1())
                .option2(dto.getOption2())
                .option3(dto.getOption3())
                .DD(dto.getArea())
                .build();
    }
}
