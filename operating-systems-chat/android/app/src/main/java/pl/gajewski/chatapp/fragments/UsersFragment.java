package pl.gajewski.chatapp.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import pl.gajewski.chatapp.R;
import pl.gajewski.chatapp.adapters.users.UserItem;
import pl.gajewski.chatapp.adapters.users.UserListAdapter;
import pl.gajewski.chatapp.authentication.AppAuth;
import pl.gajewski.chatapp.commands.types.LoggedUsersCmd;
import pl.gajewski.chatapp.connection.SocketHandler;
import pl.gajewski.chatapp.exceptions.CommandResponseException;

public class UsersFragment extends Fragment {

    private final AppAuth appAuth;
    private SocketHandler socketHandler;
    private LoggedUsersCmd loggedUsersCommand;

    private ImageView noUsersOnline;

    //we are going to use a handler to be able to run in our TimerTask
    final Handler handler = new Handler();

    private ArrayAdapter<UserItem> mUsersListAdapter;
    private final List<UserItem> mUsersList;
    private ListView usersView;
    private EditText filterText;

    public UsersFragment() {
        // Required empty public constructor

        // initialize variables
        appAuth = AppAuth.getInstance();
        socketHandler = new SocketHandler();
        loggedUsersCommand = new LoggedUsersCmd(appAuth);

        // initialize variables
        socketHandler = new SocketHandler();

        // create list view
        mUsersList = new ArrayList<>();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_user_list, container, false);

        noUsersOnline = (ImageView) fragmentView.findViewById(R.id.no_users_online);

        mUsersListAdapter = new UserListAdapter(getActivity(), mUsersList);
        usersView = (ListView) fragmentView.findViewById(R.id.usersList);
        usersView.setAdapter(mUsersListAdapter);
        usersView.setDivider(null);
        usersView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserItem receiver = (UserItem) parent.getItemAtPosition(position);

                Bundle bundle = new Bundle();
                bundle.putString("username", receiver.getUsername());

                Fragment userChatFragment = new UserChatFragment();
                userChatFragment.setArguments(bundle);

                getFragmentManager().beginTransaction()
                        .replace(R.id.mainContent, userChatFragment)
                        .commit();

            }
        });


        // prepare menu
        setHasOptionsMenu(true);

        // filter
        filterText = (EditText) fragmentView.findViewById(R.id.filter_text);
        filterText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mUsersListAdapter.getFilter().filter(s);
        }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Inflate the layout for this fragment
        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();

        // handler to update list
        handler.post(new LoggedUsersTask());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                handler.post(new LoggedUsersTask());
                handler.postDelayed(this, 1000);
            }
        }, 1000);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private void showList(final boolean show) {
        int shortAnimTime = 2000;

        usersView.setVisibility(show ? View.VISIBLE : View.GONE);
        usersView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        usersView.setVisibility(show ? View.VISIBLE : View.GONE);
                    }
                });

        noUsersOnline.setVisibility(show ? View.GONE : View.VISIBLE);
        noUsersOnline.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        noUsersOnline.setVisibility(show ? View.GONE : View.VISIBLE);
                    }
                });

    }


    class LoggedUsersTask implements Runnable {

        @Override
        public void run() {
            try {
                JSONObject responseJSON = socketHandler.execute(appAuth.getHost(), appAuth.getPort(), loggedUsersCommand);
                JSONArray usersJSON = responseJSON.getJSONObject("obj").getJSONArray("username");

                int check = 0;
                for (int i=0; i<usersJSON.length(); i++) {
                    for (UserItem userItem : mUsersList) {
                        if(usersJSON.getString(i).equals(userItem.getUsername())) {
                            check++;
                        }
                    }
                }

                synchronized (mUsersList) {

                    if(filterText.length() > 0) {
                        mUsersListAdapter.getFilter().filter(filterText.getText());
                        return;
                    }

                    if (check != mUsersList.size() || mUsersList.size() != usersJSON.length()) {

                        mUsersList.clear();
                        mUsersListAdapter.clear();

                        for (int i = 0; i < usersJSON.length(); i++) {
                            String username = usersJSON.getString(i);
                            if (!username.equals(appAuth.getUsername())) {
                                UserItem userItem = new UserItem(username, R.mipmap.user);
                                mUsersList.add(userItem);
                            }
                        }

                        if (mUsersListAdapter.isEmpty()) {
                            showList(false);
                        } else {
                            showList(true);
                        }

                    }

                    mUsersListAdapter.notifyDataSetChanged();
                    usersView.setAdapter(mUsersListAdapter);

                }

            } catch (JSONException | CommandResponseException e) {
                Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("LoggedUsers", e.getMessage());
                e.printStackTrace();
            }

        }

    }
}