package pl.edu.agh.iosr.raft.node.commands;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import pl.edu.agh.iosr.raft.node.Message;

@JsonTypeInfo(use=JsonTypeInfo.Id.NAME,
        include=JsonTypeInfo.As.PROPERTY,
        property="name")
@JsonSubTypes({
        @JsonSubTypes.Type(value=Delete.class, name="delete"),
        @JsonSubTypes.Type(value=Increment.class, name="increment"),
        @JsonSubTypes.Type(value=Put.class, name="put"),
})
public interface Command extends Message{
    CommandType getType();
    String getVariableName();
}
