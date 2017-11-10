package pl.edu.agh.iosr.raft.node.protocol.messages;

import com.fasterxml.jackson.annotation.JsonTypeInfo;


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
public class VoteRequest {
    private Integer term;
    private Integer candidateId;
    private Integer lastLogIndex;
    private Integer lastLogTerm;

    public VoteRequest(){}

    public VoteRequest(int term, int leaderId, int lastLogIndex, int lastLogTerm){
        this.term = term;
        this.candidateId = leaderId;
        this.lastLogIndex = lastLogIndex;
        this.lastLogTerm = lastLogTerm;
    }

    public Integer getTerm() {
        return term;
    }

    public Integer getCandidateId() {
        return candidateId;
    }

    public Integer getLastLogIndex() {
        return lastLogIndex;
    }

    public Integer getLastLogTerm() {
        return lastLogTerm;
    }
}
