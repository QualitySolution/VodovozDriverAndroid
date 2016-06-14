package ru.qsolution.vodovoz.driver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
import ru.qsolution.vodovoz.driver.Services.LocationService;
import ru.qsolution.vodovoz.driver.Workers.ServiceWorker;

public class RouteListsActivity extends AppCompatActivity {

    private ArrayList<RouteList> routeLists;
    private ListView list;
    private RouteListAdapter adapter;

    private Context context;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_lists);
        list = (ListView) findViewById(R.id.routeListsListView);
        context = this.getApplicationContext();
        sharedPref = context.getSharedPreferences(getString(R.string.auth_file_key), Context.MODE_PRIVATE);

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
                Intent intent = new Intent(RouteListsActivity.this, OrdersActivity.class);
                intent.putExtra("RouteListId", routeList.Id);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate your main_menu into the menu
        getMenuInflater().inflate(R.menu.route_lists_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        if (item.getItemId() == R.id.taskChangeUserBtn)
        {
            ServiceWorker.StopLocationService(this);

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.remove("Authkey");
            editor.apply();

            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        } else if (item.getItemId() == R.id.taskExitBtn) {
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.putExtra("EXIT", true);
            startActivity(i);
            finish();
        } else if (item.getItemId() == R.id.taskShutdownBtn) {
            ServiceWorker.StopLocationService(this);
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.putExtra("EXIT", true);
            startActivity(i);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        adapter = new RouteListAdapter(this, routeLists);
        list.setAdapter(adapter);
        super.onResume();
    }
}
