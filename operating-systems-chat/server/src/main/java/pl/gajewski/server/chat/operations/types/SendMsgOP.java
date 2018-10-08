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

public class SendMsgOP implements IOperation {

    /**
     * QUERY:   {"type":"SEND_TO_USER", "username":"jan_kowalski", "session_id":"23j1k24rtj", "receiver":"janek2", "content":"bla bla"}
     * SUCCESS: {"type":"q_success", "msg":"User-friendly message"}
     * FAILURE: {"type":"q_failure", "msg":"User-friendly message"}
     */

    private UserHandler userHandler;
    private String receiver;
    private String content;
    private String username;
    private String sessionID;

    public SendMsgOP(UserHandler userHandler, JSONObject jsonObject) {
        this.userHandler = userHandler;
        this.username = jsonObject.getString("username");
        this.sessionID = jsonObject.getString("session_id");
        this.receiver = jsonObject.getString("receiver");
        this.content = jsonObject.getString("content");
    }

    @Override
    public String call() {
        JSONObject json = new JSONObject();
        try {
            userHandler.sendMessage(username, sessionID, receiver, content);
            json.put("type", OperationType.C_SUCCESS);
            json.put("obj", new JSONObject());
            json.put("msg", "Message was sent successfully");
        } catch (UnauthorizedAccessException | IllegalUserHandlerStateException e) {
            System.out.println(e.getMessage());
            json.put("type", OperationType.C_FAILURE);
            json.put("msg", e.getMessage());
        }

        return json.toString();
    }

}
