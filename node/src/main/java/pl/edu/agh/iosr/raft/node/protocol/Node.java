package pl.edu.agh.iosr.raft.node.protocol;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pl.edu.agh.iosr.raft.node.MessageBroker;
import pl.edu.agh.iosr.raft.node.commands.*;
import pl.edu.agh.iosr.raft.node.properties.RaftProperties;
import pl.edu.agh.iosr.raft.node.protocol.messages.AppendEntriesRequest;
import pl.edu.agh.iosr.raft.node.protocol.messages.AppendEntriesResponse;
import pl.edu.agh.iosr.raft.node.protocol.messages.VoteRequest;
import pl.edu.agh.iosr.raft.node.protocol.messages.VoteResponse;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Component
@RabbitListener(queues = "${amqp.queue}")
public class Node implements CommandLineRunner {
    private static final long TIMEOUT = 3000;
    private final RaftProperties raftProperties;

    private final String nodeId;
    private final Integer nodeAmount;
    private Integer currentTerm;
    private String votedFor;
    private List<Entry> log;

    private Integer commitIndex;
    private Integer lastApplied;

    private Map<String, Integer> nextIndex;
    private Map<String, Integer> matchIndex;

    private Map<String, Boolean> grantedVotes;
    private String leaderId;
    private ReplicatedStateMachine replicatedStateMachine;
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
        this.grantedVotes = new HashMap<>();
        raftProperties.getNodes().forEach(node -> grantedVotes.put(node, false));
        this.heartbeatRunnableTask = new Runnable() {

            @Override
            public void run() {
                updateCommitIndex();
                raftProperties.getNodes().forEach(node -> sendSingleAppendEntriesRequest(node));
            }
        };
        this.electionRunnableTask = new Runnable() {
            @Override
            public void run() {
                startNewElection();
                raftProperties.getNodes().forEach(node -> sendSingleVoteRequest(node));
            }
        };
        this.messageBroker = messageBroker;
        this.electionTask = this.electionScheduler.schedule(this.electionRunnableTask, rand.nextInt(150) + TIMEOUT, TimeUnit.MILLISECONDS);
        this.heartbeatTask = heartbeatScheduler.scheduleAtFixedRate(heartbeatRunnableTask, 0, TIMEOUT / 2, TimeUnit.MILLISECONDS);
    }

    /* ========== METHODS USED BY ALL NODES ========== */

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
        Command updatedCommand = null;
        if (command.getType().equals(CommandType.PUT)) {
            Put putCommand = (Put) command;
            updatedCommand = new Put(leaderId, nodeId, putCommand.getVariableName(), putCommand.getValue());
        } else if (command.getType().equals(CommandType.INCREMENT)) {
            Increment incrementCommand = (Increment) command;
            updatedCommand = new Increment(leaderId, nodeId, incrementCommand.getVariableName());
        } else if (command.getType().equals(CommandType.DELETE)) {
            Delete deleteCommand = (Delete) command;
            updatedCommand = new Delete(leaderId, nodeId, deleteCommand.getVariableName());
        }
        handleCommand(updatedCommand);
    }


    private void handleCommand(Command command) {
        if (nodeState == NodeState.LEADER) {
            log.add(new Entry(currentTerm, command));
            raftProperties.getNodes().forEach(this::sendSingleAppendEntriesRequest);
        } else {
            System.out.println("Sending: " + command);
            messageBroker.sendMessage(command);
        }
    }


    private void executeCommands() {
        while (commitIndex > lastApplied) {
            lastApplied++;
            System.out.println("Executing command " + lastApplied);
            replicatedStateMachine.executeCommand(log.get(lastApplied).getCommand());
        }
    }


    private void startNewElection() {
        if (nodeState == NodeState.CANDIDATE || nodeState == NodeState.FOLLOWER) {
            System.out.println("Becoming candidate");
            currentTerm++;
            votedFor = nodeId;
            nodeState = NodeState.CANDIDATE;
            grantedVotes.forEach((node, bool) -> grantedVotes.put(node, false));
            this.nextIndex = new HashMap<>();
            raftProperties.getNodes().forEach(node -> nextIndex.put(node, 1));
            this.matchIndex = new HashMap<>();
            raftProperties.getNodes().forEach(node -> matchIndex.put(node, 0));
            electionTask.cancel(true);
            electionTask = electionScheduler.schedule(electionRunnableTask, rand.nextInt(150) + TIMEOUT, TimeUnit.MILLISECONDS);
        }
    }

    private void becomeFollower(int newTerm) {
        nodeState = NodeState.FOLLOWER;
        votedFor = null;
        currentTerm = newTerm;
//        grantedVotes.forEach((node, bool) -> grantedVotes.put(node, false));

        System.out.println("Becoming follower");
        electionTask.cancel(true);
        electionTask = electionScheduler.schedule(electionRunnableTask, rand.nextInt(150) + TIMEOUT, TimeUnit.MILLISECONDS);
    }

    private void becomeLeader() {

        if (nodeState == NodeState.CANDIDATE) {
            nodeState = NodeState.LEADER;
            raftProperties.getNodes().forEach(node -> nextIndex.replace(node, log.size()));
            System.out.println("Becoming leader");
            leaderId = nodeId;
            raftProperties.getNodes().forEach(this::sendSingleAppendEntriesRequest);
        }
    }

    private void sendSingleVoteRequest(String node) {
        if (nodeState == NodeState.CANDIDATE) {
            VoteRequest voteRequest = new VoteRequest(currentTerm, this.nodeId, log.size() - 1, log.get(log.size() - 1).getTerm(), node);
            System.out.println("Sending: " + voteRequest);
            messageBroker.sendMessage(voteRequest);
        }
    }

    private void sendSingleAppendEntriesRequest(String node) {
        if (nodeState == NodeState.LEADER && nextIndex.get(node) <= log.size()) {
            AppendEntriesRequest appendEntriesRequest = new AppendEntriesRequest(currentTerm, nodeId, nextIndex.get(node) - 1, log.get(nextIndex.get(node) - 1).getTerm(), Math.min(commitIndex, log.size() - 1), node);
            if (nextIndex.get(node) < log.size()) {
                appendEntriesRequest.setEntries(log.subList(nextIndex.get(node), log.size()));
            }
            System.out.println("Sending: " + appendEntriesRequest);
            log.forEach(System.out::println);
            messageBroker.sendMessage(appendEntriesRequest);

        }

    }

    private void updateCommitIndex() {
        if (nodeState == NodeState.LEADER) {
            long N = matchIndex.values().stream().filter(value -> value >= commitIndex).count() + 1;
            while (N > nodeAmount / 2 && (log.get(commitIndex + 1).getTerm() == currentTerm || commitIndex == 0)) {
                commitIndex++;

                N = matchIndex.values().stream().filter(value -> value >= commitIndex).count() + 1;
            }
        }
    }

    private void handleVoteRequest(VoteRequest request) {
        boolean voteGranted = false;
        if (currentTerm < request.getTerm()) {
            becomeFollower(request.getTerm());
        }

        if (currentTerm.equals(request.getTerm()) && (votedFor == null || votedFor.equals(request.getCandidateId())) && request.getLastLogIndex() >= log.size() - 1) {
            votedFor = request.getCandidateId();
            voteGranted = true;
            electionTask.cancel(true);
            this.electionTask = this.electionScheduler.schedule(this.electionRunnableTask, rand.nextInt(150) + TIMEOUT, TimeUnit.MILLISECONDS);
        }
        messageBroker.sendMessage(new VoteResponse(currentTerm, voteGranted, request.getCandidateId(), nodeId));
    }


    private void handleVoteResponse(VoteResponse response) {
        if (currentTerm < response.getTerm()) {
            becomeFollower(response.getTerm());
        }
        if (nodeState == NodeState.CANDIDATE && currentTerm.equals(response.getTerm())) {
            grantedVotes.put(response.getSenderId(), response.getVoteGranted());
            if (grantedVotes.values().stream().filter(bool -> bool).count() + 1 > nodeAmount / 2) {
                becomeLeader();
            }
        }
    }

    private void handleAppendEntriesRequest(AppendEntriesRequest request) {
        boolean success = false;
        int matchIndex = 0;
        if (currentTerm < request.getTerm()) {
            leaderId = request.getLeaderId();
            becomeFollower(request.getTerm());
        }
        if (currentTerm.equals(request.getTerm())) {
            leaderId = request.getLeaderId();
            nodeState = NodeState.FOLLOWER;
            electionTask.cancel(true);
            electionTask = electionScheduler.schedule(electionRunnableTask, rand.nextInt(150) + TIMEOUT, TimeUnit.MILLISECONDS);
            if (request.getPrevLogIndex() == 0 || (request.getPrevLogIndex() < log.size() && log.get(request.getPrevLogIndex()).getTerm() == request.getPrevLogTerm())) {
                success = true;
                int index = request.getPrevLogIndex();
                if (request.getEntries() != null) {
                    for (int i = 0; i < request.getEntries().size(); i++) {
                        index++;
                        if (log.size() > index && log.get(index).getTerm() != request.getEntries().get(i).getTerm()) {
                            while (log.size() > index - 1) {
                                log.remove(log.size() - 1);
                            }
                            log.add(request.getEntries().get(i));
                        }
                        if (log.size() <= index) {
                            log.add(request.getEntries().get(i));
                        }
                    }
                }
                matchIndex = index;
                commitIndex = Math.max(commitIndex, request.getLeaderCommit());
            }
            log.forEach(System.out::println);

        }
        AppendEntriesResponse appendEntriesResponse = new AppendEntriesResponse(currentTerm, success, request.getLeaderId(), nodeId, matchIndex);
        System.out.println("Sending: " + appendEntriesResponse);
        executeCommands();
        messageBroker.sendMessage(appendEntriesResponse);
    }

    private void handleAppendEntriesResponse(AppendEntriesResponse response) {
        if (currentTerm < response.getTerm()) {
            leaderId = response.getLeaderId();
            becomeFollower(response.getTerm());
        }
        if (nodeState == NodeState.LEADER && currentTerm.equals(response.getTerm())) {
            if (response.isSuccess()) {
                matchIndex.replace(response.getSenderId(), Math.max(matchIndex.get(response.getSenderId()), response.getMatchIndex()));
                nextIndex.replace(response.getSenderId(), matchIndex.get(response.getSenderId()) + 1);
            } else {
                nextIndex.replace(response.getSenderId(), Math.max(1, nextIndex.get(response.getSenderId()) - 1));
            }
        }
        executeCommands();
    }

    @Override
    public void run(String... args) throws Exception {
        int i = 0;
        while (i < 4) {
            Scanner sc = new Scanner(System.in);
            i = sc.nextInt();
            Command cmd;
            if (i == 1) {
                cmd = new Put(nodeId, nodeId, "x", 1);
                System.out.println("Sending " + cmd);
                messageBroker.sendMessage(cmd);
            } else if (i == 2) {
                cmd = new Increment(nodeId, nodeId, "x");
                System.out.println("Sending " + cmd);
                messageBroker.sendMessage(cmd);
            } else if (i == 3) {
                cmd = new Delete(nodeId, nodeId, "x");
                System.out.println("Sending " + cmd);
                messageBroker.sendMessage(cmd);
            }

        }
    }
}