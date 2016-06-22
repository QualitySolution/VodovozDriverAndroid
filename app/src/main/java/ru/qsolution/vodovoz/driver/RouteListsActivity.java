package ru.qsolution.vodovoz.driver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;

import ru.qsolution.vodovoz.driver.ArrayAdapters.RouteListAdapter;
import ru.qsolution.vodovoz.driver.AsyncTasks.AsyncTaskResult;
import ru.qsolution.vodovoz.driver.AsyncTasks.GetRouteListsTask;
import ru.qsolution.vodovoz.driver.AsyncTasks.IAsyncTaskListener;
import ru.qsolution.vodovoz.driver.DTO.RouteList;
import ru.qsolution.vodovoz.driver.Workers.ServiceWorker;

public class RouteListsActivity extends AppCompatActivity implements IAsyncTaskListener<AsyncTaskResult<ArrayList<RouteList>>>, SwipeRefreshLayout.OnRefreshListener {
    private ActionBarDrawerToggle drawerToggle;
    private RouteListAdapter adapter;
    private SharedPreferences sharedPref;
    private ListView list;
    private ListView drawerList;
    private Context context;
    private SwipeRefreshLayout swipeRefreshLayout;
    private DrawerLayout drawerLayout;

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
                extras.clear();
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.putExtra("EXIT", true);
                startActivity(i);
                finish();
                return;
            }
            if (extras.getBoolean("LOGOUT", false)) {
                extras.clear();
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
                return;
            }
        }
        // Configuring left menu
        drawerList = (ListView) findViewById(R.id.nav_list);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        addDrawerItems();
        setupDrawer();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //Configuring swipe to refresh
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(this);

        //Retrieving route lists
        refreshRouteLists();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        refreshRouteLists();
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
        // Activate the navigation drawer toggle
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

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
        refreshRouteLists();
    }

    private void addDrawerItems() {
        final String[] drawerItems = getResources().getStringArray(R.array.left_menu_items_array);
        ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, drawerItems);
        drawerList.setAdapter(mAdapter);

        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (drawerItems[position].equals(getResources().getString(R.string.route_lists))) {
                    refreshRouteLists();
                } else if (drawerItems[position].equals(getResources().getString(R.string.chat))) {
                    Intent i = new Intent(getApplicationContext(), ChatActivity.class);
                    startActivity(i);
                }
                drawerLayout.closeDrawers();
            }
        });
    }

    private void setupDrawer() {
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }
        };

        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(drawerToggle);
    }

    private void refreshRouteLists() {
        GetRouteListsTask task = new GetRouteListsTask(RouteListsActivity.this);
        task.addListener(RouteListsActivity.this);
        task.execute(sharedPref.getString("Authkey", ""));
    }
}
