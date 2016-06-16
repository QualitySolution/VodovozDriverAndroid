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
    EditText usernameInput;
    EditText passwordInput;
    Button loginButton;

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
            try {
                AsyncTaskResult<Boolean> authResult = new CheckAuthTask().execute(sharedPref.getString("Authkey", "")).get();
                //If already authorized - open route lists activity
                if (authResult.getException() == null && authResult.getResult()) {
                    Intent i = new Intent(this, RouteListsActivity.class);
                    startActivity(i);
                    finish();
                }
                //If wrong or expired session - delete session key
                else if (authResult.getException() == null && !authResult.getResult()) {
                    SharedPreferences.Editor edit = sharedPref.edit();
                    edit.remove("Authkey");
                    edit.apply();
                }
                //If exception
                else {
                    Toast toast = Toast.makeText(this, "Не удалось подключиться к серверу.", Toast.LENGTH_LONG);
                    toast.show();
                    throw authResult.getException();
                }
            } catch (Exception e) {
                if (BuildConfig.DEBUG)
                    e.printStackTrace();
            }
        }

        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        loginButton = (Button) findViewById(R.id.loginButton);
        usernameInput = (EditText) findViewById(R.id.loginInput);
        passwordInput = (EditText) findViewById(R.id.passwordInput);

        //Authorization logic
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // Create password MD5 Hash
                    String passString = new String(Hex.encodeHex(DigestUtils.sha1(passwordInput.getText().toString())));
                    AsyncTaskResult<String> result = new LoginTask().execute(usernameInput.getText().toString(), passString).get();

                    Context context = LoginActivity.this.getApplicationContext();

                    //If authorization was unsuccessful
                    if (result.getException() == null && result.getResult() == null) {
                        Toast toast = Toast.makeText(context, R.string.authorization_failed, Toast.LENGTH_LONG);
                        toast.show();
                    }
                    //If authorization was success
                    else if (result.getException() == null && result.getResult() != null) {
                        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.auth_file_key), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("Authkey", result.getResult());
                        editor.apply();
                        Intent i = new Intent(LoginActivity.this, RouteListsActivity.class);
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
        });
    }
}

