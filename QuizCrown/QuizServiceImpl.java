package com.example.quizSever21;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class QuizServiceImpl implements QuizService {

    @Autowired
    QuizRepository quizRepository;

    @Override
    public List<QuizDTO> findAll() {
        return quizRepository.findAll().stream()
                .map(Utils::toQuizDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<QuizDTO> getRandomQuiz(int count) {
        var totalContents = findAll();
        var totalCount = totalContents.size();

        // 전체 콘텐츠 크기에서 무작위 인덱스 세 개 추출
        var randomIndexArr = new Random().ints(0,totalCount)
                .distinct()
                .limit(count)
                .toArray();

        // 추출한 int 값을 전체 콘텐츠 리스트의 인덱스로 매핑하여 반환
        return Arrays.stream(randomIndexArr)
                .mapToObj(totalContents::get)
                .toList();
    }

    @Override
    public List<QuizDTO> findAllByDD(int dd) {
        return quizRepository.findAll().stream()
                .filter(quiz -> quiz.getDD() == dd)
                .map(Utils::toQuizDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<QuizDTO> getRandomQuizByDD(int count, int dd) {
        var totalContents = findAllByDD(dd);
        var totalCount = totalContents.size();

        // 전체 콘텐츠 크기에서 무작위 인덱스 세 개 추출
        var randomIndexArr = new Random().ints(0,totalCount)
                .distinct()
                .limit(count)
                .toArray();

        // 추출한 int 값을 전체 콘텐츠 리스트의 인덱스로 매핑하여 반환
        return Arrays.stream(randomIndexArr)
                .mapToObj(totalContents::get)
                .toList();
    }

}
