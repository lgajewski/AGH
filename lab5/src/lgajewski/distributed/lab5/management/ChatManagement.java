package lgajewski.distributed.lab5.management;

import lgajewski.distributed.lab5.protos.ChatOperationProtos;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.util.Util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static lgajewski.distributed.lab5.protos.ChatOperationProtos.ChatAction;
import static lgajewski.distributed.lab5.protos.ChatOperationProtos.ChatState;

public class ChatManagement extends ReceiverAdapter {

    private static final String TAG = ChatManagement.class.getSimpleName();

    private static final String CLUSTER_NAME = "ChatManagement321123";

    private final JChannel channel;

    private final Object stateLock = new Object();
    private ChatState state;

    public ChatManagement() throws Exception {
        channel = new JChannel();
        channel.setReceiver(this);
        state = ChatState.newBuilder().build();
    }

    public void viewAccepted(View new_view) {
        System.out.println("** view: " + new_view);
    }

    public void receive(Message msg) {
        System.out.println("[" + TAG + "] receive - " + msg.getSrc() + ": " + msg.getObject());

        ChatOperationProtos.ChatAction chatAction = (ChatAction) msg.getObject();
        synchronized (stateLock) {
            state = state.toBuilder().addState(chatAction).build();
        }
    }

    public void getState(OutputStream output) throws Exception {
        synchronized (stateLock) {
            Util.objectToStream(state, new DataOutputStream(output));
        }
    }

    public void setState(InputStream input) throws Exception {

        synchronized (stateLock) {
            state = (ChatState) Util.objectFromStream(new DataInputStream(input));

            System.out.println("[" + TAG + "] received state, count: " + state.getStateCount());
        }
    }


    public void connect() throws Exception {
        channel.connect(CLUSTER_NAME);
        channel.getState(null, 10000);
    }

    public void close() {
        channel.close();
    }

    public List<Room> getRooms() {
        Map<String, Room> rooms = new HashMap<>();

        if (state.getStateList() != null) {
            for (ChatAction chatAction : state.getStateList()) {
                ChatAction.ActionType actionType = chatAction.getAction();
                String channel = chatAction.getChannel();
                String member = chatAction.getNickname();

                Room room = rooms.containsKey(channel) ? rooms.get(channel) : new Room(channel);

                switch (actionType) {
                    case JOIN:
                        room.addMember(member);
                        break;
                    case LEAVE:
                        room.removeMember(member);
                        break;
                }

                rooms.put(channel, room);
            }
        }

        return new ArrayList<>(rooms.values());
    }

    public void join(String username, int roomId) throws Exception {
        Message msg = new Message(null, null, createChatAction(username, roomId, ChatAction.ActionType.JOIN));
        channel.send(msg);
    }

    public void leave(String username, int roomId) throws Exception {
        Message msg = new Message(null, null, createChatAction(username, roomId, ChatAction.ActionType.LEAVE));
        channel.send(msg);
    }

    private ChatAction createChatAction(String username, int roomId, ChatAction.ActionType actionType) {
        return ChatAction.newBuilder()
                .setNickname(username)
                .setChannel(String.valueOf(roomId))
                .setAction(actionType).build();
    }
}
