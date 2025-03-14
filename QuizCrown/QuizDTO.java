package com.example.quizSever21;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizDTO {
    private long num;
    private String quiz;
    private int area;
    private String explain;
    private String correct;
    private String option1;
    private String option2;
    private String option3;
    private int DD;
}
