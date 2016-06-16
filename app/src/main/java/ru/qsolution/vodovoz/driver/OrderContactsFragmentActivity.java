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
import android.widget.TextView;

import ru.qsolution.vodovoz.driver.DTO.Order;

public class OrderContactsFragmentActivity extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String SERIALIZED_ORDER = "serialized_order";

    private Order order;

    public static OrderContactsFragmentActivity newInstance(int sectionNumber, Order order) {
        OrderContactsFragmentActivity fragment = new OrderContactsFragmentActivity();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putSerializable(SERIALIZED_ORDER, order);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_order_contacts_fragment, container, false);
        order = (Order) getArguments().getSerializable(SERIALIZED_ORDER);

        if (order != null) {
            TextView orderContactPerson = (TextView) rootView.findViewById(R.id.orderContactPerson);
            TextView orderContactPhone = (TextView) rootView.findViewById(R.id.orderContactPhone);
            ListView contactsListView = (ListView) rootView.findViewById(R.id.contactsListView);

            orderContactPerson.setText(order.Contact);

            //Setting up client phones
            if (order.Phones.size() > 0) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(rootView.getContext(), android.R.layout.simple_list_item_1, order.Phones.toArray(new String[order.Phones.size()]));
                contactsListView.setAdapter(adapter);
                contactsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                        String phoneNumber = (String) parent.getItemAtPosition(position);
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(Order.GetPhoneNumberUri(phoneNumber)));
                        startActivity(intent);
                    }
                });
            } else {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(rootView.getContext(), android.R.layout.simple_list_item_1, new String[]{"Номера телефонов не указаны"});
                contactsListView.setAdapter(adapter);
            }

            //Setting up contact person phone
            if (Order.GetPhoneNumberUri(order.Phone) != null) {
                orderContactPhone.setText(order.Phone);
                orderContactPhone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(Order.GetPhoneNumberUri(order.Phone)));
                        startActivity(intent);
                    }
                });
            } else {
                orderContactPhone.setText("Телефон не указан");
            }
        } else {
            getActivity().finish();
        }

        return rootView;
    }
}