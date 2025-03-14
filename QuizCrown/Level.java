package com.example.quizSever21;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity @Table(name = "level_table")
@ToString
@Data
@Builder
@NoArgsConstructor()
@AllArgsConstructor
public class Level {

    @Id // pk 키 벨류를 뜻함
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto increament 표시
    private long id;

    private int lvID, minimumPoint, maximumPoint;
}
