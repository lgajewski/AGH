package lgajewski.distributed.lab5;

import lgajewski.distributed.lab5.chat.SimpleChat;
import lgajewski.distributed.lab5.management.ChatManagement;
import lgajewski.distributed.lab5.management.Room;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public class ChatRunner {

    private BufferedReader in;

    private String username;

    private ChatManagement chatManagement;

    public ChatRunner(String username) {
        this.username = username;
    }

    public void start() throws Exception {
        in = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Connecting to ChatManagement...");
        chatManagement = new ChatManagement();
        chatManagement.connect();

        while (true) {
            try {
                System.out.println("Type command to continue, exit/quit to stop.");
                System.out.println("\t> user");
                System.out.println("\t> connect");
                System.out.println("\t> rooms");
                System.out.print("> ");
                System.out.flush();

                String command = in.readLine().toLowerCase();

                switch (command) {
                    case "user":
                        username = readUsername();
                        System.out.println("Set username to " + username);
                        break;
                    case "connect":
                        int roomId = readRoomId();

                        // create and connect to chat
                        SimpleChat simpleChat = new SimpleChat(username, roomId);
                        simpleChat.connect(new ChatEventListener(username, roomId));

                        break;
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

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        chatManagement.close();
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

        return Integer.valueOf(in.readLine());
    }

    private class ChatEventListener implements SimpleChat.OnEventListener {

        private String username;
        private int roomId;

        ChatEventListener(String username, int roomId) {
            this.username = username;
            this.roomId = roomId;
        }

        @Override
        public void onConnect() throws Exception {
            chatManagement.join(username, roomId);
        }

        @Override
        public void onDisconnect() throws Exception {
            chatManagement.leave(username, roomId);
        }
    }

    public static void main(String[] args) throws Exception {
        String username = args.length > 0 ? args[0] : "user";

        System.out.println("[ChatRunner] Default username: " + username);
        new ChatRunner(username).start();
    }

}
