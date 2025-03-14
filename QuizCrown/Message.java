package com.example.quizSever21;

import lombok.*;

//DTO
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter @ToString
public class Message
{
    private String type, msg;
}
