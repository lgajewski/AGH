package pl.edu.agh.iosr.raft.node.protocol.messages;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import pl.edu.agh.iosr.raft.node.Message;

import java.io.Serializable;

public class AppendEntriesResponse implements Message, Serializable {
    private Integer term;
    private Boolean success;
    private String leaderId;
    private String senderId;
    private Integer matchIndex;

    public AppendEntriesResponse() {
    }

    public AppendEntriesResponse(int term, boolean success, String leaderId, String senderId, int matchIndex) {
        this.term = term;
        this.success = success;
        this.leaderId = leaderId;
        this.senderId = senderId;
        this.matchIndex = matchIndex;
    }

    public Integer getTerm() {
        return term;
    }

    public Boolean isSuccess() {
        return success;
    }

    public String getLeaderId() {
        return leaderId;
    }

    @Override
    public String getRoutingKey() {
        return leaderId;
    }

    @Override
    public String getSenderId() {
        return senderId;
    }

    @Override
    public String toString() {
        return "AppendEntriesResponse = {" +
                "term:" + term +
                ", success:" + success +
                ", leaderId:" + leaderId +
                ", senderId:" + senderId +
                "}";
    }

    public Integer getMatchIndex() {
        return matchIndex;
    }
}
