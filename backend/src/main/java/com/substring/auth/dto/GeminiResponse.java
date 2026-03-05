package com.substring.auth.dto;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class GeminiResponse {

    private List<Candidate> candidates;

    @Data
    @NoArgsConstructor
    public static class Candidate {
        private Content content;
    }

    @Data
    @NoArgsConstructor
    public static class Content {
        private List<Part> parts;
    }

    @Data
    @NoArgsConstructor
    public static class Part {
        private String text;
    }

    public String extractText() {
        return candidates.get(0).getContent().getParts().get(0).getText();
    }
}
