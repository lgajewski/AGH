package pl.edu.agh.iosr.raft.node.protocol.messages;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
public class VoteResponse {
    private Integer term;
    private Boolean voteGranted;

    public VoteResponse(){}

    public VoteResponse(int term, boolean voteGranted){
        this.term = term;
        this.voteGranted = voteGranted;
    }

}
