package com.example.userService;

public class AuthenticationResponse {

    private String token;

    public AuthenticationResponse(String token) {
        this.token = token;
    }

    // Getter and setter for the token
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
