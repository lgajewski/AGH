package pl.edu.agh.iosr.raft.node.commands;



import com.fasterxml.jackson.annotation.JsonTypeName;

import java.io.Serializable;

@JsonTypeName("put")
public class Put implements Command, Serializable {
    private String variableName;
    private Integer value;
    private String routingKey;
    private String senderId;
    private CommandType type;

    public Put(){}

    public Put(String sendTo, String senderId, String variableName, Integer value){
        this.routingKey = sendTo;
        this.senderId = senderId;
        this.variableName = variableName;
        this.value = value;
        this.type = CommandType.PUT;
    }

    @Override
    public CommandType getType() {
        return CommandType.PUT;
    }

    @Override
    public String getVariableName(){
        return variableName;
    }

    public Integer getValue(){
        return value;
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
        return "Put = {" +
                "routingKey:" + routingKey +
                ", senderId:" + senderId +
                ", variableName:" + variableName +
                ", value:" + value +
                "}";
    }

    public void setType(CommandType type) {
        this.type = type;
    }
}
