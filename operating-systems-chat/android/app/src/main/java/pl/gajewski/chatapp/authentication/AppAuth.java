package pl.gajewski.chatapp.authentication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pl.gajewski.chatapp.commands.AbstractCmd;
import pl.gajewski.chatapp.commands.types.LogoutCmd;
import pl.gajewski.chatapp.connection.SocketHandler;
import pl.gajewski.chatapp.exceptions.CommandResponseException;
import pl.gajewski.chatapp.exceptions.UnauthorizedAccessException;

public class AppAuth {

    private static AppAuth appAuthInstance = null;
    private static final Object instanceLock = new Object();

    private AppAuth() {
    }

    private String host;
    private int port;

    private String username;
    private String sessionId;

    private boolean authenticated;

    public static AppAuth getInstance() {
        if (appAuthInstance == null) {
            synchronized (instanceLock) {
                if (appAuthInstance == null) {
                    appAuthInstance = new AppAuth();
                }
            }
        }
        return appAuthInstance;
    }

    public synchronized void authenticate(String host, int port, String username, String sessionId) throws UnauthorizedAccessException {
        this.host = host;
        this.port = port;
        this.username = username;
        this.sessionId = sessionId;
        this.authenticated = true;
    }

    public synchronized boolean isAuthenticated() {
        return authenticated;
    }

    public static void clear() {
        appAuthInstance = null;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getSessionId() {
        return sessionId;
    }

}
