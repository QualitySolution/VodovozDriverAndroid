package ru.qsolution.vodovoz.driver;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.ksoap2.serialization.SoapObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import ru.qsolution.vodovoz.driver.AsyncTasks.CheckAuthTask;
import ru.qsolution.vodovoz.driver.AsyncTasks.GetRouteListsTask;
import ru.qsolution.vodovoz.driver.DTO.RouteList;

public class RouteListsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_lists);

        Context context = this.getApplicationContext();
        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.auth_file_key), Context.MODE_PRIVATE);

        try {
            Object routeListsObj = new GetRouteListsTask().execute(sharedPref.getString("Authkey", "")).get();
            if (routeListsObj instanceof SoapObject) {
                SoapObject routeLists = (SoapObject) routeListsObj;
                for (int i = 0; i < routeLists.getPropertyCount(); i++) {
                    Object property = routeLists.getProperty(i);
                    if (property instanceof SoapObject) {
                        SoapObject soapObject = (SoapObject) property;
                        RouteList routeList = new RouteList(soapObject);
                    }
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

    }
}
