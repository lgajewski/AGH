package pl.gajewski.chatapp.adapters.users;

import android.app.Fragment;

public class UserItem {

    private String username;
    private int icon;

    public UserItem(String username, int icon) {
        this.username = username;
        this.icon = icon;
    }

    public String getUsername() {
        return username;
    }

    public int getIcon() {
        return icon;
    }
}