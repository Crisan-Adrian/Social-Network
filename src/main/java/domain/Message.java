package domain;

import java.time.LocalDateTime;
import java.util.List;

public class Message extends Entity<Long> {
    Long from;
    List<Long> to;
    String message;
    LocalDateTime timestamp;
    Long reply;

    public Long getReply() {
        return reply;
    }

    public void setReply(Long reply) {
        this.reply = reply;
    }

    public Message(Long from, List<Long> to, String message, LocalDateTime timestamp) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.timestamp = timestamp;
        this.reply = null;
    }

    public Message(Long from, List<Long> to, String message, LocalDateTime timestamp, Long reply) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.timestamp = timestamp;
        this.reply = reply;
    }

    public Long getFrom() {
        return from;
    }

    public void setFrom(Long from) {
        this.from = from;
    }

    public List<Long> getTo() {
        return to;
    }

    public void setTo(List<Long> to) {
        this.to = to;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Message{" +
                "from=" + from +
                ", to=" + to +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                ", reply=" + reply +
                ", ID=" + getID() +
                '}';
    }
}
