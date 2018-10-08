package pl.gajewski.chatapp.commands.types;

import org.json.JSONException;
import org.json.JSONObject;

import pl.gajewski.chatapp.commands.AbstractCmd;
import pl.gajewski.chatapp.commands.CmdType;

public class LoginCmd extends AbstractCmd {

    private String username;

    public LoginCmd(String username) {
        this.username = username;
    }

    @Override
    public String create() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("type", CmdType.LOGIN);
        json.put("username", username);
        return json.toString();
    }

}
