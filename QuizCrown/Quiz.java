package com.example.quizSever21;

//Quiz Entity

import jakarta.persistence.*;
import lombok.*;

@Entity
@ToString
@Builder
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Table(name = "quiz_table") // 보통 클래스 이름과 다를 경우 작성 (대소문자 고려 x)
public class Quiz {
    @Id // pk 키 벨류를 뜻함
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto increament 표시
    private long num;

    private String quiz;
    private int area;
    @Column(name = "`explain`")
    private String explain;
    private String correct;
    private String option1;
    private String option2;
    private String option3;
    private int DD;
}
