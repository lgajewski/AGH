package pl.gajewski.chatapp.commands.types;

import org.json.JSONException;
import org.json.JSONObject;

import pl.gajewski.chatapp.authentication.AppAuth;
import pl.gajewski.chatapp.commands.AbstractCmd;
import pl.gajewski.chatapp.commands.CmdType;

public class LogoutCmd extends AbstractCmd {

    private AppAuth appAuth;

    public LogoutCmd(AppAuth appAuth) {
        this.appAuth = appAuth;
    }

    @Override
    public String create() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("type", CmdType.LOGOUT);
        json.put("username", appAuth.getUsername());
        json.put("session_id", appAuth.getSessionId());
        return json.toString();
    }

}
