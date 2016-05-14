package lgajewski.distributed.lab5;

import lgajewski.distributed.lab5.protos.ChatOperationProtos;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.protocols.*;
import org.jgroups.protocols.pbcast.*;
import org.jgroups.stack.ProtocolStack;

public class Main {

    public static void main(String[] args) throws Exception {
        ProtocolStack stack = getProtocolStack();

        // don't create default ProtocolStack
        JChannel channel = new JChannel(false);

        // ProtocolStack
        channel.setProtocolStack(stack);
        stack.init();

        channel.connect("ChatCluster");

        for (int i = 0; i < 10; i++) {
            Message msg = new Message(null, null, createMessage());
            channel.send(msg);
            System.out.println("sent: #" + i);
        }

        channel.setReceiver(new ReceiverAdapter() {
            @Override
            public void viewAccepted(View view) {
                super.viewAccepted(view);
                System.out.println("viewAccepted: " + view.toString());
            }

            @Override
            public void receive(Message msg) {
                super.receive(msg);
                System.out.println("receive from " + msg.getSrc() + ", object: " + msg.getObject());
            }
        });

        Thread.sleep(30000);
        channel.close();
    }

    private static byte[] createMessage() {
        ChatOperationProtos.ChatMessage msg = ChatOperationProtos.ChatMessage.newBuilder()
                .setMessage("sample mssage").build();

        return msg.toByteArray();
    }

    private static ProtocolStack getProtocolStack() {
        ProtocolStack stack = new ProtocolStack();

        stack.addProtocol(new UDP())
                .addProtocol(new PING())
                .addProtocol(new MERGE2())
                .addProtocol(new FD_SOCK())
                .addProtocol(new FD_ALL().setValue("timeout", 12000).setValue("interval", 3000))
                .addProtocol(new VERIFY_SUSPECT())
                .addProtocol(new BARRIER())
                .addProtocol(new NAKACK())
                .addProtocol(new UNICAST2())
                .addProtocol(new STABLE())
                .addProtocol(new GMS())
                .addProtocol(new UFC())
                .addProtocol(new MFC())
                .addProtocol(new FRAG2())
                .addProtocol(new STATE_TRANSFER())
                .addProtocol(new FLUSH());
        return stack;
    }

}
