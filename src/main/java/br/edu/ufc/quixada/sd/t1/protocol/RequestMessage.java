package br.edu.ufc.quixada.sd.t1.protocol;

import java.util.LinkedHashMap;
import java.util.Map;

public class RequestMessage {
    private RequestType type;
    private String username;
    private String sessionToken;
    private UserRole role;
    private Map<String, Object> payload = new LinkedHashMap<>();

    public RequestMessage() {
    }

    public RequestMessage(RequestType type) {
        this.type = type;
    }

    public RequestType getType() {
        return type;
    }

    public void setType(RequestType type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public void setPayload(Map<String, Object> payload) {
        this.payload = payload;
    }
}