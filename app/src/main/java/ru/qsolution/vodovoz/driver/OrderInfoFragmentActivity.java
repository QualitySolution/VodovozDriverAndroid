package ru.qsolution.vodovoz.driver;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.opengl.Visibility;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import ru.qsolution.vodovoz.driver.DTO.Order;

public class OrderInfoFragmentActivity extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String SERIALIZED_ORDER = "serialized_order";

    private Order order;

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
            TextView orderNumber = (TextView) rootView.findViewById(R.id.orderNumber);
            TextView orderClient = (TextView) rootView.findViewById(R.id.orderClient);
            TextView orderAddress = (TextView) rootView.findViewById(R.id.orderAddress);
            TextView orderStatus = (TextView) rootView.findViewById(R.id.orderStatus);
            TextView orderDeliveryTime = (TextView) rootView.findViewById(R.id.orderDeliveryTime);
            TextView orderComment = (TextView) rootView.findViewById(R.id.orderComment);
            TextView orderCommentTitle = (TextView) rootView.findViewById(R.id.orderCommentTitle);
            TextView deliveryPointComment = (TextView) rootView.findViewById(R.id.deliveryPointComment);
            TextView deliveryPointCommentTitle = (TextView) rootView.findViewById(R.id.deliveryPointCommentTitle);
            Button getRoute = (Button) rootView.findViewById(R.id.buttonGetRoute);

            orderNumber.setText(order.Title);
            orderClient.setText(order.Counterparty);
            orderAddress.setText(order.Address);
            orderStatus.setText(order.RouteListItemStatus);
            orderDeliveryTime.setText(order.DeliverySchedule);
            if (order.OrderComment == null || order.OrderComment.equals("")) {
                orderCommentTitle.setVisibility(View.GONE);
                orderComment.setVisibility(View.GONE);
            } else {
                orderComment.setText(order.OrderComment);
            }

            if (order.DeliveryPointComment == null || order.DeliveryPointComment.equals("")) {
                deliveryPointComment.setVisibility(View.GONE);
                deliveryPointCommentTitle.setVisibility(View.GONE);
            } else {
                deliveryPointComment.setText(order.OrderComment);
            }

            if (order.RouteListItemStatus.equals("В пути"))
                orderStatus.setTextColor(Color.parseColor("#36b032"));

            //Setting up Get Route button
            if (order.Latitude != null && order.Longitude != null) {
                getRoute.setEnabled(true);
                getRoute.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent("ru.yandex.yandexnavi.action.BUILD_ROUTE_ON_MAP");
                        intent.setPackage("ru.yandex.yandexnavi");

                        PackageManager pm = getActivity().getPackageManager();
                        List<ResolveInfo> infos = pm.queryIntentActivities(intent, 0);

                        // Checking for Yandex.Navigator is present.
                        if (infos == null || infos.size() == 0) {
                            // If no - open Google Play Market.
                            intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse("market://details?id=ru.yandex.yandexnavi"));
                        } else {
                            intent.putExtra("lat_to", order.Latitude);
                            intent.putExtra("lon_to", order.Longitude);
                        }
                        startActivity(intent);
                    }
                });
            }
        } else {
            getActivity().finish();
        }

        return rootView;
    }
}