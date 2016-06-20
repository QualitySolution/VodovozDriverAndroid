package ru.qsolution.vodovoz.driver;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.concurrent.ExecutionException;

import ru.qsolution.vodovoz.driver.AsyncTasks.*;

/**
 * Created by Andrei Vinogradov on 07.06.16.
 * (c) Quality Solution Ltd.
 */

public class LoginActivity extends AppCompatActivity implements IAsyncTaskListener<AsyncTaskResult<Boolean>> {
    private EditText usernameInput;
    private EditText passwordInput;
    private Button loginButton;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        loginButton = (Button) findViewById(R.id.loginButton);
        usernameInput = (EditText) findViewById(R.id.loginInput);
        passwordInput = (EditText) findViewById(R.id.passwordInput);

        Context context = this.getApplicationContext();
        sharedPref = context.getSharedPreferences(getString(R.string.auth_file_key), Context.MODE_PRIVATE);
        Bundle extras = getIntent().getExtras();

        //Checking app API version
        CheckAppVersionTask checkAppVersionTask = new CheckAppVersionTask();
        checkAppVersionTask.addListener(this);
        checkAppVersionTask.execute(BuildConfig.VERSION_CODE);

        //Checking if needed to close app
        if (extras != null) {
            if (extras.getBoolean("EXIT", false)) {
                finish();
                return;
            }
        }

        //Authorization logic
        loginButton.setEnabled(false);
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

        usernameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s) || TextUtils.isEmpty(passwordInput.getText())) {
                    loginButton.setEnabled(false);
                } else {
                    loginButton.setEnabled(true);
                }

            }
        });

        passwordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s) || TextUtils.isEmpty(usernameInput.getText())) {
                    loginButton.setEnabled(false);
                } else {
                    loginButton.setEnabled(true);
                }

            }
        });
    }

    @Override
    public void AsyncTaskCompleted(AsyncTaskResult<Boolean> result) {
        if (result.getException() == null && !result.getResult()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Требуется обновление")
                    .setMessage("Используется устаревшая версия программы. " +
                            "Для продолжения требуется скачать обновленное приложение.")
                    .setCancelable(false)
                    .setNegativeButton("Выход", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            LoginActivity.this.finish();
                        }
                    })
                    .setPositiveButton("Скачать", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            LoginActivity.this.finish();
                            String url = "http://files.qsolution.ru/Vodovoz/VodovozDrivers.apk";
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(url));
                            startActivity(i);
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            //Checking authorization
            if (sharedPref.contains("Authkey")) {
                CheckAuthListener listener = new CheckAuthListener(this, sharedPref);
                CheckAuthTask task = new CheckAuthTask();
                task.addListener(listener);
                task.execute(sharedPref.getString("Authkey", ""));
            }
        }
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


