package ru.qsolution.vodovoz.driver;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.ksoap2.serialization.SoapObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import ru.qsolution.vodovoz.driver.ArrayAdapters.RouteListAdapter;
import ru.qsolution.vodovoz.driver.AsyncTasks.CheckAuthTask;
import ru.qsolution.vodovoz.driver.AsyncTasks.GetRouteListsTask;
import ru.qsolution.vodovoz.driver.DTO.RouteList;

public class RouteListsActivity extends AppCompatActivity {

    private ArrayList<RouteList> routeLists;
    private ListView list;
    private RouteListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_lists);
        list = (ListView) findViewById(R.id.routeListsListView);
        Context context = this.getApplicationContext();
        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.auth_file_key), Context.MODE_PRIVATE);

        try {
            routeLists = new GetRouteListsTask().execute(sharedPref.getString("Authkey", "")).get();
            adapter = new RouteListAdapter(this, routeLists);

            list.setAdapter(adapter);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RouteList routeList = adapter.getItem(position);
                //TODO: Open OrdersActivity for that route list.
            }
        });

    }
}
