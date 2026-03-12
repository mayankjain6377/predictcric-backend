package com.sportsprediction.app.dto.response;

import lombok.Data;

@Data
public class QuestionResponse {
    private Long id;
    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctAnswer;

    public QuestionResponse(Long id, String questionText, String optionA, String optionB, String optionC, String optionD) {
    }

    public QuestionResponse() {

    }
}