package pl.edu.agh.iosr.raft.node.commands;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
public class Increment implements Command {
    private String variableName;
    private String routingKey;
    private String senderId;

    public Increment(String sendTo, String senderId){
        this.routingKey = sendTo;
        this.senderId = senderId;
    }
    @Override
    public CommandType getType() {
        return CommandType.INCREMENT;
    }

    @Override
    public String getVariableName() {
        return "x";
    }

    @Override
    public String getRoutingKey() {
        return routingKey;
    }

    @Override
    public String getSenderId() {
        return null;
    }
}
