package pl.gajewski.server.chat.operations.types;

import org.json.JSONObject;
import pl.gajewski.server.chat.operations.IOperation;
import pl.gajewski.server.chat.operations.OperationFactory;
import pl.gajewski.server.chat.operations.OperationType;
import pl.gajewski.server.chat.user.UserHandler;
import pl.gajewski.server.chat.exceptions.IllegalUserHandlerStateException;
import pl.gajewski.server.chat.exceptions.UnauthorizedAccessException;

/**
 * @author Gajo
 *         06/05/2015
 */

public class LogoutOP implements IOperation {

    /**
     * QUERY:   {"type":"LOGOUT", "username":"jan_kowalski", "session_id":"id"}
     * SUCCESS: {"type":"Q_SUCCESS", "msg":"User-friendly message"}
     * FAILURE: {"type":"Q_FAILURE", "msg":"User-friendly message"}
     */

    private final UserHandler userHandler;
    private String username;
    private String sessionID;

    public LogoutOP(UserHandler userHandler, JSONObject jsonObject) {
        this.userHandler = userHandler;
        this.username = jsonObject.getString("username");
        this.sessionID = jsonObject.getString("session_id");
    }

    @Override
    public String call() {
        JSONObject json = new JSONObject();
        try {
            userHandler.logout(username, sessionID);
            json.put("type", OperationType.C_SUCCESS);
            json.put("obj", new JSONObject());
            json.put("msg", "Successfully logout: " + username);
        } catch (UnauthorizedAccessException | IllegalUserHandlerStateException e) {
            System.out.println(e.getMessage());
            json.put("type", OperationType.C_FAILURE);
            json.put("msg", e.getMessage());
        }

        return json.toString();
    }

}
