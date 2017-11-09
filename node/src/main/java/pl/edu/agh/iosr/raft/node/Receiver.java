package pl.edu.agh.iosr.raft.node;

import org.springframework.stereotype.Component;

@Component
public class Receiver {

    public void receive(String command) {
        System.out.println("Received <" + command + ">");
    }

}