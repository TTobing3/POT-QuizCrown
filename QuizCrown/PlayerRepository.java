package com.example.quizSever21;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.naming.Name;
import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Integer>  {
    // 기본 법칙에 따를 수 있음
    List<Player> findByName(String name);
}
