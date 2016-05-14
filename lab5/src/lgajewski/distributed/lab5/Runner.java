package lgajewski.distributed.lab5;

import lgajewski.distributed.lab5.protos.ChatOperationProtos;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Runner {

    public static void main(String[] args) {

        try {
            InetAddress group = InetAddress.getByName("224.0.0.7");
            MulticastSocket multicastSocket = new MulticastSocket(6789);
            multicastSocket.joinGroup(group);
            for (int i = 0; i < 10; i++) {
                byte[] buf = createMessage();
                DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length, group, 6789);
                multicastSocket.send(datagramPacket);
                System.out.println("sent #" + i);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static byte[] createMessage() {
        ChatOperationProtos.ChatMessage msg = ChatOperationProtos.ChatMessage.newBuilder()
                .setMessage("sample mssage").build();

        return msg.toByteArray();
    }
}
