package pl.gajewski.server.chat.operations.types;

import org.json.JSONArray;
import org.json.JSONObject;
import pl.gajewski.server.chat.operations.IOperation;
import pl.gajewski.server.chat.operations.OperationFactory;
import pl.gajewski.server.chat.operations.OperationType;
import pl.gajewski.server.chat.user.UserHandler;
import pl.gajewski.server.chat.exceptions.UnauthorizedAccessException;

import java.util.Collections;
import java.util.List;

/**
 * @author Gajo
 *         06/05/2015
 */

public class LoggedUsersOP implements IOperation {

    /**
     * QUERY:   {"type":"GET_LOGGED_USERS", "username":"jan_kowalski", "session_id":"23j1k24rtj"}
     * SUCCESS: {"type":"Q_SUCCESS", "msg":"User-friendly message", "obj": [{"user1", "user2", "user3"}]}
     * FAILURE: {"type":"Q_FAILURE", "msg":"User-friendly message"}
     */

    private final UserHandler userHandler;
    private String username;
    private String sessionID;

    public LoggedUsersOP(UserHandler userHandler, JSONObject jsonObject) {
        this.userHandler = userHandler;
        this.username = jsonObject.getString("username");
        this.sessionID = jsonObject.getString("session_id");
    }

    @Override
    public String call() {
        JSONObject json = new JSONObject();
        try {
            List<String> users = userHandler.getLoggedUsers(username, sessionID);
            Collections.sort(users);

            JSONObject obj = new JSONObject();
            obj.put("username", users);
            json.put("type", OperationType.C_SUCCESS);
            json.put("obj", obj);
            json.put("msg", "Received logged users list");
        } catch (UnauthorizedAccessException e) {
            System.out.println(e.getMessage());
            json.put("type", OperationType.C_FAILURE);
            json.put("msg", e.getMessage());
        }

        return json.toString();
    }

}
