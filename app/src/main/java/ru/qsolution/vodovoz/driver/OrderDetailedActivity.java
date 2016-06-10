package ru.qsolution.vodovoz.driver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.ExecutionException;

import ru.qsolution.vodovoz.driver.AsyncTasks.GetOrderDetailedTask;
import ru.qsolution.vodovoz.driver.DTO.Order;

public class OrderDetailedActivity extends AppCompatActivity {
    Order order;
    TextView orderNumber;
    TextView orderClient;
    TextView orderAddress;
    TextView orderStatus;
    TextView orderDeliveryTime;
    TextView orderContactPerson;
    TextView orderContactPhone;
    Button getRoute;
    Button callContactPerson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detailed);

        Context context = this.getApplicationContext();
        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.auth_file_key), Context.MODE_PRIVATE);
        Bundle extras = getIntent().getExtras();

        try {
            order = new GetOrderDetailedTask()
                    .execute(sharedPref.getString("Authkey", ""), extras.getString("OrderId")).get();

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        if (order != null) {
            orderNumber = (TextView) findViewById(R.id.orderNumber);
            orderClient = (TextView) findViewById(R.id.orderClient);
            orderAddress = (TextView) findViewById(R.id.orderAddress);
            orderStatus = (TextView) findViewById(R.id.orderStatus);
            orderDeliveryTime = (TextView) findViewById(R.id.orderDeliveryTime);
            orderContactPerson = (TextView) findViewById(R.id.orderContactPerson);
            orderContactPhone = (TextView) findViewById(R.id.orderContactPhone);
            getRoute = (Button) findViewById(R.id.buttonGetRoute);
            callContactPerson = (Button) findViewById(R.id.buttonCall);

            orderNumber.setText(order.Title);
            orderClient.setText(order.Counterparty);
            orderAddress.setText(order.Address);
            orderStatus.setText(order.OrderStatus);
            orderDeliveryTime.setText(order.DeliverySchedule);
            orderContactPerson.setText(order.Contact);
            orderContactPhone.setText(order.Phone);

            if (order.Latitude != null && order.Longitude != null) {
                getRoute.setEnabled(true);
                getRoute.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent("ru.yandex.yandexnavi.action.BUILD_ROUTE_ON_MAP");
                        intent.setPackage("ru.yandex.yandexnavi");

                        PackageManager pm = getPackageManager();
                        List<ResolveInfo> infos = pm.queryIntentActivities(intent, 0);

                        // Проверяем, установлен ли Яндекс.Навигатор
                        if (infos == null || infos.size() == 0) {
                            // Если нет - будем открывать страничку Навигатора в Google Play
                            intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse("market://details?id=ru.yandex.yandexnavi"));
                        } else {
                            intent.putExtra("lat_to", order.Latitude);
                            intent.putExtra("lon_to", order.Longitude);
                        }

                        // Запускаем нужную Activity
                        startActivity(intent);
                    }
                });
            }

            if (order.GetPhoneNumberUri() != null) {
                callContactPerson.setEnabled(true);
                callContactPerson.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(order.GetPhoneNumberUri()));
                        startActivity(intent);
                    }
                });
            } else {
                orderContactPhone.setText("Телефон не указан");
            }
        }
    }
}