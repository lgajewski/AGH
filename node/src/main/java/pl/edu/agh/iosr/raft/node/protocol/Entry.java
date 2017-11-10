package pl.edu.agh.iosr.raft.node.protocol;

public class Entry {
    private int term;
    private String command;

    public Entry(int term, String command){
        this.term = term;
        this.command = command;
    }

    public int getTerm() {
        return term;
    }

    public String getCommand() {
        return command;
    }
}
