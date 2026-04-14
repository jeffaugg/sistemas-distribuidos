package br.edu.ufc.quixada.sd.t1.voting;

import br.edu.ufc.quixada.sd.t1.protocol.UserRole;

import java.time.Instant;

public class VotingSession {
    private String token;
    private String username;
    private UserRole role;
    private Instant createdAt;

    public VotingSession() {
    }

    public VotingSession(String token, String username, UserRole role, Instant createdAt) {
        this.token = token;
        this.username = username;
        this.role = role;
        this.createdAt = createdAt;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}