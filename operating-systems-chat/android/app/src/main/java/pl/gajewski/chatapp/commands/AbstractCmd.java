package pl.gajewski.chatapp.commands;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import pl.gajewski.chatapp.exceptions.CommandResponseException;

public abstract class AbstractCmd {

    public abstract String create() throws JSONException;

    public JSONObject receive(String response) throws JSONException, CommandResponseException {
        JSONObject json = new JSONObject(response);
        String type = json.getString("type");
        if (CmdType.C_SUCCESS.toString().equals(type)) {
            // log user friendly message
            Log.i("Command", json.getString("msg"));

            // extract proper object
            JSONObject result = new JSONObject();
            result.put("msg", json.getString("msg"));
            result.put("obj", json.get("obj"));
            return result;
        } else if (CmdType.C_FAILURE.toString().equals(type)) {
            throw new CommandResponseException(json.getString("msg"), this);
        } else {
            throw new JSONException("Incorrect JSON in response, \nmsg: " + json.getString("msg"));
        }
    }

}
