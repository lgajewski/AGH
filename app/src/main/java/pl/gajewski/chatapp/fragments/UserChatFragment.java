package pl.gajewski.chatapp.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import pl.gajewski.chatapp.Message;
import pl.gajewski.chatapp.R;
import pl.gajewski.chatapp.authentication.AppAuth;
import pl.gajewski.chatapp.commands.types.GetAllMsgCmd;
import pl.gajewski.chatapp.commands.types.SendMsgCmd;
import pl.gajewski.chatapp.connection.SocketHandler;
import pl.gajewski.chatapp.exceptions.CommandResponseException;

public class UserChatFragment extends Fragment {


    private SocketHandler socketHandler;

    final Handler handler = new Handler();
    private GetAllMsgCmd getLastMsgCmd;
    private AppAuth appAuth;
    private Runnable task;
    private String receiver;

    private ArrayAdapter<Message> mMessageListAdapter;
    private List<Message> mMessageList;

    public UserChatFragment() {
        // Required empty public constructor

        // check app authentication
        appAuth = AppAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_user_chat, container, false);

        receiver = getArguments().getString("username", null);

        if (receiver != null) {
            getLastMsgCmd = new GetAllMsgCmd(appAuth, receiver);

            // set text
            TextView usernameView = (TextView) fragmentView.findViewById(R.id.username);
            usernameView.setText(receiver);

            mMessageList = new ArrayList<>();
            mMessageListAdapter = new MessageListAdapter(getActivity());
            ListView msgList = (ListView) fragmentView.findViewById(R.id.messageList);
            msgList.setAdapter(mMessageListAdapter);

            // input
            final EditText inputView = (EditText) fragmentView.findViewById(R.id.input);
            inputView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                    if (id == R.id.login || id == EditorInfo.IME_NULL) {
                        if(inputView.length() > 0) {
                            handler.post(new SendMsgTask(inputView.getText().toString()));
                            inputView.setText("");
                        }
                        return true;
                    }
                    return false;
                }
            });

            final Button inputButton = (Button) fragmentView.findViewById(R.id.send_button);
            inputButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendMessage(inputView);
                }
            });

            // initialize variables
            socketHandler = new SocketHandler();

            // create repeatable task
            int period = 1000;
            task = new GetLastMsgTask(period);
        } else {
            // set text
            TextView usernameView = (TextView) fragmentView.findViewById(R.id.username);
            usernameView.setText("no such user");
        }

        return fragmentView;
    }

    private void sendMessage(EditText inputView) {
        String content = inputView.getText().toString();
        inputView.setText("");
        handler.post(new SendMsgTask(content));
    }

    @Override
    public void onResume() {
        super.onResume();

        int delay = 100;
        if (task != null) {
            handler.postDelayed(task, delay);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (task != null) {
            handler.removeCallbacks(task);
        }
    }

    class SendMsgTask implements Runnable {

        private String content;

        public SendMsgTask(String content) {
            this.content = content;
        }

        @Override
        public void run() {
            try {
                SendMsgCmd msgCmd = new SendMsgCmd(appAuth, receiver, content);
                socketHandler.execute(appAuth.getHost(), appAuth.getPort(), msgCmd);

//                String date = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
//                String time = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
//                Message msg = new Message(appAuth.getUsername(), date, time, content);
//                mMessageList.add(msg);
//                mMessageListAdapter.notifyDataSetChanged();
            } catch (JSONException | CommandResponseException e) {
                Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("GetMessages", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    class GetLastMsgTask implements Runnable {

        private int period;

        public GetLastMsgTask(int period) {
            this.period = period;
        }

        @Override
        public void run() {
            try {
                JSONObject responseJSON = socketHandler.execute(appAuth.getHost(), appAuth.getPort(), getLastMsgCmd);

                // update user messages
                JSONArray msgJSON = responseJSON.getJSONArray("obj");

                if(mMessageList.size() < msgJSON.length()) {

                    mMessageList.clear();

                    if (msgJSON.length() > 0) {

                        for (int i = 0; i < msgJSON.length(); i++) {
                            String sender = msgJSON.getJSONObject(i).getString("sender");
                            String date = msgJSON.getJSONObject(i).getString("date");
                            String time = msgJSON.getJSONObject(i).getString("time");
                            String content = msgJSON.getJSONObject(i).getString("content");
                            Message msg = new Message(sender, date, time, content);
                            mMessageList.add(msg);
                        }

                        mMessageListAdapter.notifyDataSetChanged();

                    }

                }

            } catch (JSONException | CommandResponseException e) {
                Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("GetMessages", e.getMessage());
                e.printStackTrace();
            } finally {
                handler.postDelayed(task, period);
            }

        }
    }

    class MessageListAdapter extends ArrayAdapter<Message> {
        private final Activity activity;

        public MessageListAdapter(Activity activity) {
            super(activity, R.layout.message, mMessageList);
            this.activity = activity;

        }

        class ViewHolder {
            public TextView content;
            public TextView date;
            public TextView userButton;
            public ImageView circle;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
            // reuse views
            if (rowView == null) {
                LayoutInflater inflater = activity.getLayoutInflater();
                rowView = inflater.inflate(R.layout.message, null);
                // configure view holder
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.content = (TextView) rowView.findViewById(R.id.msg_content);
                viewHolder.date = (TextView) rowView.findViewById(R.id.msg_date);
                viewHolder.userButton = (TextView) rowView.findViewById(R.id.msg_u);
                viewHolder.circle = (ImageView) rowView.findViewById(R.id.msg_circle);
                rowView.setTag(viewHolder);
            }

            // fill data

            ViewHolder holder = (ViewHolder) rowView.getTag();
            Message msg = mMessageList.get(position);
            holder.content.setText(msg.getContent());
            holder.date.setText(msg.getTime() + " | " + msg.getDate());
            holder.userButton.setText(String.valueOf(msg.getSender().charAt(0)));
            holder.circle.animate().setDuration(1000).alpha(1).rotation(360);

            LinearLayout linearLayout = (LinearLayout) rowView.findViewById(R.id.message_layout);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
            if (msg.getSender().equals(appAuth.getUsername())) {
                lp.setMargins(80, 0, 0, 0);
            } else {
                lp.setMargins(5, 0, 0, 0);
            }

            return rowView;
        }
    }

}