package lgajewski.distributed.lab5.chat;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.stack.ProtocolStack;

public class SimpleChat extends ReceiverAdapter {

    private final String username;
    private final int roomId;

    private JChannel channel;

    public SimpleChat(String username, int roomId) throws Exception {
        this.username = username;
        this.roomId = roomId;

        ProtocolStack stack = Properties.getProtocolStack(roomId);

        channel = new JChannel();
        channel.setReceiver(this);
        channel.setProtocolStack(stack);
        stack.init();
    }

    public void viewAccepted(View new_view) {
        System.out.println("** view: " + new_view);

        System.out.println();
        System.out.flush();

    }

    public void send(Message msg) throws Exception {
        channel.send(msg);
    }

    public void receive(Message msg) {
        String line = msg.getSrc() + ": " + msg.getObject();

        System.out.println(line);

        System.out.print("#" + roomId + " > ");
        System.out.flush();
    }


    public void connect() throws Exception {
        channel.connect(String.valueOf(roomId));
    }

    public void disconnect() {
        channel.close();
    }

    public boolean isConnected() {
        return channel.isConnected();
    }

    public String getUsername() {
        return username;
    }

    public int getRoomId() {
        return roomId;
    }
}