package pl.edu.agh.iosr.raft.node.protocol;

import java.util.LinkedList;
import java.util.List;

public class ReplicatedStateMachine {
    private List<String> state;

    public ReplicatedStateMachine(){
        this.state = new LinkedList<>();
    }

    public void executeCommand(String command){
        this.state.add(command);
    }
}
