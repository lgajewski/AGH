package pl.gajewski.server.chat.user;

import java.util.*;

public class User {

    private String username;
    private Map<String, List<Message>> messageMap;
    private String sessionID;

    private int sent;
    private int received;

    public User(String name, String sessionID) {
        this.username = name;
        this.messageMap = new HashMap<>();
        this.sessionID = sessionID;
    }

    public String getUsername() {
        return username;
    }

    public List<Message> getMessages(String recipient) {
        if(messageMap.containsKey(recipient)) {
            return messageMap.get(recipient);
        } else {
            return new ArrayList<>();
        }
    }

    public void clearMessages() {
        messageMap.clear();
    }

    public void addMessage(String recipient, Message msg) {
        if(messageMap.containsKey(recipient)) {
            messageMap.get(recipient).add(msg);
        } else {
            List<Message> list = new ArrayList<>();
            list.add(msg);
            messageMap.put(recipient, list);
        }
    }

    public String getSessionID() {
        return sessionID;
    }

    public int getSent() {
        return sent;
    }

    public int getReceived() {
        return received;
    }

    public void inc(String recipient) {
        if(recipient.equals(username)) {
            sent++;
        } else {
            received++;
        }
    }
}