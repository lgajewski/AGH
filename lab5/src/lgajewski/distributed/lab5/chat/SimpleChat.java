package lgajewski.distributed.lab5.chat;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.stack.ProtocolStack;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import static lgajewski.distributed.lab5.protos.ChatOperationProtos.ChatMessage;

public class SimpleChat extends ReceiverAdapter {

    @SuppressWarnings("WeakerAccess")
    public interface OnEventListener {
        void onConnect() throws Exception;

        void onDisconnect() throws Exception;
    }

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

    private void eventLoop() {

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        while (true) {

            try {

                System.out.print("#" + roomId + " > ");
                System.out.flush();

                String line = in.readLine().toLowerCase();

                if (line.startsWith("quit") || line.startsWith("exit"))
                    break;

                line = "[" + username + "] " + line;

                ChatMessage chatMessage = ChatMessage.newBuilder().setMessage(line).build();

                Message msg = new Message(null, null, chatMessage);
                channel.send(msg);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void viewAccepted(View new_view) {
        System.out.println("** view: " + new_view);

        System.out.println();
        System.out.flush();

    }


    public void receive(Message msg) {
        String line = msg.getSrc() + ": " + msg.getObject();

        System.out.println(line);

        System.out.print("#" + roomId + " > ");
        System.out.flush();
    }


    public void connect(OnEventListener listener) throws Exception {
        channel.connect(String.valueOf(roomId));
        listener.onConnect();

        eventLoop();

        channel.close();
        listener.onDisconnect();
    }

}