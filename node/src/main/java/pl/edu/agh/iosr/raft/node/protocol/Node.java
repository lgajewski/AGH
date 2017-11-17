package pl.edu.agh.iosr.raft.node.protocol;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.edu.agh.iosr.raft.node.MessageBroker;
import pl.edu.agh.iosr.raft.node.commands.Command;
import pl.edu.agh.iosr.raft.node.properties.RaftProperties;
import pl.edu.agh.iosr.raft.node.protocol.messages.AppendEntriesRequest;
import pl.edu.agh.iosr.raft.node.protocol.messages.AppendEntriesResponse;
import pl.edu.agh.iosr.raft.node.protocol.messages.VoteRequest;
import pl.edu.agh.iosr.raft.node.protocol.messages.VoteResponse;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Component
@RabbitListener(queues = "${amqp.queue}")
public class Node {

    private final RaftProperties raftProperties;

    //PERSISTENT STATE
    private final String nodeId;
    private final Integer nodeAmount;
    private Integer currentTerm;
    private String votedFor;
    private List<Entry> log;

    //VOLATILE STATE?
    private Integer commitIndex;
    private Integer lastApplied;

    //LEADER ONLY
    private Map<String, Integer> nextIndex;
    private Map<String, Integer> matchIndex;

    private String leaderId;
    private ReplicatedStateMachine replicatedStateMachine;
    private Integer grantedVotes;
    private ScheduledFuture<?> electionTask;
    private ScheduledFuture<?> heartbeatTask;
    private NodeState nodeState;
    private ScheduledExecutorService electionScheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledExecutorService heartbeatScheduler = Executors.newSingleThreadScheduledExecutor();
    private final Random rand = new Random();

    private Runnable electionRunnableTask;
    private Runnable heartbeatRunnableTask;
    private MessageBroker messageBroker;

