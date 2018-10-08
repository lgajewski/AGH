package lgajewski.distributed.ex3.chat;


import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Message {

    public static final int SIZE_IN_BYTES = 70;

    public static final int MAX_USERNAME = 6;
    public static final int MAX_CONTENT = 20;

    private final String username;
    private final String content;
    private final Long timestamp;

    public Message(String username, String content) {
        this(username, content, System.currentTimeMillis());
    }

    public Message(String username, String content, long timestamp) {
        this.username = username;
        this.content = ChatApp.shorten(content, MAX_CONTENT);
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getUsername() {
        return username;
    }

    public String getContent() {
        return content;
    }

    public byte[] getBytes() throws IOException {
        String msg = toString();
        int length = SIZE_IN_BYTES - msg.length();
        if (length < SIZE_IN_BYTES) {
            while (length-- > 0) {
                msg += ' ';
            }
        }

        return msg.getBytes();
    }

    public static Message retrieveMessage(InputStream inputStream) throws IOException {
        Properties properties = new Properties();
        properties.load(inputStream);

        String username = (String) properties.get("u");
        String msg = (String) properties.get("msg");
        Long timestamp = Long.valueOf((String) properties.get("time"));
        String hash = (String) properties.get("hash");


        Message message = new Message(username, msg, timestamp);

        if (!String.valueOf(message.hashCode()).equals(hash)) {
            throw new IllegalArgumentException("Message has wrong format! Hashcode is different!");
        }

        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message = (Message) o;

        return username.equals(message.username) && content.equals(message.content) && timestamp.equals(message.timestamp);
    }

    @Override
    public int hashCode() {
        int result = username.hashCode();
        result = 31 * result + content.hashCode();
        result = 31 * result + timestamp.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "u=" + username + "\n" +
                "msg=" + content + "\n" +
                "time=" + timestamp + "\n" +
                "hash=" + hashCode() + "\n";
    }

    public int getMaxMessageSize() {
        return SIZE_IN_BYTES;
    }
}