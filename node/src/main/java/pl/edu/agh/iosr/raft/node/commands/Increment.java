package pl.edu.agh.iosr.raft.node.commands;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.io.Serializable;

@JsonTypeName("increment")
public class Increment implements Command, Serializable {
    private String variableName;
    private String routingKey;
    private String senderId;

    public Increment(){}

    public Increment(String sendTo, String senderId, String variableName){
        this.routingKey = sendTo;
        this.senderId = senderId;
        this.variableName = variableName;
    }
    @Override
    public CommandType getType() {
        return CommandType.INCREMENT;
    }

    @Override
    public String getVariableName() {
        return variableName;
    }

    @Override
    public String getRoutingKey() {
        return routingKey;
    }

    @Override
    public String getSenderId() {
        return null;
    }

    @Override
    public String toString() {
        return "Increment = {" +
                "routingKey:" + routingKey +
                ", senderId:" + senderId +
                ", variableName:" + variableName +
                "}";
    }
}