    @Autowired
    public Node(MessageBroker messageBroker, RaftProperties raftProperties) {
        this.nodeId = raftProperties.getName();
        this.nodeAmount = raftProperties.getNodes().size() + 1;
        this.raftProperties = raftProperties;
        this.nodeState = NodeState.FOLLOWER;
        this.currentTerm = 1;
        this.votedFor = null;
        this.log = new ArrayList<>();
        this.log.add(new Entry(0, null));
        this.commitIndex = 0;
        this.lastApplied = 0;
        this.nextIndex = new HashMap<>();
        raftProperties.getNodes().forEach(node -> nextIndex.put(node, log.size()));
        this.matchIndex = new HashMap<>();
        raftProperties.getNodes().forEach(node -> matchIndex.put(node, 0));
        this.leaderId = null;
        this.replicatedStateMachine = new ReplicatedStateMachine();
        this.grantedVotes = 0;
        this.heartbeatRunnableTask = new Runnable() {

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
        this.messageBroker = messageBroker;
        this.electionTask = this.electionScheduler.schedule(this.electionRunnableTask, rand.nextInt(150) + 1150, TimeUnit.MILLISECONDS);
    }

    /* ========== METHODS USED BY ALL NODES ========== */

//    @RabbitListener(queues = "${amqp.queue}")
//    public void receivedMessage(Object message) {
//        System.out.println(message.getClass());
//        System.out.println(VoteRequest.class.isAssignableFrom(message.getClass()));
//        if(message instanceof VoteRequest){
//            handleVoteRequest((VoteRequest)message);
//        } else if(message instanceof VoteResponse){
//            handleVoteResponse((VoteResponse)message);
//        } else if(message instanceof AppendEntriesRequest){
//            handleAppendEntriesRequest((AppendEntriesRequest)message);
//        } else if(message instanceof AppendEntriesResponse){
//            handleAppendEntriesResponse((AppendEntriesResponse)message);
//        } else if(message instanceof Command){
//            handleCommand((Command)message);
//        }
//    }
    @RabbitHandler
    public void receivedMessage(final AppendEntriesRequest appendEntriesRequest) {
        System.out.println("Received " + appendEntriesRequest);
        handleAppendEntriesRequest(appendEntriesRequest);
    }

    @RabbitHandler
    public void receivedMessage(final AppendEntriesResponse appendEntriesResponse) {
        System.out.println("Received " + appendEntriesResponse);
        handleAppendEntriesResponse(appendEntriesResponse);
    }

    @RabbitHandler
    public void receivedMessage(final VoteRequest voteRequest) {
        System.out.println("Received " + voteRequest);
        handleVoteRequest(voteRequest);
    }

    @RabbitHandler
    public void receivedMessage(final VoteResponse voteResponse) {
        System.out.println("Received " + voteResponse);
        handleVoteResponse(voteResponse);
    }

    @RabbitHandler
    public void receivedMessage(final Command command) {
        System.out.println("Received " + command);
        handleCommand(command);
    }

    private void handleCommand(Command command) {
        if (nodeId.equals(leaderId)) {
            log.add(new Entry(currentTerm, command));
            sendAppendEntriesRequest();
            //TODO respond after entry applied to state machine
        } else {
            messageBroker.sendMessage(command);
        }
    }

    private void handleAppendEntriesRequest(AppendEntriesRequest appendEntriesRequest) {
        if (appendEntriesRequest.getTerm() < currentTerm) {
            AppendEntriesResponse appendEntriesResponse = new AppendEntriesResponse(currentTerm, false, appendEntriesRequest.getLeaderId(), nodeId);
            System.out.println("Sending: " + appendEntriesResponse);
            messageBroker.sendMessage(appendEntriesResponse);
            return;
        }
        Integer index = appendEntriesRequest.getPrevLogIndex();

        if (log.get(index) == null || log.get(index).getTerm() != appendEntriesRequest.getPrevLogTerm()) {
            AppendEntriesResponse appendEntriesResponse = new AppendEntriesResponse(currentTerm, false, appendEntriesRequest.getLeaderId(), nodeId);
            System.out.println("Sending: " + appendEntriesResponse);
            messageBroker.sendMessage(appendEntriesResponse);
            return;
        }

        int firstBrokenIndex = -1;
        List<Entry> newEntries = appendEntriesRequest.getEntries();
        if (newEntries != null) {
            for (int i = 0; i <= newEntries.size() - 1; i++) {
                if (newEntries.get(i).getTerm() != log.get(index + i + 1).getTerm()) {
                    firstBrokenIndex = i;
                    break;
                }
            }
            if (firstBrokenIndex > -1) {
                for (int i = firstBrokenIndex; i < newEntries.size(); i++) {
                    if (log.get(index + i + 1) == null) {
                        log.add(index + i + 1, newEntries.get(i));
                    } else {
                        log.set(index + i + 1, newEntries.get(i));
                    }
                }
            }
        }
        if (appendEntriesRequest.getLeaderCommit() > commitIndex) {
            commitIndex = Math.min(appendEntriesRequest.getLeaderCommit(), log.size() - 1);
        }
        if (nodeState == NodeState.CANDIDATE) {
            leaderId = appendEntriesRequest.getLeaderId();
            becomeFollower();
        }
        AppendEntriesResponse appendEntriesResponse = new AppendEntriesResponse(currentTerm, true, appendEntriesRequest.getLeaderId(), nodeId);
        System.out.println("Sending: " + appendEntriesResponse);
        messageBroker.sendMessage(appendEntriesResponse);
    }

    private void handleVoteRequest(VoteRequest voteRequest) {
        System.out.println("Received: " + voteRequest);
        VoteResponse voteResponse = new VoteResponse(currentTerm, false, voteRequest.getCandidateId(), nodeId);
        VoteResponse voteResponse2 = new VoteResponse(currentTerm, true, voteRequest.getCandidateId(), nodeId);
        if (voteRequest.getTerm() < currentTerm) {
            System.out.println("Sending: " + voteResponse);
            messageBroker.sendMessage(voteResponse);
            return;
        }
        if ((votedFor == null || votedFor.equals(voteRequest.getCandidateId())) &&
                log.get(log.size() - 1).getTerm() == voteRequest.getLastLogTerm() &&
                log.size() - 1 == voteRequest.getLastLogIndex()) {
            System.out.println("Sending: " + voteResponse2);
            messageBroker.sendMessage(voteResponse2);
        }
        System.out.println("Sending: " + voteResponse);
        messageBroker.sendMessage(voteResponse);
    }

    private void executeCommands() {
        System.out.println("Executing commands");

        while (commitIndex > lastApplied) {
            lastApplied++;
            replicatedStateMachine.executeCommand(log.get(lastApplied).getCommand());
        }
    }

    private void becomeCandidate() {
        System.out.println("Becoming a candidate");
        if (nodeState == NodeState.LEADER) {
            heartbeatTask.cancel(false);
        }
        nodeState = NodeState.CANDIDATE;

        //start election
        currentTerm++;
        grantedVotes++;
        votedFor = nodeId;
        electionScheduler = Executors.newSingleThreadScheduledExecutor();
        electionTask = electionScheduler.schedule(electionRunnableTask, rand.nextInt(150) + 1150, TimeUnit.MILLISECONDS);
        sendVoteRequests();
    }

    /* ========== METHODS USED BY CANDIDATES ========== */
    private void becomeLeader() {
        electionTask.cancel(true);
//        heartbeatTask.cancel(true);
        System.out.println("Becoming a leader");
        nodeState = NodeState.LEADER;
        leaderId = nodeId;
        heartbeatTask = heartbeatScheduler.scheduleAtFixedRate(heartbeatRunnableTask, 0, 700, TimeUnit.MILLISECONDS);

    }

    private void becomeFollower() {
        grantedVotes = 0;
        electionTask.cancel(true);
        electionTask = electionScheduler.schedule(electionRunnableTask, rand.nextInt(150) + 1500, TimeUnit.MILLISECONDS);
        System.out.println("Becoming follower");
        if (nodeState == NodeState.LEADER) {
            heartbeatTask.cancel(false);
        }
        nodeState = NodeState.FOLLOWER;
    }

    private void sendVoteRequests() {
        System.out.println("sendVoteRequests");
        raftProperties.getNodes().forEach(node -> {
            VoteRequest voteRequest = new VoteRequest(currentTerm, nodeId, log.size() - 1, log.get(log.size() - 1).getTerm(), node);
            System.out.println("Sending: " + voteRequest);
            messageBroker.sendMessage(voteRequest);
        });
    }

    private void handleVoteResponse(VoteResponse voteResponse) {
        if (voteResponse.getVoteGranted() && voteResponse.getTerm().equals(currentTerm)) {
            grantedVotes++;
        }
        if (grantedVotes > nodeAmount / 2) {
            becomeLeader();
        }
    }

    /* ========== METHODS USED BY LEADER ========== */
    private void sendHeartbeat() {
        raftProperties.getNodes().forEach(node -> {
            AppendEntriesRequest appendEntriesRequest = new AppendEntriesRequest(currentTerm, nodeId, nextIndex.get(node) - 1, log.get(nextIndex.get(node) - 1).getTerm(), commitIndex, node);
            System.out.println("Sending heartbeat: " + appendEntriesRequest);
            messageBroker.sendMessage(appendEntriesRequest);
        });
    }

    private void sendAppendEntriesRequest() {
        raftProperties.getNodes().forEach(node -> {
            AppendEntriesRequest appendEntriesRequest = new AppendEntriesRequest(currentTerm, nodeId, nextIndex.get(node) - 1, log.get(nextIndex.get(node) - 1).getTerm(), commitIndex, node);
            appendEntriesRequest.setEntries(log.subList(nextIndex.get(node), log.size() - 1));
            System.out.println("Sending: " + appendEntriesRequest);
            messageBroker.sendMessage(appendEntriesRequest);
        });
    }

    private void handleAppendEntriesResponse(AppendEntriesResponse appendEntriesResponse) {
        matchIndex.replace(appendEntriesResponse.getSenderId(), appendEntriesResponse.getTerm());
    }
}