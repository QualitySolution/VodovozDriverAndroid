package ru.qsolution.vodovoz.driver;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.concurrent.ExecutionException;

import ru.qsolution.vodovoz.driver.AsyncTasks.CheckAuthTask;
import ru.qsolution.vodovoz.driver.AsyncTasks.GetRouteListsTask;

public class RouteListsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_lists);

        Context context = this.getApplicationContext();
        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.auth_file_key), Context.MODE_PRIVATE);

        try {
            Object routeLists = new GetRouteListsTask().execute(sharedPref.getString("Authkey", "")).get();
            if (true) {
                String asd = "asd";
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

    }
}
