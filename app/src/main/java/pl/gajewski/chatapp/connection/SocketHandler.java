package pl.gajewski.chatapp.connection;

import org.json.JSONException;
import org.json.JSONObject;

import pl.gajewski.chatapp.commands.AbstractCmd;
import pl.gajewski.chatapp.exceptions.CommandResponseException;

public class
        SocketHandler {

    /**
     * Load shared library for network service
     * Declaration of native methods
     */

    static {
        System.loadLibrary("chat-app");
    }

    private native String query(String host, int port, String query);

    public JSONObject execute(String host, int port, AbstractCmd query) throws JSONException, CommandResponseException {
        String response = query(host, port, query.create());
        return query.receive(response);
    }
}
