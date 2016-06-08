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
import java.util.concurrent.ExecutionException;
import ru.qsolution.vodovoz.driver.AsyncTasks.*;

public class LoginActivity extends AppCompatActivity {

    EditText usernameInput;
    EditText passwordInput;
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Checking authorization
        Context context = this.getApplicationContext();
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.auth_file_key), Context.MODE_PRIVATE);
        if (sharedPref.contains("Authkey")) {
            try {
                Boolean authOk = new CheckAuthTask().execute(sharedPref.getString("Authkey", "")).get();
                //If already authorized - open route lists activity
                if (authOk != null && authOk) {
                    Intent i = new Intent(this, RouteListsActivity.class);
                    startActivity(i);
                    finish();
                } else if (authOk != null){
                    SharedPreferences.Editor edit = sharedPref.edit();
                    edit.remove("Authkey");
                    edit.apply();
                }
            } catch (InterruptedException | ExecutionException e) {
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
                    String passString= new String(Hex.encodeHex(DigestUtils.sha1(passwordInput.getText().toString())));
                    Object result = new LoginTask().execute(usernameInput.getText().toString(), passString).get();

                    Context context = LoginActivity.this.getApplicationContext();

                    if (result == null) {
                        Toast toast = Toast.makeText(context, R.string.authorization_failed, Toast.LENGTH_LONG);
                        toast.show();
                    }
                    else {
                        SharedPreferences sharedPref = context.getSharedPreferences(
                                getString(R.string.auth_file_key), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("Authkey", result.toString());
                        editor.apply();
                        Intent i = new Intent(LoginActivity.this, RouteListsActivity.class);
                        startActivity(i);
                        finish();
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

