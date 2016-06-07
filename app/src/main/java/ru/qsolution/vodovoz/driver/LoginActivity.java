package ru.qsolution.vodovoz.driver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class LoginActivity extends AppCompatActivity {

    EditText usernameInput;
    EditText passwordInput;
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Context context = this.getApplicationContext();
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.auth_file_key), Context.MODE_PRIVATE);
        if (sharedPref.contains("Authkey")) {
            try {
                Boolean result = new checkAuthTask().execute(sharedPref.getString("Authkey", "")).get();
                if (result){
                    Intent i = new Intent(this, RouteListsActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    SharedPreferences.Editor edit = sharedPref.edit();
                    edit.remove("Authkey");
                    edit.apply();
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        loginButton = (Button) findViewById(R.id.loginButton);
        usernameInput = (EditText) findViewById(R.id.loginInput);
        passwordInput = (EditText) findViewById(R.id.passwordInput);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // Create password MD5 Hash
                    String passString= new String(Hex.encodeHex(DigestUtils.sha1(passwordInput.getText().toString())));
                    Object result = new loginTask().execute(usernameInput.getText().toString(), passString).get();

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


    class loginTask extends AsyncTask<String, Void, Object> {
        @Override
        protected Object doInBackground(String... args) {

            String METHOD_NAME = "Auth";

            HttpTransportSE httpTransport = new HttpTransportSE(NetworkWorker.ServiceUrl);

            SoapObject request = new SoapObject(NetworkWorker.Namespace, METHOD_NAME);
            request.addProperty("login", args[0]);
            request.addProperty("password", args[1]);

            SoapSerializationEnvelope envelope = NetworkWorker.CreateEnvelope(request);

            try {
                httpTransport.call(NetworkWorker.GetSoapAction(METHOD_NAME), envelope);
            } catch (IOException | XmlPullParserException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                return envelope.getResponse();
            } catch (SoapFault e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }
    }

    class checkAuthTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... args) {

            String METHOD_NAME = "CheckAuth";

            HttpTransportSE httpTransport = new HttpTransportSE(NetworkWorker.ServiceUrl);

            SoapObject request = new SoapObject(NetworkWorker.Namespace, METHOD_NAME);
            request.addProperty("authKey", args[0]);

            SoapSerializationEnvelope envelope = NetworkWorker.CreateEnvelope(request);

            try {
                httpTransport.call(NetworkWorker.GetSoapAction(METHOD_NAME), envelope);
            } catch (IOException | XmlPullParserException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                SoapPrimitive response = (SoapPrimitive)envelope.getResponse();
                Boolean boolResponse = Boolean.parseBoolean(response.getValue().toString());

                return boolResponse;
            } catch (SoapFault e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }
    }
}

