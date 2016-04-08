package lgajewski.distributed.ex3.chat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class MsgSender implements Runnable {
    private final InetAddress inetAddress;
    private final int port;
    private final String username;

    private boolean running = true;

    public MsgSender(InetAddress address, int port, String username) {
        this.inetAddress = address;
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
        System.out.println("[sender] running, please type some messages to sent");
        try (MulticastSocket socket = new MulticastSocket()) {
            socket.joinGroup(inetAddress);

            final Scanner scanner = new Scanner(System.in);
            while (running) {
                Message message = readMessage(scanner);
                send(socket, message.getBytes());
            }
        } catch (NoSuchElementException e) {
            System.out.println("Terminating scanner..");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Message readMessage(Scanner scanner) throws IOException {
        String content = scanner.nextLine();
        return new Message(username, content);
    }

    private void send(DatagramSocket socket, byte[] bytes) throws IOException {
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, inetAddress, port);
        socket.send(packet);
    }
}