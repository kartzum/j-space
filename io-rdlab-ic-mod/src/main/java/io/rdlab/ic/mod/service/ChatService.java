package io.rdlab.ic.mod.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class ChatService {
    private final ChatClient chatClient;

    public ChatService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String prompt(String content) {
        StringBuilder answer = new StringBuilder();
        chatClient.prompt(content).stream().content().toStream().forEach(answer::append);
        return answer.toString();
    }
}
