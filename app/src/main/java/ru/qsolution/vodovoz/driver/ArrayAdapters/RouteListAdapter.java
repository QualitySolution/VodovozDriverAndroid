package ru.qsolution.vodovoz.driver.ArrayAdapters;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.view.menu.ActionMenuItemView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import ru.qsolution.vodovoz.driver.DTO.RouteList;
import ru.qsolution.vodovoz.driver.R;
import ru.qsolution.vodovoz.driver.Services.LocationService;

/**
 * Created by Andrei on 09.06.16.
 */
public class RouteListAdapter extends ArrayAdapter<RouteList> {

    private final Activity context;
    private ArrayList<RouteList> routeLists = new ArrayList<>();

    public RouteListAdapter(Activity context,
                            ArrayList<RouteList> routeLists) {
        super(context, R.layout.route_list_item, routeLists);
        this.context = context;
        this.routeLists = routeLists;

    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.route_list_item, null, true);
        TextView routeListNumber = (TextView) rowView.findViewById(R.id.routeListNumber);
        TextView routeListDate = (TextView) rowView.findViewById(R.id.routeListDate);
        TextView routeListDeliveryShift = (TextView) rowView.findViewById(R.id.routeListDeliveryShift);
        TextView routeListForwarder = (TextView) rowView.findViewById(R.id.routeListForwarder);

        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");

        routeListNumber.setText(routeLists.get(position).Id);
        routeListDate.setText(df.format(routeLists.get(position).Date));
        routeListDeliveryShift.setText(routeLists.get(position).DeliveryShift);
        routeListForwarder.setText(routeLists.get(position).Forwarder);


        if (routeLists.get(position).Id.equals(LocationService.RouteListId)) {
            rowView.setBackgroundColor(Color.parseColor("#BAE8BA"));
        }

        return rowView;
    }
}