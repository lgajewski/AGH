package lgajewski.distributed.lab5.management;

import java.util.ArrayList;
import java.util.List;

public class Room {

    private String roomId;
    private List<String> members;

    Room(String roomId) {
        this.roomId = roomId;
        this.members = new ArrayList<>();
    }

    public String getRoomId() {
        return roomId;
    }

    public List<String> getMembers() {
        return new ArrayList<>(members);
    }

    void addMember(String member) {
        members.add(member);
    }

    boolean removeMember(String member) {
        return members.remove(member);
    }

    boolean isEmpty() {
        return members.isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("Room [" + roomId + "], members: " + members.size() + "\n");
        for (String member : members) {
            builder.append("\t > ").append(member).append("\n");
        }

        return builder.append("\n").toString();
    }
}
