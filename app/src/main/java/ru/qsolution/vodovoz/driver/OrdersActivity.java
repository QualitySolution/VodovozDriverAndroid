package ru.qsolution.vodovoz.driver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import ru.qsolution.vodovoz.driver.AsyncTasks.GetOrdersTask;
import ru.qsolution.vodovoz.driver.AsyncTasks.IAsyncTaskListener;
import ru.qsolution.vodovoz.driver.DTO.ShortOrder;
import ru.qsolution.vodovoz.driver.Services.LocationService;
import ru.qsolution.vodovoz.driver.Workers.ServiceWorker;

public class OrdersActivity extends AppCompatActivity implements IAsyncTaskListener<AsyncTaskResult<ArrayList<ShortOrder>>> {
    private OrdersAdapter adapter;
    private SharedPreferences sharedPref;
    private String routeListId;
    private Context context;
    private ListView list;

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
            GetOrdersTask task = new GetOrdersTask();
            task.addListener(this);
            task.execute(sharedPref.getString("Authkey", ""), routeListId);
        }
    }

    @Override
    public void AsyncTaskCompleted(AsyncTaskResult<ArrayList<ShortOrder>> result) {
        try {
            if (result.getException() == null && result.getResult() != null && result.getResult().size() > 0) {
                adapter = new OrdersAdapter(this, result.getResult());
                list.setAdapter(adapter);
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        ShortOrder order = adapter.getItem(position);
                        Intent intent = new Intent(OrdersActivity.this, TabbedOrderDetailedActivity.class);
                        intent.putExtra("OrderId", order.Id);
                        startActivity(intent);
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.orders_list_menu, menu);
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
        } else if (item.getItemId() == R.id.taskAcceptRouteListBtn) {
            ServiceWorker.StopLocationService(this);
            ServiceWorker.StartLocationService(this, routeListId);
            ActionMenuItemView accept = (ActionMenuItemView) findViewById(R.id.taskAcceptRouteListBtn);
            accept.setEnabled(false);
            accept.setTitle("Принят");
            //TODO
        }
        return super.onOptionsItemSelected(item);
    }
}
