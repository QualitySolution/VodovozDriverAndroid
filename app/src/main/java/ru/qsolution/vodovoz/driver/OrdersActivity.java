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
import android.widget.ListView;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import ru.qsolution.vodovoz.driver.ArrayAdapters.OrdersAdapter;
import ru.qsolution.vodovoz.driver.AsyncTasks.GetOrdersTask;
import ru.qsolution.vodovoz.driver.DTO.ShortOrder;
import ru.qsolution.vodovoz.driver.Services.LocationService;
import ru.qsolution.vodovoz.driver.Workers.ServiceWorker;

public class OrdersActivity extends AppCompatActivity {
    private ArrayList<ShortOrder> ordersList;
    private ListView list;
    private OrdersAdapter adapter;
    private Context context;
    private SharedPreferences sharedPref;
    private String routeListId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        list = (ListView) findViewById(R.id.ordersListView);

        context = this.getApplicationContext();
        sharedPref = context.getSharedPreferences(getString(R.string.auth_file_key), Context.MODE_PRIVATE);
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            try {
                routeListId = extras.getString("RouteListId");
                ordersList = new GetOrdersTask().execute(sharedPref.getString("Authkey", ""), routeListId).get();
                if (ordersList != null) {
                    adapter = new OrdersAdapter(this, ordersList);
                    list.setAdapter(adapter);
                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            ShortOrder order = adapter.getItem(position);
                            Intent intent = new Intent(OrdersActivity.this, TabbedOrderDetailedActivity.class);
                            intent.putExtra("OrderId", order.Id);
                            startActivity(intent);
                        }
                    });
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate your main_menu into the menu
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
