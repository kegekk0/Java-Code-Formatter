package pl.pja.edu.tpo07.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class StoredCode implements Serializable {
    private final String id;
    private final String code;
    private final LocalDateTime expirationTime;

    public StoredCode(String id, String code, LocalDateTime expirationTime) {
        this.id = id;
        this.code = code;
        this.expirationTime = expirationTime;
    }

    public String getId() { return id; }
    public String getCode() { return code; }
    public LocalDateTime getExpirationTime() { return expirationTime; }
    public boolean isExpired() { return LocalDateTime.now().isAfter(expirationTime); }
}