package com.nurapp.user.auth;

public record TokenPairResponse(String accessToken, String refreshToken) {
}