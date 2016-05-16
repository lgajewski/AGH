package lgajewski.distributed.lab5;

import lgajewski.distributed.lab5.chat.ChatException;
import lgajewski.distributed.lab5.chat.SimpleChat;
import lgajewski.distributed.lab5.management.ChatManagement;
import lgajewski.distributed.lab5.management.Room;
import org.jgroups.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import static lgajewski.distributed.lab5.protos.ChatOperationProtos.ChatMessage;

@SuppressWarnings("WeakerAccess")
public class ChatRunner {

    public static final int ROOM_LIMIT = 200;
    private SimpleChat[] chatRooms = new SimpleChat[ROOM_LIMIT];

    private String username;

    private final BufferedReader in;

    public ChatRunner(String username) {
        this.username = username;
        this.in = new BufferedReader(new InputStreamReader(System.in));
    }

    public void start() throws Exception {

        System.out.println("Connecting to ChatManagement...");
        ChatManagement chatManagement = new ChatManagement();
        chatManagement.connect();

        while (true) {
            try {
                System.out.println("Type command to continue, exit/quit to stop.");
                System.out.println("\t> user");
                System.out.println("\t> connect");
                System.out.println("\t> disconnect");
                System.out.println("\t> send");
                System.out.println("\t> rooms");
                System.out.print("> ");
                System.out.flush();

                String command = in.readLine().toLowerCase();

                int roomId;
                SimpleChat simpleChat;

                switch (command) {
                    case "user":
                        username = readUsername();
                        System.out.println("Set username to " + username);
                        break;
                    case "connect":
                        roomId = readRoomId();

                        // create and connect to chat
                        simpleChat = getChatRoom(username, roomId);

                        if (simpleChat.isConnected()) {
                            throw new ChatException("Unable to connect! Already connected to room " + roomId);
                        }

                        simpleChat.connect();
                        System.out.println("Connected to room " + roomId);

                        chatManagement.join(username, roomId);

                        break;
                    case "disconnect":
                        roomId = readRoomId();

                        // get chat from an array
                        simpleChat = getChatRoom(username, roomId);
                        if (!simpleChat.isConnected()) {
                            throw new ChatException("Unable to disconnect! Not connected to room " + roomId);
                        }

                        simpleChat.disconnect();

                        chatManagement.leave(username, roomId);

                        System.out.println("Disconnected from room " + roomId);

                        break;
                    case "send":
                        roomId = readRoomId();

                        // get chat from an array
                        simpleChat = getChatRoom(username, roomId);
                        if (!simpleChat.isConnected()) {
                            throw new ChatException("Unable to send message! Not connected to room " + roomId);
                        }

                        sendMessageLoop(simpleChat);

                    case "rooms":
                        List<Room> rooms = chatManagement.getRooms();
                        rooms.forEach(System.out::println);

                        if (rooms.isEmpty()) {
                            System.out.println("No users active");
                        }

                        break;
                }

                // handle exit from event loop
                if (command.startsWith("quit") || command.startsWith("exit"))
                    break;
            } catch (ChatException e) {
                System.err.println(e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        chatManagement.close();
    }

    private void sendMessageLoop(SimpleChat simpleChat) throws Exception {
        while (true) {
            System.out.print("#" + simpleChat.getRoomId() + " > ");
            System.out.flush();

            String line = in.readLine().toLowerCase();

            if (line.equals("exit") || line.equals("quit")) {
                break;
            }

            line = "[" + simpleChat.getUsername() + "] " + line;

            ChatMessage chatMessage = ChatMessage.newBuilder().setMessage(line).build();

            simpleChat.send(new Message(null, null, chatMessage));
        }
    }

    private String readUsername() throws IOException {
        // read username
        System.out.println("Username (" + username + ")");
        System.out.print("> ");
        System.out.flush();

        return in.readLine().toLowerCase();
    }

    private int readRoomId() throws IOException {
        // read roomId
        System.out.println("Room identifier [1-200]");
        System.out.print("> ");
        System.out.flush();

        int roomId = Integer.valueOf(in.readLine());
        if (roomId < 1 || roomId > 200) {
            throw new IllegalArgumentException("roomId should be between 1 and 200");
        }

        return roomId;
    }

    private SimpleChat getChatRoom(String username, int roomId) throws Exception {
        SimpleChat simpleChat = chatRooms[roomId - 1];

        if (simpleChat == null) {
            simpleChat = chatRooms[roomId - 1] = new SimpleChat(username, roomId);
        }

        if (!simpleChat.getUsername().equals(username)) {
            throw new ChatException("Unable to get chat room for different user! " + username + " != " + simpleChat.getUsername());
        }

        if (simpleChat.getRoomId() != roomId) {
            throw new ChatException("Unable to get chat room for different roomId! " + roomId + " != " + simpleChat.getRoomId());
        }

        return simpleChat;
    }

    public static void main(String[] args) throws Exception {
        String username = args.length > 0 ? args[0] : "user";

        System.out.println("[ChatRunner] Default username: " + username);
        new ChatRunner(username).start();
    }

}
