package ru.qsolution.vodovoz.driver;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import ru.qsolution.vodovoz.driver.DTO.Order;

public class OrderInfoFragmentActivity extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String SERIALIZED_ORDER = "serialized_order";

    private TextView orderNumber;
    private TextView orderClient;
    private TextView orderAddress;
    private TextView orderStatus;
    private TextView orderDeliveryTime;
    private Button getRoute;

    private Order order;

    public OrderInfoFragmentActivity() {
    }

    public static OrderInfoFragmentActivity newInstance(int sectionNumber, Order order) {
        OrderInfoFragmentActivity fragment = new OrderInfoFragmentActivity();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putSerializable(SERIALIZED_ORDER, order);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_order_info_fragment, container, false);

        order = (Order) getArguments().getSerializable(SERIALIZED_ORDER);

        if (order != null) {
            orderNumber = (TextView) rootView.findViewById(R.id.orderNumber);
            orderClient = (TextView) rootView.findViewById(R.id.orderClient);
            orderAddress = (TextView) rootView.findViewById(R.id.orderAddress);
            orderStatus = (TextView) rootView.findViewById(R.id.orderStatus);
            orderDeliveryTime = (TextView) rootView.findViewById(R.id.orderDeliveryTime);
            getRoute = (Button) rootView.findViewById(R.id.buttonGetRoute);

            orderNumber.setText(order.Title);
            orderClient.setText(order.Counterparty);
            orderAddress.setText(order.Address);
            orderStatus.setText(order.OrderStatus);
            orderDeliveryTime.setText(order.DeliverySchedule);

            if (order.OrderStatus.equals("В пути"))
                orderStatus.setTextColor(Color.parseColor("#36b032"));

            if (order.Latitude != null && order.Longitude != null) {
                getRoute.setEnabled(true);
                getRoute.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent("ru.yandex.yandexnavi.action.BUILD_ROUTE_ON_MAP");
                        intent.setPackage("ru.yandex.yandexnavi");

                        PackageManager pm = getActivity().getPackageManager();
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
        }
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();

    }
}