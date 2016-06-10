package ru.qsolution.vodovoz.driver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import ru.qsolution.vodovoz.driver.ArrayAdapters.OrdersAdapter;
import ru.qsolution.vodovoz.driver.AsyncTasks.GetOrdersTask;
import ru.qsolution.vodovoz.driver.DTO.ShortOrder;

public class OrdersActivity extends AppCompatActivity {
    private ArrayList<ShortOrder> ordersList;
    private ListView list;
    private OrdersAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        list = (ListView) findViewById(R.id.ordersListView);

        Context context = this.getApplicationContext();
        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.auth_file_key), Context.MODE_PRIVATE);
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            try {
                ordersList = new GetOrdersTask().execute(sharedPref.getString("Authkey", ""), extras.getString("RouteListId")).get();
                if (ordersList != null) {
                    adapter = new OrdersAdapter(this, ordersList);
                    list.setAdapter(adapter);
                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            ShortOrder order = adapter.getItem(position);
                            Intent intent = new Intent(OrdersActivity.this, OrderDetailedActivity.class);
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
}
