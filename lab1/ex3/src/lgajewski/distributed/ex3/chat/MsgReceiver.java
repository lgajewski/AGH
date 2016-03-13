package lgajewski.distributed.ex3.chat;

import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MsgReceiver implements Runnable {
    private final InetAddress inetAddress;
    private final int port;
    private final String username;

    private boolean running = true;

    public MsgReceiver(InetAddress inetAddress, int port, String username) {
        this.inetAddress = inetAddress;
        this.port = port;
        this.username = username;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public void run() {
        System.out.println("[receiver] ready to receive messages");
        try (MulticastSocket socket = new MulticastSocket(port)) {
//            socket.setInterface(InetAddress.getByName(null));
            socket.joinGroup(inetAddress);
            while (running) {
                byte[] bytes = new byte[Message.SIZE_IN_BYTES];
                final DatagramPacket packet = new DatagramPacket(bytes, Message.SIZE_IN_BYTES);
                socket.receive(packet);


                try {
                    final Message message = Message.retrieveMessage(new ByteInputStream(bytes));
                    if (!username.equals(message.getUsername())) {
                        System.out.println("[receiver] <" + message.getUsername() + ">: " + message.getContent());
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println("[receiver] got malformed message");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class ByteInputStream extends InputStream {

        private byte[] bytes;
        private int i;

        public ByteInputStream(byte[] bytes) {
            this.bytes = bytes;
        }

        @Override
        public int read() throws IOException {
            if (i < bytes.length) {
                return bytes[i++];
            } else {
                return -1;
            }
        }
    }

}