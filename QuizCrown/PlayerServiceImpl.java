package com.example.quizSever21;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlayerServiceImpl implements PlayerService {
    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    LevelRepository levelRepository;

    @Override
    public int getLevelFromPoint(int point) {
        // lvID 추출
        // 첫 번째 값 반환
        var i = levelRepository.findAll().stream()
                .filter(level -> level.getMinimumPoint() <= point && level.getMaximumPoint() >= point)
                .map(Level::getLvID) // lvID 추출
                .findFirst() // 첫 번째 값 반환
                .orElse(null);
        return i; // 값이
    }

    @Override
    public List<PlayerDTO> findAll() {
        return playerRepository.findAll().stream()
                .map(Utils::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PlayerDTO findById(long id) {
        return playerRepository.findById((int) id)
                .map(Utils::toDTO)
                .orElse(null);
    }

    @Override
    public List<PlayerDTO> findByName(String name) {
        return playerRepository.findAll().stream()
                .map(Utils::toDTO)
                .filter(player -> player.getName().equalsIgnoreCase(name))
                .collect(Collectors.toList());
    }

    @Override
    public boolean checkVaild(String name){
        var result = findByName(name);

        return result.isEmpty();
    }

    @Override
    public void save(PlayerDTO playerDTO) {
        Player player =  Utils.toEntity(playerDTO);
        playerRepository.save(player);
    }
}