package pl.edu.agh.iosr.raft.node.commands;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.io.Serializable;

@JsonTypeName("delete")
public class Delete implements Command, Serializable {
    private String variableName;
    private String routingKey;
    private String senderId;

    public Delete(){}

    public Delete(String sendTo, String senderId, String variableName){
        this.routingKey = sendTo;
        this.senderId = senderId;
        this.variableName = variableName;
    }

    @Override
    public CommandType getType() {
        return CommandType.DELETE;
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
        return senderId;
    }


    @Override
    public String toString() {
        return "Delete = {" +
                "routingKey:" + routingKey +
                ", senderId:" + senderId +
                ", variableName:" + variableName +
                "}";
    }
}
