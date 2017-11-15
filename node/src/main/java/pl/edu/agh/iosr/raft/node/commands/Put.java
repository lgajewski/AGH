package pl.edu.agh.iosr.raft.node.commands;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
public class Put implements Command {
    private String variableName;
    private Integer value;
    private String routingKey;
    private String senderId;

    public Put(String sendTo, String senderId){
        this.routingKey = sendTo;
        this.senderId = senderId;
    }

    @Override
    public CommandType getType() {
        return CommandType.PUT;
    }

    @Override
    public String getVariableName(){
        return "x";
    }

    public Integer getValue(){
        return 1;
    }

    @Override
    public String getRoutingKey() {
        return routingKey;
    }

    @Override
    public String getSenderId() {
        return senderId;
    }
}
