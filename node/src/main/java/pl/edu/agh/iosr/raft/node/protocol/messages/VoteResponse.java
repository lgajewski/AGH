package pl.edu.agh.iosr.raft.node.protocol.messages;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import pl.edu.agh.iosr.raft.node.Message;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
public class VoteResponse implements Message {
    private String candidateId;
    private Integer term;
    private Boolean voteGranted;

    public VoteResponse() {
    }

    public VoteResponse(int term, boolean voteGranted, String candidateId) {
        this.term = term;
        this.voteGranted = voteGranted;
        this.candidateId = candidateId;
    }

    @Override
    public String getRoutingKey() {
        return candidateId;
    }
}
