package com.substring.auth.dto;

public record LoginRequest(
        String email,
        String password
) {
}
