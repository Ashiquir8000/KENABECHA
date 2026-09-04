package com.example.catalogproject.controller;

import com.example.catalogproject.dto.ChatMessageDTO;
import com.example.catalogproject.entity.ChatMessage;
import com.example.catalogproject.entity.StaffMessage;
import com.example.catalogproject.entity.User;
import com.example.catalogproject.repository.ChatMessageRepository;
import com.example.catalogproject.repository.StaffMessageRepository;
import com.example.catalogproject.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final StaffMessageRepository staffMessageRepository; // স্টাফ মেসেজ রিপোজিটরি যুক্ত করা হলো



    @MessageMapping("/chat.send")
    public void processMessage(@Payload ChatMessageDTO chatDTO) {
        User user = userRepository.findById(chatDTO.getUserId()).orElse(null);

        if (user != null) {
            ChatMessage message = new ChatMessage();
            message.setUser(user);
            message.setSenderType(chatDTO.getSenderType());
            message.setContent(chatDTO.getContent());
            chatMessageRepository.save(message);

            ChatResponse response = new ChatResponse(user.getId(), user.getUserName(), message.getSenderType(), message.getContent());

            messagingTemplate.convertAndSend("/topic/moderator", response);
            messagingTemplate.convertAndSend("/topic/user/" + user.getId(), response);
        }
    }

    @GetMapping("/chat/history/{userId}")
    @ResponseBody
    public List<ChatMessageDTO> getChatHistory(@PathVariable("userId") Integer userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return List.of();

        return chatMessageRepository.findByUserOrderByTimestampAsc(user).stream()
                .map(msg -> {
                    ChatMessageDTO dto = new ChatMessageDTO();
                    dto.setUserId(user.getId());
                    dto.setSenderType(msg.getSenderType());
                    dto.setContent(msg.getContent());
                    return dto;
                }).toList();
    }



    @MessageMapping("/staffchat.send")
    public void processStaffMessage(@Payload StaffChatRequest request) {
        User sender = userRepository.findById(request.getSenderId()).orElse(null);
        User receiver = userRepository.findById(request.getReceiverId()).orElse(null);

        if (sender != null && receiver != null) {
            StaffMessage message = new StaffMessage();
            message.setSender(sender);
            message.setReceiver(receiver);
            message.setContent(request.getContent());
            message.setTimestamp(LocalDateTime.now());

            staffMessageRepository.save(message);

            StaffChatResponse response = new StaffChatResponse(
                    sender.getId(), sender.getUserName(),
                    receiver.getId(), receiver.getUserName(),
                    message.getContent()
            );

            messagingTemplate.convertAndSend("/topic/user/" + sender.getId(), response);
            messagingTemplate.convertAndSend("/topic/user/" + receiver.getId(), response);
        }
    }

    @GetMapping("/chat/staff/history/{userId1}/{userId2}")
    @ResponseBody
    public List<StaffChatResponse> getStaffChatHistory(@PathVariable Integer userId1, @PathVariable Integer userId2) {
        User user1 = userRepository.findById(userId1).orElse(null);
        User user2 = userRepository.findById(userId2).orElse(null);

        if (user1 == null || user2 == null) return List.of();

        return staffMessageRepository.findBySenderAndReceiverOrReceiverAndSenderOrderByTimestampAsc(user1, user2, user1, user2)
                .stream().map(msg -> new StaffChatResponse(
                        msg.getSender().getId(), msg.getSender().getUserName(),
                        msg.getReceiver().getId(), msg.getReceiver().getUserName(),
                        msg.getContent()
                )).toList();
    }


    @Data
    @AllArgsConstructor
    public static class ChatResponse {
        private Integer userId;
        private String userName;
        private String senderType;
        private String content;
    }

    @Data
    public static class StaffChatRequest {
        private Integer senderId;
        private Integer receiverId;
        private String content;
    }

    @Data
    @AllArgsConstructor
    public static class StaffChatResponse {
        private Integer senderId;
        private String senderName;
        private Integer receiverId;
        private String receiverName;
        private String content;
    }
}