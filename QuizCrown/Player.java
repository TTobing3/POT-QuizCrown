package com.example.quizSever21;

//Player Entity

import jakarta.persistence.*;
import lombok.*;

@Entity
@ToString
@Builder
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Table(name = "player_table") // 보통 클래스 이름과 다를 경우 작성 (대소문자 고려 x)
public class Player
{
    @Id // pk 키 벨류를 뜻함
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto increament 표시
    private long id;

    @Column(name = "`name`")
    private String name;
    private int areaPoint, normalPoint, levelId;
}
