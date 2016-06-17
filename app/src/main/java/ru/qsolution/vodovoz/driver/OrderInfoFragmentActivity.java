package ru.qsolution.vodovoz.driver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ru.qsolution.vodovoz.driver.ArrayAdapters.OrderStatusAdapter;
import ru.qsolution.vodovoz.driver.AsyncTasks.AsyncTaskResult;
import ru.qsolution.vodovoz.driver.AsyncTasks.ChangeOrderStatusTask;
import ru.qsolution.vodovoz.driver.AsyncTasks.IAsyncTaskListener;
import ru.qsolution.vodovoz.driver.DTO.Order;

public class OrderInfoFragmentActivity extends Fragment implements IAsyncTaskListener<AsyncTaskResult<Boolean>>{
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String SERIALIZED_ORDER = "serialized_order";

    private Order order;
    private Spinner spinner;
    private ArrayAdapter<String> adapter;
    private String newStatus;

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
            TextView orderDeliveryTime = (TextView) rootView.findViewById(R.id.orderDeliveryTime);
            TextView orderComment = (TextView) rootView.findViewById(R.id.orderComment);
            TextView orderCommentTitle = (TextView) rootView.findViewById(R.id.orderCommentTitle);
            TextView deliveryPointComment = (TextView) rootView.findViewById(R.id.deliveryPointComment);
            TextView deliveryPointCommentTitle = (TextView) rootView.findViewById(R.id.deliveryPointCommentTitle);
            Button getRoute = (Button) rootView.findViewById(R.id.buttonGetRoute);

            orderNumber.setText(order.Title);
            orderClient.setText(order.Counterparty);
            orderAddress.setText(order.Address);
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
                deliveryPointComment.setText(order.DeliveryPointComment);
            }

            spinner = (Spinner) rootView.findViewById(R.id.orderStatusSpinner);
            TextView statusTextView = (TextView) rootView.findViewById(R.id.orderStatusTextView);
            if (order.RouteListItemStatus.equals("Опоздали") || order.RouteListItemStatus.equals("Отмена клиентом")) {
                spinner.setVisibility(View.GONE);
                statusTextView.setText(order.RouteListItemStatus);
                switch (order.RouteListItemStatus) {
                    case "Отмена клиентом": statusTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.grey)); break;
                    case "Опоздали": statusTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.red)); break;
                }
            } else {
                statusTextView.setVisibility(View.GONE);
                String[] array = getActivity().getResources().getStringArray(R.array.order_status_array);
                adapter = new OrderStatusAdapter(getActivity(), array);
                spinner.setAdapter(adapter);
                int position = adapter.getPosition(order.RouteListItemStatus);
                spinner.setSelection(position);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Object item = parent.getItemAtPosition(position);
                        if (item instanceof String) {
                            newStatus = item.toString();
                            if (order.RouteListItemStatus.equals(newStatus))
                                return;
                            ChangeOrderStatusTask task = new ChangeOrderStatusTask(getActivity());
                            task.addListener(OrderInfoFragmentActivity.this);
                            SharedPreferences sharedPref = getContext().getSharedPreferences(getString(R.string.auth_file_key), Context.MODE_PRIVATE);
                            task.execute(sharedPref.getString("Authkey", ""), order.Id, Order.ORDER_STATUS.get(newStatus));
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }

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

    @Override
    public void AsyncTaskCompleted(AsyncTaskResult<Boolean> result) {
        try {
            if (result.getException() == null && result.getResult()) {
                order.RouteListItemStatus = newStatus;
                ((TabbedOrderDetailedActivity)getActivity()).needUpdate = true;
            } else {
                int position = adapter.getPosition(order.RouteListItemStatus);
                spinner.setSelection(position);

                if (result.getException() == null && !result.getResult()) {
                    Toast toast = Toast.makeText(getContext(), "При изменении статуса произошла ошибка.", Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    Toast toast = Toast.makeText(getContext(), "Не удалось подключиться к серверу.", Toast.LENGTH_LONG);
                    toast.show();
                    throw result.getException();
                }
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG)
                e.printStackTrace();
        }
    }
}