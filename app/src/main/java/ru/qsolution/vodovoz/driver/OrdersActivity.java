package ru.qsolution.vodovoz.driver;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ActionMenuItemView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import ru.qsolution.vodovoz.driver.ArrayAdapters.OrdersAdapter;
import ru.qsolution.vodovoz.driver.AsyncTasks.AsyncTaskResult;
import ru.qsolution.vodovoz.driver.AsyncTasks.FinishRouteListTask;
import ru.qsolution.vodovoz.driver.AsyncTasks.GetOrdersTask;
import ru.qsolution.vodovoz.driver.AsyncTasks.IAsyncTaskListener;
import ru.qsolution.vodovoz.driver.DTO.Order;
import ru.qsolution.vodovoz.driver.DTO.ShortOrder;
import ru.qsolution.vodovoz.driver.Services.INotificationObserver;
import ru.qsolution.vodovoz.driver.Services.LocationService;
import ru.qsolution.vodovoz.driver.Services.MyFirebaseMessagingService;
import ru.qsolution.vodovoz.driver.Workers.ServiceWorker;

public class OrdersActivity extends AppCompatActivity implements
        IAsyncTaskListener<AsyncTaskResult<ArrayList<ShortOrder>>>,
        SwipeRefreshLayout.OnRefreshListener,
        INotificationObserver {
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 42;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 24;

    private OrdersAdapter adapter;
    private SharedPreferences sharedPref;
    private String routeListId;
    private Context context;
    private ListView list;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<ShortOrder> orders = new ArrayList<>();
    private ArrayList<ShortOrder> filteredOrders = new ArrayList<>();
    private MenuItem showAllOrdersMenuItem;
    private MenuItem useGPSTimeMenuItem;
    private ListView drawerList;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private Boolean isActive = false;

    @Override
    protected void onStart() {
        super.onStart();
        isActive = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isActive = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        context = this.getApplicationContext();
        list = (ListView) findViewById(R.id.ordersListView);
        Bundle extras = getIntent().getExtras();
        sharedPref = context.getSharedPreferences(getString(R.string.auth_file_key), Context.MODE_PRIVATE);

        if (extras != null) {
            routeListId = extras.getString("RouteListId");
            refreshOrders();
            swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
            swipeRefreshLayout.setOnRefreshListener(this);
        }
        // Configuring left menu
        drawerList = (ListView) findViewById(R.id.nav_list);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        addDrawerItems();
        setupDrawer();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        MyFirebaseMessagingService.AddObserver(this);
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
    public void AsyncTaskCompleted(AsyncTaskResult<ArrayList<ShortOrder>> result) {
        try {
            if (result.getException() == null && result.getResult() != null && result.getResult().size() > 0) {
                orders = result.getResult();
                for (ShortOrder order : orders)
                    if (order.OrderStatus.equals("В пути"))
                        filteredOrders.add(order);

                if (showAllOrdersMenuItem.isChecked())
                    adapter = new OrdersAdapter(this, orders);
                else
                    adapter = new OrdersAdapter(this, filteredOrders);

                list.setAdapter(adapter);
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        ShortOrder order = adapter.getItem(position);
                        Intent intent = new Intent(OrdersActivity.this, TabbedOrderDetailedActivity.class);
                        intent.putExtra("OrderId", order.Id);
                        startActivityForResult(intent, 1);
                    }
                });
            } else if (result.getException() == null && (result.getResult() == null || result.getResult().size() == 0)) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new String[]{"Заказы отсутствуют"});
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
        if (noEnRouteOrders())
            runCloseRouteListDlg();
    }

    private boolean noEnRouteOrders() {
        if (orders == null)
            return false;
        for (ShortOrder order : orders) {
            if (order.OrderStatus.equals("В пути"))
                return false;
        }
        return true;
    }

    private void runCloseRouteListDlg() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Завершить маршрутный лист?")
                .setMessage("В данном маршрутном листе не осталось невыполненных заказов. Завершить его? " +
                        "После завершения данный маршрутный лист уже нельзя будет открыть.")
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        FinishRouteListTask task = new FinishRouteListTask();
                        FinishRouteListListener listener = new FinishRouteListListener(OrdersActivity.this.getApplicationContext());
                        task.addListener(listener);
                        task.execute(sharedPref.getString("Authkey", ""), routeListId);

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        Dialog dlg = builder.create();
        dlg.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK)
            refreshOrders();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.orders_list_menu, menu);
        showAllOrdersMenuItem = menu.findItem(R.id.taskShowAll);
        showAllOrdersMenuItem.setChecked(sharedPref.getBoolean("ShowAllOrders", true));
        useGPSTimeMenuItem = menu.findItem(R.id.taskUseGPSTime);
        useGPSTimeMenuItem.setChecked(sharedPref.getBoolean("UseGPSTime", true));

        if (!sharedPref.contains("ShowAllOrders")) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("ShowAllOrders", true);
            editor.apply();
        }
        if (!sharedPref.contains("UseGPSTime")) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("UseGPSTime", true);
            editor.apply();
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (routeListId.equals(LocationService.RouteListId)) {
            MenuItem accept = menu.findItem(R.id.taskAcceptRouteListBtn);
            if (accept != null) {
                accept.setEnabled(false);
                accept.setTitle("Принят");
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Activate the navigation drawer toggle
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        if (item.getItemId() == R.id.taskShowAll) {
            item.setChecked(!item.isChecked());
            if (item.isChecked())
                adapter = new OrdersAdapter(this, orders);
            else
                adapter = new OrdersAdapter(this, filteredOrders);
            list.setAdapter(adapter);

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("ShowAllOrders", item.isChecked());
            editor.apply();
        } else if (item.getItemId() == R.id.taskUseGPSTime) {
            item.setChecked(!item.isChecked());
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("UseGPSTime", item.isChecked());
            editor.apply();
        } else if (item.getItemId() == R.id.taskChangeUserBtn) {
            ServiceWorker.StopLocationService(this);

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.remove("Authkey");
            editor.apply();

            Intent i = new Intent(getApplicationContext(), RouteListsActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.putExtra("LOGOUT", true);
            startActivity(i);
            finish();
        } else if (item.getItemId() == R.id.taskExitBtn) {
            Intent i = new Intent(getApplicationContext(), RouteListsActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.putExtra("EXIT", true);
            startActivity(i);
            finish();
        } else if (item.getItemId() == R.id.taskShutdownBtn) {
            ServiceWorker.StopLocationService(this);
            Intent i = new Intent(getApplicationContext(), RouteListsActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.putExtra("EXIT", true);
            startActivity(i);
            finish();
        } else if (item.getItemId() == R.id.taskAcceptRouteListBtn) {
            final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                return super.onOptionsItemSelected(item);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
                return super.onOptionsItemSelected(item);
            }
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("GPS отключен")
                        .setMessage("Для продолжения требуется включить GPS.")
                        .setNeutralButton("Ок", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                ServiceWorker.StopLocationService(this);
                ServiceWorker.StartLocationService(this, routeListId);
                ActionMenuItemView accept = (ActionMenuItemView) findViewById(R.id.taskAcceptRouteListBtn);
                accept.setEnabled(false);
                accept.setTitle("Принят");
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
                    } else {
                        ServiceWorker.StopLocationService(this);
                        ServiceWorker.StartLocationService(this, routeListId);
                        ActionMenuItemView accept = (ActionMenuItemView) findViewById(R.id.taskAcceptRouteListBtn);
                        accept.setEnabled(false);
                        accept.setTitle("Принят");
                    }
                } else {
                    Toast toast = Toast.makeText(context, "Для использования данной функции необходим доступ к местоположению.", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
            case MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
                    } else {
                        ServiceWorker.StopLocationService(this);
                        ServiceWorker.StartLocationService(this, routeListId);
                        ActionMenuItemView accept = (ActionMenuItemView) findViewById(R.id.taskAcceptRouteListBtn);
                        accept.setEnabled(false);
                        accept.setTitle("Принят");
                    }
                } else {
                    Toast toast = Toast.makeText(context, "Для использования данной функции необходим доступ к местоположению.", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        }
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        refreshOrders();
    }

    private void addDrawerItems() {
        final String[] drawerItems = getResources().getStringArray(R.array.left_menu_items_array);
        ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, drawerItems);
        drawerList.setAdapter(mAdapter);

        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (drawerItems[position].equals(getResources().getString(R.string.route_lists))) {
                    OrdersActivity.this.finish();
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

    private void refreshOrders() {
        GetOrdersTask task = new GetOrdersTask(this);
        task.addListener(this);
        task.execute(sharedPref.getString("Authkey", ""), routeListId);
    }

    @Override
    public void HandleNotification() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                refreshOrders();
            }
        });
    }

    @Override
    public Boolean IsActive() {
        return isActive;
    }

    @Override
    public String NotificationType() {
        return "orderStatusChange";
    }

    private class FinishRouteListListener implements IAsyncTaskListener<AsyncTaskResult<Boolean>> {
        private final Context context;

        public FinishRouteListListener(Context context) {
            this.context = context;
        }

        @Override
        public void AsyncTaskCompleted(AsyncTaskResult<Boolean> result) {
            try {
                if (result.getException() == null && result.getResult()) {
                    finish();
                }
                //If not succeeded
                else if (result.getException() == null && !result.getResult()) {
                    //TODO
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
