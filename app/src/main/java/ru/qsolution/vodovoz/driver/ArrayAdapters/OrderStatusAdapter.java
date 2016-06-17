package ru.qsolution.vodovoz.driver.ArrayAdapters;

/**
 * Created by Andrei Vinogradov on 16.06.16.
 * (c) Quality Solution Ltd.
 */

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import ru.qsolution.vodovoz.driver.R;

/**
 * Created by Andrei on 09.06.16.
 */
public class OrderStatusAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] statusArray;

    public OrderStatusAdapter(Activity context, String[] statusArray) {
        super(context, R.layout.spinner_item, statusArray);
        this.context = context;
        this.statusArray = statusArray;
    }

    class OrderStatusHolder {
        TextView status;

        public OrderStatusHolder (View view) {
            status = (TextView) view.findViewById(R.id.spinner_item_text);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return prepareView(position, convertView);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent)
    {
        return prepareView(position, convertView);
    }

    private View prepareView(int position, View convertView) {
        LayoutInflater inflater = context.getLayoutInflater();
        View view = convertView;
        OrderStatusHolder sh;

        if (convertView == null) {
            view = inflater.inflate(R.layout.spinner_item, null);
            sh = new OrderStatusHolder(view);
            view.setTag(sh);
        } else {
            sh = (OrderStatusHolder)view.getTag();
        }
        sh.status.setText(statusArray[position]);
        switch (statusArray[position]) {
            case "Выполнен": sh.status.setTextColor(ContextCompat.getColor(context, R.color.green)); break;
            default: sh.status.setTextColor(Color.BLACK); break;
        }
        return view;
    }
}