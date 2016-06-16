package ru.qsolution.vodovoz.driver.ArrayAdapters;

/**
 * Created by Andrei Vinogradov on 16.06.16.
 * (c) Quality Solution Ltd.
 */

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ru.qsolution.vodovoz.driver.R;

/**
 * Created by Andrei on 09.06.16.
 */
public class OrderStatusAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private String[] statusArray;

    public OrderStatusAdapter(Activity context, String[] statusArray) {
        super(context, R.layout.spinner_item, statusArray);
        this.context = context;
        this.statusArray = statusArray;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.spinner_item, null, true);
        prepareTextView(position, rowView);
        return rowView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent)
    {
        View view = super.getView(position, convertView, parent);
        prepareTextView(position, view);
        return view;
    }

    private void prepareTextView(int position, View view ) {
        TextView textView = (TextView) view.findViewById(R.id.spinner_item_text);
        textView.setText(statusArray[position]);
        switch (statusArray[position]) {
            case "Выполнен": textView.setTextColor(context.getResources().getColor(R.color.green)); break;
            case "Отмена клиентом": textView.setTextColor(context.getResources().getColor(R.color.grey)); break;
            case "Опоздали": textView.setTextColor(context.getResources().getColor(R.color.red)); break;
            default: textView.setTextColor(Color.BLACK); break;
        }
    }
}