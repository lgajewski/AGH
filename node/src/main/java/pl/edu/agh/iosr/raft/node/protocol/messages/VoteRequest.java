package pl.edu.agh.iosr.raft.node.protocol.messages;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import pl.edu.agh.iosr.raft.node.Message;


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
public class VoteRequest implements Message {
    private Integer term;
    private String candidateId;
    private Integer lastLogIndex;
    private Integer lastLogTerm;

    public VoteRequest() {
    }

    public VoteRequest(int term, String leaderId, int lastLogIndex, int lastLogTerm) {
        this.term = term;
        this.candidateId = leaderId;
        this.lastLogIndex = lastLogIndex;
        this.lastLogTerm = lastLogTerm;
    }

    public Integer getTerm() {
        return term;
    }

    public String getCandidateId() {
        return candidateId;
    }

    public Integer getLastLogIndex() {
        return lastLogIndex;
    }

    public Integer getLastLogTerm() {
        return lastLogTerm;
    }

    @Override
    public String getRoutingKey() {
        return candidateId;
    }
}
