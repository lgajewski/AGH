package pl.gajewski.server.chat.operations.types;

import org.json.JSONArray;
import org.json.JSONObject;
import pl.gajewski.server.chat.operations.IOperation;
import pl.gajewski.server.chat.operations.OperationFactory;
import pl.gajewski.server.chat.operations.OperationType;
import pl.gajewski.server.chat.user.Message;
import pl.gajewski.server.chat.user.UserHandler;
import pl.gajewski.server.chat.exceptions.UnauthorizedAccessException;

import java.util.List;
import java.util.Set;

/**
 * @author Gajo
 *         06/05/2015
 */

public class GetAllMsgOP implements IOperation {

    /**
     * QUERY:   {"type":"GET_ALL_MSG", "username":"jan_kowalski", "session_id":"23j1k24rtj"}
     * SUCCESS: {"type":"q_success", "msg":"User-friendly message", "obj": [{"sender":"user1", "time":"11:00", "content":"bla bla"}
     *          {"sender":"user2", "time":"12:00", "content":"example"}]}
     * FAILURE: {"type":"q_failure", "msg":"User-friendly message"}
     */

    private final UserHandler userHandler;
    private String receiver;
    private String username;
    private String sessionID;

    public GetAllMsgOP(UserHandler userHandler, JSONObject jsonObject) {
        this.userHandler = userHandler;
        this.username = jsonObject.getString("username");
        this.sessionID = jsonObject.getString("session_id");
        this.receiver = jsonObject.getString("receiver");
    }

    @Override
    public String call() {
        JSONObject json = new JSONObject();
        try {
            JSONArray msgJSONArray = new JSONArray();
            List<Message> messageList = userHandler.getMessages(username, sessionID, receiver);
            for (Message message : messageList) {
                msgJSONArray.put(message.getJSONObject());
            }

            json.put("obj", msgJSONArray);
            json.put("type", OperationType.C_SUCCESS);
            json.put("msg", "Received user messages");
        } catch (UnauthorizedAccessException e) {
            System.out.println(e.getMessage());
            json.put("type", OperationType.C_FAILURE);
            json.put("msg", e.getMessage());
        }

        return json.toString();
    }

}
