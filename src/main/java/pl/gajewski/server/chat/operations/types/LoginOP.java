package pl.gajewski.server.chat.operations.types;

import org.json.JSONObject;
import pl.gajewski.server.chat.operations.IOperation;
import pl.gajewski.server.chat.operations.OperationFactory;
import pl.gajewski.server.chat.operations.OperationType;
import pl.gajewski.server.chat.user.UserHandler;
import pl.gajewski.server.chat.exceptions.IllegalUserHandlerStateException;

/**
 * @author Gajo
 *         06/05/2015
 */

public class LoginOP implements IOperation {

    /**
     * QUERY:   {"type":"LOGIN", "username":"jan_kowalski"}
     * SUCCESS: {"type":"Q_SUCCESS", "msg":"User-friendly message", "obj": {"username":"jan_kowalski", "session_id":"23j1k24rtj"}}
     * FAILURE: {"type":"Q_FAILURE", "msg":"User-friendly message"}
     */

    private final UserHandler userHandler;
    private String username;

    public LoginOP(UserHandler userHandler, JSONObject jsonObject) {
        this.userHandler = userHandler;
        this.username = jsonObject.getString("username");
    }

    @Override
    public String call() {

        JSONObject json = new JSONObject();
        try {
            String sessionID = userHandler.login(username);
            JSONObject obj = new JSONObject();
            obj.put("username", username);
            obj.put("session_id", sessionID);

            json.put("type", OperationType.C_SUCCESS);
            json.put("msg", "Logged in: " + username);
            json.put("obj", obj);
        } catch (IllegalUserHandlerStateException e) {
            System.out.println(e.getMessage());
            json.put("type", OperationType.C_FAILURE);
            json.put("msg", e.getMessage());
        }

        return json.toString();
    }

}
