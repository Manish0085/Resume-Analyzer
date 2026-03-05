package com.substring.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpenRouterRequest {

    private String model;
    private List<Message> messages;

    @JsonProperty("response_format")
    private ResponseFormat responseFormat;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        private String role;
        private String content;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponseFormat {
        private String type;
    }

    public static OpenRouterRequest of(String model, String prompt) {
        return new OpenRouterRequest(
                model,
                List.of(new Message("user", prompt)),
                new ResponseFormat("json_object"));
    }
}
