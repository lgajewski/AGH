package pl.gajewski.server.chat.operations.types;

import org.json.JSONObject;
import pl.gajewski.server.chat.exceptions.IllegalUserHandlerStateException;
import pl.gajewski.server.chat.exceptions.UnauthorizedAccessException;
import pl.gajewski.server.chat.operations.IOperation;
import pl.gajewski.server.chat.operations.OperationType;
import pl.gajewski.server.chat.user.UserHandler;

/**
 * @author Gajo
 *         06/05/2015
 */

public class GetStatOP implements IOperation {

    private final UserHandler userHandler;
    private final String sessionID;
    private String username;

    public GetStatOP(UserHandler userHandler, JSONObject jsonObject) {
        this.userHandler = userHandler;
        this.username = jsonObject.getString("username");
        this.sessionID = jsonObject.getString("session_id");
    }

    @Override
    public String call() {

        JSONObject json = new JSONObject();
        try {
            int[] stats = userHandler.getStatistics(username, sessionID);
            JSONObject obj = new JSONObject();
            obj.put("sent", stats[0]);
            obj.put("received", stats[1]);
            json.put("type", OperationType.C_SUCCESS);
            json.put("msg", "Retrieved statistics successfully");
            json.put("obj", obj);
        } catch (UnauthorizedAccessException e) {
            System.out.println(e.getMessage());
            json.put("type", OperationType.C_FAILURE);
            json.put("msg", e.getMessage());
        }

        return json.toString();
    }

}
