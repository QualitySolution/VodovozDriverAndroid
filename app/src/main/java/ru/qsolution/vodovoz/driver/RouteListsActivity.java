package ru.qsolution.vodovoz.driver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import ru.qsolution.vodovoz.driver.ArrayAdapters.RouteListAdapter;
import ru.qsolution.vodovoz.driver.AsyncTasks.AsyncTaskResult;
import ru.qsolution.vodovoz.driver.AsyncTasks.GetRouteListsTask;
import ru.qsolution.vodovoz.driver.AsyncTasks.IAsyncTaskListener;
import ru.qsolution.vodovoz.driver.DTO.RouteList;
import ru.qsolution.vodovoz.driver.Workers.ServiceWorker;

public class RouteListsActivity extends AppCompatActivity implements IAsyncTaskListener<AsyncTaskResult<ArrayList<RouteList>>>, SwipeRefreshLayout.OnRefreshListener {
    private RouteListAdapter adapter;
    private SharedPreferences sharedPref;
    private ListView list;
    private Context context;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_lists);

        Bundle extras = getIntent().getExtras();
        list = (ListView) findViewById(R.id.routeListsListView);
        context = this.getApplicationContext();
        sharedPref = context.getSharedPreferences(getString(R.string.auth_file_key), Context.MODE_PRIVATE);

        if (extras != null) {
            if (extras.getBoolean("EXIT", false)) {
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.putExtra("EXIT", true);
                startActivity(i);
                finish();
                return;
            }
            if (extras.getBoolean("LOGOUT", false)) {
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
                return;
            }
        }
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        GetRouteListsTask task = new GetRouteListsTask(this);
        task.addListener(this);
        task.execute(sharedPref.getString("Authkey", ""));
    }

    @Override
    protected void onResume() {
        GetRouteListsTask task = new GetRouteListsTask(this);
        task.addListener(this);
        task.execute(sharedPref.getString("Authkey", ""));
        super.onResume();
    }

    @Override
    public void AsyncTaskCompleted(AsyncTaskResult<ArrayList<RouteList>> result) {
        try {
            if (result.getException() == null && result.getResult() != null && result.getResult().size() > 0) {
                adapter = new RouteListAdapter(this, result.getResult());
                list.setAdapter(adapter);
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        RouteList routeList = adapter.getItem(position);
                        Intent intent = new Intent(RouteListsActivity.this, OrdersActivity.class);
                        intent.putExtra("RouteListId", routeList.Id);
                        startActivity(intent);
                    }
                });
            } else if (result.getException() == null && (result.getResult() == null || result.getResult().size() == 0)) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new String[]{"Маршрутные листы отсутствуют"});
                list.setAdapter(adapter);
            } else {
                Toast toast = Toast.makeText(context, "Не удалось подключиться к серверу.", Toast.LENGTH_LONG);
                toast.show();
                finish();
                throw result.getException();
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG)
                e.printStackTrace();
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.route_lists_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.taskChangeUserBtn) {
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
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        GetRouteListsTask task = new GetRouteListsTask(RouteListsActivity.this);
        task.addListener(RouteListsActivity.this);
        task.execute(sharedPref.getString("Authkey", ""));
    }
}
