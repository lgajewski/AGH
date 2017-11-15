package pl.edu.agh.iosr.raft.node.commands;

import pl.edu.agh.iosr.raft.node.Message;

public interface Command extends Message{
    CommandType getType();
    String getVariableName();
}
