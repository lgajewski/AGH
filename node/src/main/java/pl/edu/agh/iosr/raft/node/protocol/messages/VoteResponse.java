package pl.edu.agh.iosr.raft.node.protocol.messages;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import pl.edu.agh.iosr.raft.node.Message;

import java.io.Serializable;

public class VoteResponse implements Message, Serializable {
    private String candidateId;
    private String senderId;
    private Integer term;
    private Boolean voteGranted;

    public VoteResponse(){}

    public VoteResponse(int term, boolean voteGranted, String candidateId, String senderId) {
        this.term = term;
        this.voteGranted = voteGranted;
        this.candidateId = candidateId;
        this.senderId = senderId;
    }

    @Override
    public String getRoutingKey() {
        return candidateId;
    }

    @Override
    public String getSenderId() {
        return senderId;
    }

    public String getCandidateId() {
        return candidateId;
    }

    public Integer getTerm() {
        return term;
    }

    public Boolean getVoteGranted() {
        return voteGranted;
    }

    @Override
    public String toString() {
        return "VoteResponse = {" +
                "term:" + term +
                ", candidateId:" + candidateId +
                ", senderId:" + senderId +
                ", voteGranted:" + voteGranted +
                "}";
    }
}
