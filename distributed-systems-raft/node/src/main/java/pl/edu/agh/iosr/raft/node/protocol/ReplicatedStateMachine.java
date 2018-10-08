package pl.edu.agh.iosr.raft.node.protocol;

import pl.edu.agh.iosr.raft.node.commands.*;

import java.util.HashMap;

public class ReplicatedStateMachine {
    private HashMap<String, Integer> state;

    public ReplicatedStateMachine(){
        this.state = new HashMap<>();
    }

    public void executeCommand(Command command){
        System.out.println("Exectuting: " + command);
        String key = command.getVariableName();

        if(command.getType().equals(CommandType.PUT)){
            Put putCommand = (Put)command;
            this.state.put(putCommand.getVariableName(), putCommand.getValue());
        }
        else if (command.getType().equals(CommandType.INCREMENT)){
            this.state.put(key, this.state.get(command.getVariableName()));
        }
        else if (command.getType().equals(CommandType.DELETE)){
            this.state.remove(key);
        }
        this.state.forEach((entryKey, value) -> System.out.println(entryKey + ", " + value));
    }
}
