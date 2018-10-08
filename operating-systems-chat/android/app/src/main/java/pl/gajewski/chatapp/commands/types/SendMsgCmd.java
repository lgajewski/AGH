package pl.gajewski.chatapp.commands.types;

import org.json.JSONException;
import org.json.JSONObject;

import pl.gajewski.chatapp.authentication.AppAuth;
import pl.gajewski.chatapp.commands.AbstractCmd;
import pl.gajewski.chatapp.commands.CmdType;

public class SendMsgCmd extends AbstractCmd {

    private AppAuth appAuth;
    private String receiver;
    private String content;

    public SendMsgCmd(AppAuth appAuth, String receiver, String content) {
        this.appAuth = appAuth;
        this.receiver = receiver;
        this.content = content;
    }

    @Override
    public String create() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("type", CmdType.SEND_TO_USER);
        json.put("username", appAuth.getUsername());
        json.put("session_id", appAuth.getSessionId());
        json.put("receiver", receiver);
        json.put("content", content);
        return json.toString();
    }

}
