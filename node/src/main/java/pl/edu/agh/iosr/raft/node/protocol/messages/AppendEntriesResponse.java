package pl.edu.agh.iosr.raft.node.protocol.messages;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
public class AppendEntriesResponse implements Serializable {
    private Integer term;
    private Boolean success;

    public AppendEntriesResponse(){}

    public AppendEntriesResponse(int term, boolean success){
        this.term = term;
        this.success = success;
    }


    public Integer getTerm() {
        return term;
    }

    public Boolean isSuccess() {
        return success;
    }

}
