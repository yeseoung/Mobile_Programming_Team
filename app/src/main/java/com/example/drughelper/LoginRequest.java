package com.example.drughelper;

public class LoginRequest {
    private String email;
    private String password;

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getter / Setter가 필요하다면 추가
}
