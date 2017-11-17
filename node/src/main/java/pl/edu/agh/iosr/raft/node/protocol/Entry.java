package pl.edu.agh.iosr.raft.node.protocol;

import pl.edu.agh.iosr.raft.node.commands.Command;

public class Entry {
    private int term;
    private Command command;

    public Entry(int term, Command command){
        this.term = term;
        this.command = command;
    }

    public int getTerm() {
        return term;
    }

    public Command getCommand() {
        return command;
    }
}
