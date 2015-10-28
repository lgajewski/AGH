package pl.gajewski.server.chat.user;

import com.sun.istack.internal.NotNull;
import org.json.JSONObject;

import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;

public class Message {

    private User sender;
    private LocalDate date;
    private LocalTime time;
    private String content;

    public Message(User sender, LocalDate date, LocalTime time, String content) {
        this.sender = sender;
        this.date = date;
        this.time = time;
        this.content = content;
    }

    public User getSender() {
        return sender;
    }

    public LocalDate getMessageDate() {
        return date;
    }

    public LocalTime getMessageTime() {
        return time;
    }

    public JSONObject getJSONObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sender", sender.getUsername());
        jsonObject.put("date", date);
        jsonObject.put("time", time);
        jsonObject.put("content", content);
        return jsonObject;
    }


}