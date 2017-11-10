package pl.edu.agh.iosr.raft.node.protocol;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import pl.edu.agh.iosr.raft.node.AMQPConfiguration;
import pl.edu.agh.iosr.raft.node.Application;
import pl.edu.agh.iosr.raft.node.protocol.messages.AppendEntriesRequest;
import pl.edu.agh.iosr.raft.node.protocol.messages.AppendEntriesResponse;
import pl.edu.agh.iosr.raft.node.protocol.messages.VoteRequest;
import pl.edu.agh.iosr.raft.node.protocol.messages.VoteResponse;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

@Component
public class Node {


    //PERSISTENT STATE
    private final Integer nodeId;
    private final Integer nodeAmount;
    private Integer currentTerm;
    private Integer votedFor;
    private List<Entry> log;

    //VOLATILE STATE?
    private Integer commitIndex;
    private Integer lastApplied;

    //LEADER ONLY
    private Map<Integer, Integer> nextIndex;
    private Map<Integer, Integer> matchIndex;

    private Integer leaderId;
    private ReplicatedStateMachine replicatedStateMachine;
    private Integer grantedVotes;
    private ScheduledFuture<?> electionTask;
    private ScheduledFuture<?> heartbeatTask;
    private NodeState nodeState;
    private static final ObjectMapper mapper = new ObjectMapper();
    private ScheduledExecutorService electionScheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledExecutorService heartbeatScheduler = Executors.newSingleThreadScheduledExecutor();
    private final Random rand = new Random();;
    private Runnable electionRunnableTask;
    private Runnable heartbeatRunnableTask;
    private RabbitTemplate rabbitTemplate;


    public Node(RabbitTemplate rabbitTemplate) {
        this.nodeId = Application.nodeId;
        this.nodeAmount = Application.nodeAmount;
        this.nodeState = NodeState.FOLLOWER;
        this.currentTerm = 0;
        this.votedFor = -1;
        this.log = new ArrayList<>();
        this.commitIndex = 0;
        this.lastApplied = 0;
        this.nextIndex = new HashMap<>();
        this.matchIndex = new HashMap<>();
        this.leaderId = null;
        this.replicatedStateMachine = new ReplicatedStateMachine();
        this.grantedVotes = 0;
        this.heartbeatRunnableTask = new Runnable(){

            @Override
            public void run() {
                sendHeartbeat();
            }
        };
        this.electionRunnableTask = new Runnable() {
            @Override
            public void run() {
                becomeCandidate();
            }
        };
        this.rabbitTemplate = rabbitTemplate;
        this.electionTask = this.electionScheduler.schedule(this.electionRunnableTask, rand.nextInt(150)+150, TimeUnit.MILLISECONDS);
    }


