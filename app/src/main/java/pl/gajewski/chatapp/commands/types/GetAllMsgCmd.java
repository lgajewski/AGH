package pl.gajewski.chatapp.commands.types;

import org.json.JSONException;
import org.json.JSONObject;

import pl.gajewski.chatapp.Message;
import pl.gajewski.chatapp.authentication.AppAuth;
import pl.gajewski.chatapp.commands.AbstractCmd;
import pl.gajewski.chatapp.commands.CmdType;

public class GetAllMsgCmd extends AbstractCmd {

    private String receiver;
    private AppAuth appAuth;

    public GetAllMsgCmd(AppAuth appAuth, String receiver) {
        this.appAuth = appAuth;
        this.receiver = receiver;

    }

    @Override
    public String create() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("username", appAuth.getUsername());
        json.put("session_id", appAuth.getSessionId());
        json.put("type", CmdType.GET_ALL_MSG);
        json.put("receiver", receiver);

        return json.toString();
    }
}
