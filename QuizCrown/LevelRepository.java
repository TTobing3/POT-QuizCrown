package com.example.quizSever21;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LevelRepository extends JpaRepository<Level, Integer>  {
    // 기본 법칙에 따를 수 있음

    @Override
    List<Level> findAll();
}
