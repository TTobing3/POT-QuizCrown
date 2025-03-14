package com.example.quizSever21;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Integer> {
    @Query(value = "SELECT * FROM quiz_table ORDER BY RAND() LIMIT :count", nativeQuery = true)
    List<Quiz> findRandomQuizzes(int count);

    @Query(value = "SELECT * FROM quiz_table WHERE DD = :ddValue ORDER BY RAND() LIMIT :count", nativeQuery = true)
    List<Quiz> findRandomQuizzesByDdValue(int ddValue, int count);
}
