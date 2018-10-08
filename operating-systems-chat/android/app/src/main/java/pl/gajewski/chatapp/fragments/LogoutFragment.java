package pl.gajewski.chatapp.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pl.gajewski.chatapp.HomeActivity;
import pl.gajewski.chatapp.LoginActivity;
import pl.gajewski.chatapp.R;
import pl.gajewski.chatapp.authentication.AppAuth;
import pl.gajewski.chatapp.commands.types.LogoutCmd;
import pl.gajewski.chatapp.connection.SocketHandler;
import pl.gajewski.chatapp.exceptions.CommandResponseException;

public class LogoutFragment extends Fragment {


    private final AppAuth appAuth;
    private LogoutCmd logoutCmd;
    private final SocketHandler socketHandler;

    public LogoutFragment() {
        // Required empty public constructor

        appAuth = AppAuth.getInstance();
        logoutCmd = new LogoutCmd(appAuth);
        socketHandler = new SocketHandler();
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        if (!appAuth.isAuthenticated()) {
            AppAuth.clear();
            return inflater.inflate(R.layout.fragment_auth, container, false);
        }

        new LogoutTask().execute();

        return inflater.inflate(R.layout.fragment_logout, container, false);
    }

    class LogoutTask extends AsyncTask<Void, Void, Void> {

        private String result;

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            // clear auth
            AppAuth.clear();

            // start LoginActivity
            Toast.makeText(getActivity().getApplicationContext(), this.result, Toast.LENGTH_LONG).show();

            Intent intent = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                socketHandler.execute(appAuth.getHost(), appAuth.getPort(), logoutCmd);
                result = "Logout successful";
            } catch (JSONException | CommandResponseException e) {
                result = e.getMessage();
                Log.e("LoggedUsers", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }
    }
}