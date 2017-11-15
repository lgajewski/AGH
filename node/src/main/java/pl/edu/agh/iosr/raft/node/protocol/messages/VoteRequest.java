package pl.edu.agh.iosr.raft.node.protocol.messages;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import pl.edu.agh.iosr.raft.node.Message;

import java.io.Serializable;


public class VoteRequest implements Message {
    private Integer term;
    private String candidateId;
    private Integer lastLogIndex;
    private Integer lastLogTerm;
    private String sendTo;

    public VoteRequest(){}

    public VoteRequest(int term, String candidateId, int lastLogIndex, int lastLogTerm, String sendTo) {
        this.term = term;
        this.candidateId = candidateId;
        this.lastLogIndex = lastLogIndex;
        this.lastLogTerm = lastLogTerm;
        this.sendTo = sendTo;
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
        return sendTo;
    }

    @Override
    public String getSenderId() {
        return candidateId;
    }

    @Override
    public String toString() {
        return "VoteRequest = {" +
                "term:" + term +
                ", candidateId:" + candidateId +
                ", lastLogIndex:" + lastLogIndex +
                ", lastLogTerm:" + lastLogTerm +
                ", sendTo:" + sendTo +
                "}";
    }
}
