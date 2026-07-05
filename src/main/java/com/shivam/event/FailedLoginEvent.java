package com.shivam.event;

import java.time.LocalDateTime;

public class FailedLoginEvent {
    private String ip;
    private String username;
    private LocalDateTime timestamp;

    public FailedLoginEvent() {
    }

    public FailedLoginEvent(String ip, String username, LocalDateTime timestamp) {
        this.ip = ip;
        this.username = username;
        this.timestamp = timestamp;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
