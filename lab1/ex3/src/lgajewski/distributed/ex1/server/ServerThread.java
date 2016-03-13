
package lgajewski.distributed.ex1.server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ServerThread extends Thread {

    protected DatagramSocket socket = null;
    //private static Map<Integer, InetAddress> ports = new HashMap<Integer, InetAddress>();

    public ServerThread() throws java.net.SocketException {
        super("ServerThread");
        this.socket = new DatagramSocket(Config.MULTICAST_INBOUND_PORT);
    }

    public void run() {
        while (true) { // until killed
            try {

                byte[] buf = new byte[Config.BUF_SIZE];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                String data = null;

                // recieve a packet
                System.out.println("DEBUG: receiving on: " + this.socket.getLocalAddress());
                socket.receive(packet); // thread blocks here
                data = new String(packet.getData(), 0, packet.getLength());
                System.out.println("DEBUG: recieved: " + packet.getAddress() + " " + packet.getPort() + " " + data);

                // broadcast it
                InetAddress group = InetAddress.getByName(Config.MULTICAST_IP);
                packet = new DatagramPacket(buf, buf.length, group, Config.MULTICAST_OUTBOUND_PORT);
                socket.send(packet);

            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
        socket.close();
    }

}