package ru.qsolution.vodovoz.driver;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    EditText usernameInput;
    EditText passwordInput;
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginButton=(Button)findViewById(R.id.loginButton);
        usernameInput=(EditText)findViewById(R.id.loginInput);
        passwordInput=(EditText)findViewById(R.id.passwordInput);

        //tx1.setVisibility(View.GONE);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if(usernameInput.getText().toString().equals("admin") &&

                        passwordInput.getText().toString().equals("admin")) {
                    Toast.makeText(getApplicationContext(), R.string.authorization_in_progress,Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), R.string.authorization_failed,Toast.LENGTH_SHORT).show();

                    //tx1.setVisibility(View.VISIBLE);
                    //tx1.setBackgroundColor(Color.RED);
                    //tx1.setText(Integer.toString(counter));
                }*/
            }
        });
    }
}
