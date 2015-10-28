package pl.gajewski.chatapp.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import pl.gajewski.chatapp.R;
import pl.gajewski.chatapp.authentication.AppAuth;
import pl.gajewski.chatapp.commands.types.StatisticsCmd;
import pl.gajewski.chatapp.connection.SocketHandler;
import pl.gajewski.chatapp.exceptions.CommandResponseException;

public class HomeFragment extends Fragment {

    private Handler handler;
    private AppAuth appAuth;
    private SocketHandler socketHandler;
    private StatisticsCmd statisticsCmd;
    private TextView sentView;
    private TextView receivedView;

    public HomeFragment() {
        // Required empty public constructor
        appAuth = AppAuth.getInstance();
        socketHandler = new SocketHandler();
        statisticsCmd = new StatisticsCmd(appAuth);
        handler = new Handler();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // check app auth
        if (!appAuth.isAuthenticated()) {
            AppAuth.clear();
            return inflater.inflate(R.layout.fragment_auth, container, false);
        }

        // fill forms
        TextView usernameView = (TextView) view.findViewById(R.id.home_username);
        usernameView.setText(appAuth.getUsername());

        sentView = (TextView) view.findViewById(R.id.home_sent);
        receivedView = (TextView) view.findViewById(R.id.home_received);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        handler.post(new LoggedUsersTask());
    }

    class LoggedUsersTask implements Runnable {

        @Override
        public void run() {
            try {
                JSONObject responseJSON = socketHandler.execute(appAuth.getHost(), appAuth.getPort(), statisticsCmd);
                int sent = responseJSON.getJSONObject("obj").getInt("sent");
                int received = responseJSON.getJSONObject("obj").getInt("received");

                sentView.setText(String.valueOf(sent));
                receivedView.setText(String.valueOf(received));

            } catch (JSONException | CommandResponseException e) {
                Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("LoggedUsers", e.getMessage());
                e.printStackTrace();
            }

        }

    }


}