package pl.edu.agh.iosr.raft.node.protocol.messages;


import com.fasterxml.jackson.annotation.JsonTypeInfo;
import pl.edu.agh.iosr.raft.node.protocol.Entry;

import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
public class AppendEntriesRequest {
    private Integer term;
    private Integer leaderId;
    private Integer prevLogIndex;
    private Integer prevLogTerm;
    private List<Entry> entries;
    private Integer leaderCommit;

    public AppendEntriesRequest(){};

    public AppendEntriesRequest(int term, int leaderId) {
        this.term = term;
        this.leaderId = leaderId;
    }


    public Integer getTerm() {
        return term;
    }

    public Integer getLeaderId() {
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

    public void setPrevLogIndex(Integer prevLogIndex) {
        this.prevLogIndex = prevLogIndex;
    }

    public void setPrevLogTerm(Integer prevLogTerm) {
        this.prevLogTerm = prevLogTerm;
    }

    public void setLeaderCommit(Integer leaderCommit) {
        this.leaderCommit = leaderCommit;
    }

    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }
}
