package br.edu.ufc.quixada.sd.t1.protocol;

import java.util.LinkedHashMap;
import java.util.Map;

public class ReplyMessage {
    private boolean success;
    private String message;
    private Map<String, Object> payload = new LinkedHashMap<>();

    public ReplyMessage() {
    }

    public ReplyMessage(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public void setPayload(Map<String, Object> payload) {
        this.payload = payload;
    }
}