    public String sendMessage(){
        AppendEntriesRequest appendEntriesRequest = new AppendEntriesRequest(this.currentTerm, this.nodeId);
        appendEntriesRequest.setLeaderCommit(this.commitIndex);
        appendEntriesRequest.setPrevLogIndex(this.lastApplied + 1);
        appendEntriesRequest.setPrevLogTerm(this.log.get(this.lastApplied - 1).getTerm());
        appendEntriesRequest.setEntries(this.log.subList(this.lastApplied + 1, this.log.size()));
        String json = null;
        try {
            json = mapper.writeValueAsString(appendEntriesRequest);
            System.out.println(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    public void receiveMessage(String json){

        try {
            System.out.println(json);
            String className = mapper.readTree(json).findValue("type").asText();
            if(className.equalsIgnoreCase("AppendEntriesRequest")){
                handleAppendEntriesRequest(mapper.readValue(json, AppendEntriesRequest.class));
            } else if(className.equalsIgnoreCase("AppendEntriesResponse")){
                handleAppendEntriesResponse(1, mapper.readValue(json, AppendEntriesResponse.class));
            } else if(className.equalsIgnoreCase("VoteRequest")){
                handleVoteRequest(mapper.readValue(json, VoteRequest.class));
            } else if(className.equalsIgnoreCase("VoteResponse")){
                handleVoteResponse(mapper.readValue(json, VoteResponse.class));
            } else {
                handleCommand(mapper.readValue(json, String.class));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }




    private void handleAppendEntriesRequest(AppendEntriesRequest appendEntriesRequest) {
        System.out.println(appendEntriesRequest.getTerm());
        if (appendEntriesRequest.getTerm() > this.currentTerm) {
            rabbitTemplate.convertAndSend(AMQPConfiguration.getRoutingKey(appendEntriesRequest.getLeaderId()), new AppendEntriesResponse(this.currentTerm, false));
            return;
        }
        int index = appendEntriesRequest.getPrevLogIndex();
        if (this.log.get(index) == null || this.log.get(index).getTerm() != appendEntriesRequest.getPrevLogTerm()) {
            rabbitTemplate.convertAndSend(AMQPConfiguration.getRoutingKey(appendEntriesRequest.getLeaderId()), new AppendEntriesResponse(this.currentTerm, false));
            return;
        }

        int firstBrokenIndex = -1;
        List<Entry> newEntries = appendEntriesRequest.getEntries();
        for (int i = 1; i <= newEntries.size(); i++) {
            if (newEntries.get(i).getTerm() != this.log.get(index + i).getTerm()) {
                firstBrokenIndex = i;
                break;
            }
        }
        if (firstBrokenIndex > -1) {
            for (int i = firstBrokenIndex; i < newEntries.size(); i++) {
                if (this.log.get(index + i) == null) {
                    this.log.add(index + i, newEntries.get(i));
                } else {
                    this.log.set(index + i, newEntries.get(i));
                }
            }
        }
        if (appendEntriesRequest.getLeaderCommit() > this.commitIndex) {
            this.commitIndex = Math.min(appendEntriesRequest.getLeaderCommit(), this.log.size() - 1);
        }
        rabbitTemplate.convertAndSend(AMQPConfiguration.getRoutingKey(appendEntriesRequest.getLeaderId()), new AppendEntriesResponse(this.currentTerm, true));

    }

    private void handleVoteRequest(VoteRequest voteRequest) {
        if (voteRequest.getTerm() < this.currentTerm) {
            rabbitTemplate.convertAndSend(AMQPConfiguration.getRoutingKey(voteRequest.getCandidateId()), new VoteResponse(this.currentTerm, false));
            return;
        }
        if ((this.votedFor == null || this.votedFor.equals(voteRequest.getCandidateId())) &&
                this.log.get(this.log.size() - 1).getTerm() == voteRequest.getLastLogTerm() &&
                this.log.size() == voteRequest.getLastLogIndex()) {
            rabbitTemplate.convertAndSend(AMQPConfiguration.getRoutingKey(voteRequest.getCandidateId()), new VoteResponse(this.currentTerm, true));
        }
        rabbitTemplate.convertAndSend(AMQPConfiguration.getRoutingKey(voteRequest.getCandidateId()), new VoteResponse(this.currentTerm, false));
    }

    private void handleAppendEntriesResponse(int nodeId, AppendEntriesResponse appendEntriesResponse) {
        matchIndex.replace(nodeId, appendEntriesResponse.getTerm());
    }

    private void handleVoteResponse(VoteResponse voteResponse) {

    }

    private void handleCommand(String command){
        if(this.nodeId.equals(this.leaderId)){
            this.log.add(new Entry(this.currentTerm, command));

            //TODO respond after entry applied to state machine
        } else {
            rabbitTemplate.convertAndSend(AMQPConfiguration.getRoutingKey(this.leaderId), command);
        }
    }





    private void executeCommands() {
        System.out.println("Executing commands");

        while (this.commitIndex > this.lastApplied) {
            this.lastApplied++;
            replicatedStateMachine.executeCommand(this.log.get(this.lastApplied).getCommand());
        }
    }

//    private void updateTerms(int term) {
//        if (term > this.currentTerm) {
//            this.currentTerm = term;
//            this.votedFor = null;
//        }
//    }

    private void becomeFollower() {
        System.out.println("Becoming follower");
        if(this.nodeState == NodeState.LEADER){
            this.heartbeatTask.cancel(false);
        }
        this.nodeState = NodeState.FOLLOWER;
    }

    private void becomeCandidate() {
        System.out.println("Becoming a candidate");
        if(this.nodeState == NodeState.LEADER){
            this.heartbeatTask.cancel(false);
        }
        this.nodeState = NodeState.CANDIDATE;

        //start election
        this.currentTerm++;
        this.votedFor = this.nodeId;
        this.electionScheduler = Executors.newSingleThreadScheduledExecutor();
        this.electionTask = this.electionScheduler.schedule(this.electionRunnableTask, rand.nextInt(150)+150, TimeUnit.MILLISECONDS);
        VoteRequest voteRequest = new VoteRequest(this.currentTerm, this.nodeId, this.log.size() - 1, this.log.get(this.log.size() - 1).getTerm());
//        TODO: send VoteRequest to all other servers VoteRequest(this.currentTerm, this.nodeId, this.log.size()-1, this.log.get(this.log.size()-1).getTerm())
    }

    private void becomeLeader() {
        System.out.println("Becoming a leader");
        this.nodeState = NodeState.LEADER;
        this.heartbeatTask = this.heartbeatScheduler.scheduleAtFixedRate(this.heartbeatRunnableTask, 0, 150, TimeUnit.MILLISECONDS);

    }

    private void sendAppendEntriesRequest() {
        System.out.println("sendAppendEntriesRequest");
        //TODO fix indices
        AppendEntriesRequest appendEntriesRequest = new AppendEntriesRequest(this.currentTerm, this.nodeId);
        appendEntriesRequest.setLeaderCommit(this.commitIndex);
        appendEntriesRequest.setPrevLogIndex(this.lastApplied + 1);
        appendEntriesRequest.setPrevLogTerm(this.log.get(this.lastApplied - 1).getTerm());
        appendEntriesRequest.setEntries(this.log.subList(this.lastApplied + 1, this.log.size() - 1));
        for(int i=1;i< this.nodeAmount;i++){
            if(i != this.nodeId){
                rabbitTemplate.convertAndSend(AMQPConfiguration.getRoutingKey(i), appendEntriesRequest);
            }
        }
    }

    private void sendHeartbeat(){
        System.out.println("sendHeartbeat");
        AppendEntriesRequest appendEntriesRequest = new AppendEntriesRequest(this.currentTerm, this.nodeId);
        appendEntriesRequest.setLeaderCommit(this.commitIndex);
        for(int i=1;i< this.nodeAmount;i++){
            if(i != this.nodeId){
                rabbitTemplate.convertAndSend(AMQPConfiguration.getRoutingKey(i), appendEntriesRequest);
            }
        }
    }

    private void sendVoteRequest(){
        System.out.println("sendVoteRequest");
        VoteRequest voteRequest = new VoteRequest(this.currentTerm, this.nodeId, this.log.size()-1, this.log.get(this.log.size() - 1).getTerm());
        for(int i=1;i< this.nodeAmount;i++){
            if(i != this.nodeId){
                rabbitTemplate.convertAndSend(AMQPConfiguration.getRoutingKey(i), voteRequest);
            }
        }
    }
}
