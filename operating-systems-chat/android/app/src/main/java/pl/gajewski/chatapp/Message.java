package pl.gajewski.chatapp;

import org.json.JSONException;
import org.json.JSONObject;

public class Message {

    private String sender;
    private String date;
    private String time;
    private String content;

    public Message(String sender, String date, String time, String content) {
        this.sender = sender;
        this.date = date;
        this.time = time;
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getContent() {
        return content;
    }

    public String printable() {
        return "["+sender+"] " + time + " - " + content;
    }

    public static JSONObject getJSONTime(String date, String time) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("date", date);
        jsonObject.put("time", time);
        return jsonObject;
    }

}