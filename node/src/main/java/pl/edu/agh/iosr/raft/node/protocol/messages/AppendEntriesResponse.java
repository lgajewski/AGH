package pl.edu.agh.iosr.raft.node.protocol.messages;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import pl.edu.agh.iosr.raft.node.Message;

import java.io.Serializable;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
public class AppendEntriesResponse implements Serializable, Message {
    private Integer term;
    private Boolean success;
    private String leaderId;

    public AppendEntriesResponse() {
    }

    public AppendEntriesResponse(int term, boolean success, String leaderId) {
        this.term = term;
        this.success = success;
        this.leaderId = leaderId;
    }

    public Integer getTerm() {
        return term;
    }

    public Boolean isSuccess() {
        return success;
    }

    @Override
    public String getRoutingKey() {
        return leaderId;
    }
}
