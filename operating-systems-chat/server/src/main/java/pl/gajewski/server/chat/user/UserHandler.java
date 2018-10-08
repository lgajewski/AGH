package pl.gajewski.server.chat.user;

import pl.gajewski.server.chat.exceptions.IllegalUserHandlerStateException;
import pl.gajewski.server.chat.exceptions.UnauthorizedAccessException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

/**
 * @author Gajo
 *         06/05/2015
 */

public class UserHandler {

    private Map<String, User> users;

    public UserHandler() {
        this.users = new Hashtable<>();
    }

    private boolean isAuthCorrect(String username, String sessionID) {
        User user = users.get(username);
        return (user != null && user.getSessionID().equals(sessionID));
    }

    public String login(String username) throws IllegalUserHandlerStateException {
        if(!users.containsKey(username)) {
            String sessionID = UUID.randomUUID().toString();
            users.put(username, new User(username, sessionID));
            return sessionID;
        } else {
            throw new IllegalUserHandlerStateException("User is currently logged in");
        }
    }

    public void logout(String username, String sessionID) throws UnauthorizedAccessException, IllegalUserHandlerStateException {
        if(isAuthCorrect(username, sessionID)) {
            User user = users.get(username);
            if (user != null && user.getSessionID().equals(sessionID)) {
                users.remove(username);
            } else {
                throw new IllegalUserHandlerStateException("No such user with that sessionID");
            }
        } else {
            throw new UnauthorizedAccessException("logout");
        }
    }

    public List<Message> getMessages(String username, String sessionID, String receiver) throws UnauthorizedAccessException {
        if(isAuthCorrect(username, sessionID)) {
            return users.get(username).getMessages(receiver);
        } else {
            throw new UnauthorizedAccessException("getMessages");
        }
    }

    public void clearMessages(String username, String sessionID) throws UnauthorizedAccessException, IllegalUserHandlerStateException {
        if(isAuthCorrect(username, sessionID)) {
            User user = users.get(username);
            if(user != null) {
                user.clearMessages();
            } else {
                throw new IllegalUserHandlerStateException("No such user");
            }
        } else {
            throw new UnauthorizedAccessException("clearMessages");
        }
    }

    public List<String> getLoggedUsers(String username, String sessionID) throws UnauthorizedAccessException {
        if(isAuthCorrect(username, sessionID)) {
            List<String> result = new LinkedList<>();
            for (User user : users.values()) {
                result.add(user.getUsername());
            }
            return result;
        } else {
            throw new UnauthorizedAccessException("getLoggedUsers");
        }
    }

    public void sendMessage(String username, String sessionID, String receiverName, String content)
            throws UnauthorizedAccessException, IllegalUserHandlerStateException {
        if(isAuthCorrect(username, sessionID)) {
            User sender = users.get(username);
            User receiver = users.get(receiverName);
            if(sender != null && receiver != null) {
                Message message = new Message(sender, LocalDate.now(), LocalTime.now(), content);
                sender.addMessage(receiver.getUsername(), message);
                receiver.addMessage(sender.getUsername(), message);

                // for statistics
                sender.inc(sender.getUsername());
                receiver.inc(sender.getUsername());
            } else {
                throw new IllegalUserHandlerStateException("No such receiver in database");
            }
        } else {
            throw new UnauthorizedAccessException("sendMessage");
        }
    }

    public int[] getStatistics(String username, String sessionID) throws UnauthorizedAccessException {
        if(isAuthCorrect(username, sessionID)) {
            User user = users.get(username);
            return new int[]{user.getSent(), user.getReceived()};

        } else {
            throw new UnauthorizedAccessException("sendMessage");
        }
    }
}
