package com.example.catalogproject.dto;

import lombok.Data;

@Data
public class ChatMessageDTO {
    private Integer userId;
    private String senderType; 
    private String content;
}