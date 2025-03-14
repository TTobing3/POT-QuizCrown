package com.example.quizSever21;

import java.util.List;

public interface PlayerService {
    List<PlayerDTO> findAll();
    PlayerDTO findById(long id);
    List<PlayerDTO> findByName(String name);
    void save(PlayerDTO playerDTO);

    int getLevelFromPoint(int point);
    boolean checkVaild(String name);
}
