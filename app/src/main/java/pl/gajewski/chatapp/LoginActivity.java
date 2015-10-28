package pl.gajewski.chatapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import pl.gajewski.chatapp.authentication.AppAuth;
import pl.gajewski.chatapp.commands.AbstractCmd;
import pl.gajewski.chatapp.commands.types.LoginCmd;
import pl.gajewski.chatapp.exceptions.CommandResponseException;
import pl.gajewski.chatapp.connection.SocketHandler;
import pl.gajewski.chatapp.exceptions.UnauthorizedAccessException;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity {

    //"192.168.56.1", 8080

    private final int MIN_SLEEP = 3000;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mUsernameView;
    private EditText mHostView;
    private EditText mPortView;
    private View mProgressBar;
    private SocketHandler socketHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        socketHandler = new SocketHandler();

        mHostView = (EditText) findViewById(R.id.host);
        mPortView = (EditText) findViewById(R.id.port);
        mUsernameView = (EditText) findViewById(R.id.username);
        mUsernameView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mLoginButton = (Button) findViewById(R.id.log_in_button);
        mLoginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mProgressBar = findViewById(R.id.progressBar);
    }

    /**
     * Fills form with default data when application icon touched
     */
    public void fill(View view) {
        mHostView.setText("192.168.56.1");
        mPortView.setText("8080");
        mUsernameView.setText("gajo");
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (mAuthTask != null) return;

        // Reset errors.
        mUsernameView.setError(null);
        mPortView.setError(null);
        mHostView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString();
        String host = mHostView.getText().toString();
        String port = mPortView.getText().toString();

        View focus = null;

        // Check for a valid mUsername, if the user entered one.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focus = mUsernameView;
        } else if (username.length() < 4) {
            mUsernameView.setError(getString(R.string.error_username_too_short));
            focus = mUsernameView;
        }

        // Check for a valid mHost and mPort
        if (TextUtils.isEmpty(port) || !TextUtils.isDigitsOnly(port)) {
            mPortView.setError(getString(R.string.error_invalid_port));
            focus = mPortView;
        }
        if (TextUtils.isEmpty(host)) {
            mHostView.setError(getString(R.string.error_field_required));
            focus = mHostView;
        }


        if (focus != null) {
            // There was an error; focus form field with an error
            focus.requestFocus();
        } else {
            // perform the user login attempt
            AbstractCmd loginCommand = new LoginCmd(username);
            mAuthTask = new UserLoginTask(host, Integer.valueOf(port), loginCommand);
            mAuthTask.execute();
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress(final boolean show) {

        mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressBar.animate().setDuration(MIN_SLEEP).alpha(show ? 1 : 0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mProgressBar.setVisibility(show ? View.GONE : View.VISIBLE);
                    }
                });

    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, String> {

        private final AbstractCmd command;
        private final int port;
        private final String host;

        public UserLoginTask(String host, int port, AbstractCmd command) {
            this.host = host;
            this.port = port;
            this.command = command;
        }

        @Override
        protected void onPreExecute() {
            showProgress(true);
        }

        @Override
        protected String doInBackground(Void... params) {
            long start = System.currentTimeMillis();

            String message;
            try {
                // create new socket to execute login command
                JSONObject responseJSON = socketHandler.execute(host, port, command);
                JSONObject authJSON = responseJSON.getJSONObject("obj");

                // save authentication
                String username = authJSON.getString("username");
                String sessionID = authJSON.getString("session_id");
                AppAuth.getInstance().authenticate(host, port, username, sessionID);

                message = responseJSON.getString("msg");

                long time = System.currentTimeMillis() - start;
                if (time < MIN_SLEEP) {
                    Thread.sleep(MIN_SLEEP - time);
                }
            } catch (InterruptedException e) {
                message = "Connection Interrupted";
                e.printStackTrace();
            } catch (JSONException | CommandResponseException | UnauthorizedAccessException e) {
                message = e.getMessage();
                e.printStackTrace();
            }

            return message;
        }

        @Override
        protected void onPostExecute(String message) {
            mAuthTask = null;
            showProgress(false);

            // show info
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

            // start another activity
            if(AppAuth.getInstance().isAuthenticated()) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                finish();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }


    }
}

