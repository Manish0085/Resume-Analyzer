package com.substring.auth.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class OpenRouterResponse {

    private List<Choice> choices;

    @Data
    @NoArgsConstructor
    public static class Choice {
        private Message message;
    }

    @Data
    @NoArgsConstructor
    public static class Message {
        private String content;
    }

    public String extractText() {
        if (choices == null || choices.isEmpty()) {
            return null;
        }
        return choices.get(0).getMessage().getContent();
    }
}
