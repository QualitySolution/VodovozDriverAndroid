package ru.qsolution.vodovoz.driver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import ru.qsolution.vodovoz.driver.AsyncTasks.*;

/**
 * Created by Andrei Vinogradov on 07.06.16.
 * (c) Quality Solution Ltd.
 */

public class LoginActivity extends AppCompatActivity {
    private EditText usernameInput;
    private EditText passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context context = this.getApplicationContext();
        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.auth_file_key), Context.MODE_PRIVATE);
        Bundle extras = getIntent().getExtras();

        //Checking if needed to close app
        if (extras != null) {
            if (extras.getBoolean("EXIT", false)) {
                finish();
                return;
            }
        }
        //Checking authorization
        if (sharedPref.contains("Authkey")) {
            CheckAuthListener listener = new CheckAuthListener(this, sharedPref);
            CheckAuthTask task = new CheckAuthTask();
            task.addListener(listener);
            task.execute(sharedPref.getString("Authkey", ""));
        }

        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Button loginButton = (Button) findViewById(R.id.loginButton);
        usernameInput = (EditText) findViewById(R.id.loginInput);
        passwordInput = (EditText) findViewById(R.id.passwordInput);

        //Authorization logic
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create password MD5 Hash
                String passString = new String(Hex.encodeHex(DigestUtils.sha1(passwordInput.getText().toString())));
                Context context = LoginActivity.this.getApplicationContext();
                SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.auth_file_key), Context.MODE_PRIVATE);
                LoginListener listener = new LoginListener(context, sharedPref);

                LoginTask task = new LoginTask();
                task.addListener(listener);
                task.execute(usernameInput.getText().toString(), passString);

            }
        });
    }

    private class LoginListener implements IAsyncTaskListener<AsyncTaskResult<String>> {
        private final Context context;
        private final SharedPreferences sharedPref;

        public LoginListener(Context context, SharedPreferences sharedPref) {
            this.context = context;
            this.sharedPref = sharedPref;
        }

        @Override
        public void AsyncTaskCompleted(AsyncTaskResult<String> result) {
            try {
                //If authorization was unsuccessful
                if (result.getException() == null && result.getResult() == null) {
                    Toast toast = Toast.makeText(context, R.string.authorization_failed, Toast.LENGTH_LONG);
                    toast.show();
                }
                //If authorization was success
                else if (result.getException() == null && result.getResult() != null) {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("Authkey", result.getResult());
                    editor.apply();
                    Intent i = new Intent(context, RouteListsActivity.class);
                    startActivity(i);
                    finish();
                }
                //If exception occurred
                else {
                    Toast toast = Toast.makeText(context, "Не удалось подключиться к серверу.", Toast.LENGTH_LONG);
                    toast.show();
                    throw result.getException();
                }
            } catch (Exception e) {
                if (BuildConfig.DEBUG)
                    e.printStackTrace();
            }
        }
    }

    private class CheckAuthListener implements IAsyncTaskListener<AsyncTaskResult<Boolean>> {
        private final Context context;
        private final SharedPreferences sharedPref;

        public CheckAuthListener(Context context, SharedPreferences sharedPref) {
            this.context = context;
            this.sharedPref = sharedPref;
        }

        @Override
        public void AsyncTaskCompleted(AsyncTaskResult<Boolean> result) {
            try {
                if (result.getException() == null && result.getResult()) {
                    Intent i = new Intent(context, RouteListsActivity.class);
                    startActivity(i);
                    finish();
                }
                //If wrong or expired session - delete session key
                else if (result.getException() == null && !result.getResult()) {
                    SharedPreferences.Editor edit = sharedPref.edit();
                    edit.remove("Authkey");
                    edit.apply();
                }
                //If exception
                else {
                    Toast toast = Toast.makeText(context, "Не удалось подключиться к серверу.", Toast.LENGTH_LONG);
                    toast.show();
                    throw result.getException();
                }
            } catch (Exception e) {
                if (BuildConfig.DEBUG)
                    e.printStackTrace();
            }
        }
    }
}


