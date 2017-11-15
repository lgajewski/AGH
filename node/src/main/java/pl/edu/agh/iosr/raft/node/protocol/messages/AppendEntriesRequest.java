package pl.edu.agh.iosr.raft.node.protocol.messages;


import com.fasterxml.jackson.annotation.JsonTypeInfo;
import pl.edu.agh.iosr.raft.node.Message;
import pl.edu.agh.iosr.raft.node.protocol.Entry;

import java.io.Serializable;
import java.util.List;

public class AppendEntriesRequest implements Message, Serializable {
    private Integer term;
    private String leaderId;
    private Integer prevLogIndex;
    private Integer prevLogTerm;
    private List<Entry> entries;
    private Integer leaderCommit;
    private String sendTo;

    public AppendEntriesRequest(){}

    public AppendEntriesRequest(int term, String leaderId, int prevLogIndex, int prevLogTerm, int leaderCommit, String sendTo) {
        this.term = term;
        this.leaderId = leaderId;
        this.prevLogIndex = prevLogIndex;
        this.prevLogTerm = prevLogTerm;
        this.leaderCommit = leaderCommit;
        this.sendTo = sendTo;
    }

    public Integer getTerm() {
        return term;
    }

    public String getLeaderId() {
        return leaderId;
    }

    public Integer getPrevLogIndex() {
        return prevLogIndex;
    }

    public Integer getPrevLogTerm() {
        return prevLogTerm;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public Integer getLeaderCommit() {
        return leaderCommit;
    }

    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }

    @Override
    public String getRoutingKey() {
        return sendTo;
    }

    @Override
    public String getSenderId() {
        return leaderId;
    }

    @Override
    public String toString() {
        return "AppendEntriesRequest = {" +
                "term:" + term +
                ", leaderId:" + leaderId +
                ", prevLogIndex:" + prevLogIndex +
                ", prevLogTerm:" + prevLogTerm +
                ", entries:" + entries +
                ", leaderCommit:" + leaderCommit +
                ", sendTo:" + sendTo +
                "}";
    }
}
