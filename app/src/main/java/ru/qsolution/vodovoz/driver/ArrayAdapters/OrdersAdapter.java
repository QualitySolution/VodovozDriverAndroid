package ru.qsolution.vodovoz.driver.ArrayAdapters;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ru.qsolution.vodovoz.driver.DTO.ShortOrder;
import ru.qsolution.vodovoz.driver.R;

/**
 * Created by Andrei on 09.06.16.
 */
public class OrdersAdapter extends ArrayAdapter<ShortOrder> {

    private final Activity context;
    private ArrayList<ShortOrder> ordersList = new ArrayList<>();

    public OrdersAdapter(Activity context, ArrayList<ShortOrder> ordersList) {
        super(context, R.layout.route_list_item, ordersList);
        this.context = context;
        this.ordersList = ordersList;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.order_item, null, true);
        TextView orderNumber = (TextView) rowView.findViewById(R.id.orderNumber);
        TextView orderStatus = (TextView) rowView.findViewById(R.id.orderStatus);
        TextView orderDeliveryTime = (TextView) rowView.findViewById(R.id.orderDeliveryTime);
        TextView orderClient = (TextView) rowView.findViewById(R.id.orderClient);
        TextView orderAddress = (TextView) rowView.findViewById(R.id.orderAddress);


        orderNumber.setText(ordersList.get(position).Id);
        orderStatus.setText(ordersList.get(position).OrderStatus);
        orderDeliveryTime.setText(ordersList.get(position).DeliverySchedule);
        orderClient.setText(ordersList.get(position).Counterparty);
        orderAddress.setText(ordersList.get(position).Address);

        if (ordersList.get(position).OrderStatus.equals("В пути"))
            orderStatus.setTextColor(Color.parseColor("#36b032"));
        return rowView;
    }
}