package ru.qsolution.vodovoz.driver.ArrayAdapters;

import android.app.Activity;
import android.graphics.Color;
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

    class RouteListHolder {
        TextView routeListNumber;
        TextView routeListDate;
        TextView routeListDeliveryShift;
        TextView routeListForwarder;

        public RouteListHolder (View view) {
            routeListNumber = (TextView) view.findViewById(R.id.routeListNumber);
            routeListDate = (TextView) view.findViewById(R.id.routeListDate);
            routeListDeliveryShift = (TextView) view.findViewById(R.id.routeListDeliveryShift);
            routeListForwarder = (TextView) view.findViewById(R.id.routeListForwarder);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View view = convertView;
        RouteListHolder rl;

        if (convertView == null) {
            view = inflater.inflate(R.layout.route_list_item, null);
            rl = new RouteListHolder(view);
            view.setTag(rl);
        } else {
            rl = (RouteListHolder)view.getTag();
        }

        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");

        rl.routeListNumber.setText(routeLists.get(position).Id);
        rl.routeListDate.setText(df.format(routeLists.get(position).Date));
        rl.routeListDeliveryShift.setText(routeLists.get(position).DeliveryShift);
        if (routeLists.get(position).Forwarder.equals("anyType{}"))
            rl.routeListForwarder.setText("Без экспедитора");
        else
            rl.routeListForwarder.setText(routeLists.get(position).Forwarder);

        if (routeLists.get(position).Id.equals(LocationService.RouteListId)) {
            view.setBackgroundColor(Color.parseColor("#BAE8BA"));
        }

        return view;
    }
}