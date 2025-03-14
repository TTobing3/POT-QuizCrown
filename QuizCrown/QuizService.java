package com.example.quizSever21;

import java.util.List;

public interface QuizService {
    List<QuizDTO> findAll();
    List<QuizDTO> getRandomQuiz(int count);
    List<QuizDTO> findAllByDD(int dd);
    List<QuizDTO> getRandomQuizByDD(int count, int dd);
}
