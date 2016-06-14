package ru.qsolution.vodovoz.driver;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import ru.qsolution.vodovoz.driver.DTO.Order;

public class OrderItemsFragmentActivity extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String SERIALIZED_ORDER = "serialized_order";

    private ListView orderItemsListView;
    private ListView orderEquipmentListView;

    private Order order;

    public OrderItemsFragmentActivity() {
    }

    public static OrderItemsFragmentActivity newInstance(int sectionNumber, Order order) {
        OrderItemsFragmentActivity fragment = new OrderItemsFragmentActivity();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putSerializable(SERIALIZED_ORDER, order);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_order_items_fragment, container, false);

        orderItemsListView = (ListView) rootView.findViewById(R.id.orderItemsListView);
        orderEquipmentListView = (ListView) rootView.findViewById(R.id.orderEquipmentListView);

        order = (Order) getArguments().getSerializable(SERIALIZED_ORDER);

        if (order != null) {
            ArrayAdapter<String> itemsAdapter;
            if (order.OrderItems.size() > 0) {
                itemsAdapter = new ArrayAdapter<>(rootView.getContext(), android.R.layout.simple_list_item_1, order.OrderItems.toArray(new String[order.OrderItems.size()]));
            } else {
                itemsAdapter = new ArrayAdapter<>(rootView.getContext(), android.R.layout.simple_list_item_1, new String[] {"Товары отсутствуют"});
            }
            orderItemsListView.setAdapter(itemsAdapter);

            ArrayAdapter<String> equipmentAdapter;
            if (order.OrderEquipment.size() > 0) {
                equipmentAdapter = new ArrayAdapter<>(rootView.getContext(), android.R.layout.simple_list_item_1, order.OrderEquipment.toArray(new String[order.OrderEquipment.size()]));
            } else {
                equipmentAdapter = new ArrayAdapter<>(rootView.getContext(), android.R.layout.simple_list_item_1, new String[] {"Оборудование отсутствует"});
            }
            orderEquipmentListView.setAdapter(equipmentAdapter);
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