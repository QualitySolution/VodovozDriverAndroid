package ru.qsolution.vodovoz.driver.ArrayAdapters;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ru.qsolution.vodovoz.driver.DTO.ShortOrder;
import ru.qsolution.vodovoz.driver.R;

/**
 * Created by Andrei Vinogradov on 09.06.16.
 * (c) Quality Solution Ltd.
 */

public class OrdersAdapter extends ArrayAdapter<ShortOrder> {

    private final Activity context;
    private ArrayList<ShortOrder> ordersList = new ArrayList<>();

    public OrdersAdapter(Activity context, ArrayList<ShortOrder> ordersList) {
        super(context, R.layout.route_list_item, ordersList);
        this.context = context;
        this.ordersList = ordersList;
    }

    class OrderViewHolder {
        TextView OrderNumber;
        TextView OrderStatus;
        TextView OrderDeliveryTime;
        TextView OrderClient;
        TextView OrderAddress;

        public OrderViewHolder(View view) {
            OrderNumber = (TextView) view.findViewById(R.id.orderNumber);
            OrderStatus = (TextView) view.findViewById(R.id.orderStatus);
            OrderDeliveryTime = (TextView) view.findViewById(R.id.orderDeliveryTime);
            OrderClient = (TextView) view.findViewById(R.id.orderClient);
            OrderAddress = (TextView) view.findViewById(R.id.orderAddress);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View view = convertView;
        OrderViewHolder vh;
        if (convertView == null) {
            view = inflater.inflate(R.layout.order_item, null);
            vh = new OrderViewHolder(view);
            view.setTag(vh);
        } else {
            vh = (OrderViewHolder) view.getTag();
        }

        vh.OrderNumber.setText(ordersList.get(position).Id);
        vh.OrderStatus.setText(ordersList.get(position).OrderStatus);
        vh.OrderDeliveryTime.setText(ordersList.get(position).DeliverySchedule);
        vh.OrderClient.setText(ordersList.get(position).Counterparty);
        vh.OrderAddress.setText(ordersList.get(position).Address);

        switch (ordersList.get(position).OrderStatus) {
            case "Выполнен":
                vh.OrderStatus.setTextColor(ContextCompat.getColor(context, R.color.green));
                break;
            case "Отмена клиентом":
                vh.OrderStatus.setTextColor(ContextCompat.getColor(context, R.color.grey));
                break;
            case "Опоздали":
                vh.OrderStatus.setTextColor(ContextCompat.getColor(context, R.color.red));
                break;
            default:
                vh.OrderStatus.setTextColor(Color.BLACK);
                break;
        }

        return view;
    }
}