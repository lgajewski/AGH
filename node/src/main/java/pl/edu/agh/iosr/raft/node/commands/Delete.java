package pl.edu.agh.iosr.raft.node.commands;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import pl.edu.agh.iosr.raft.node.Message;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
public class Delete implements Command, Message{
    private String variableName;
    private String routingKey;
    private String senderId;

    public Delete(String sendTo, String senderId){
        this.routingKey = sendTo;
        this.senderId = senderId;
    }

    @Override
    public CommandType getType() {
        return CommandType.DELETE;
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
        return senderId;
    }
}
