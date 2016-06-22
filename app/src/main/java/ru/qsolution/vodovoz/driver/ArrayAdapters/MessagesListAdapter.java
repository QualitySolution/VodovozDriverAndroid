package ru.qsolution.vodovoz.driver.ArrayAdapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import ru.qsolution.vodovoz.driver.DTO.Message;
import ru.qsolution.vodovoz.driver.R;

/**
 * Created by Andrei Vinogradov on 22.06.16.
 * (c) Quality Solution Ltd.
 */
public class MessagesListAdapter extends BaseAdapter {

    private Context context;
    private List<Message> messagesItems;

    public MessagesListAdapter(Context context, List<Message> navDrawerItems) {
        this.context = context;
        this.messagesItems = navDrawerItems;
    }

    @Override
    public int getCount() {
        return messagesItems.size();
    }

    @Override
    public Object getItem(int position) {
        return messagesItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Message m = messagesItems.get(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        // Identifying the message owner
        if (messagesItems.get(position).IsSelf) {
            // message belongs to you, so load the right aligned layout
            convertView = mInflater.inflate(R.layout.list_item_message_right,
                    null);
        } else {
            // message belongs to other person, load the left aligned layout
            convertView = mInflater.inflate(R.layout.list_item_message_left,
                    null);
        }

        TextView lblFrom = (TextView) convertView.findViewById(R.id.lblMsgFrom);
        TextView txtMsg = (TextView) convertView.findViewById(R.id.txtMsg);

        txtMsg.setText(m.Message);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        if (sdf.format(m.DateTime).equals(sdf.format(new Date())))
            lblFrom.setText(String.format("%s %s", m.Sender.equals("") ? "Я" : m.Sender, new SimpleDateFormat("HH:mm").format(m.DateTime)));
        else
            lblFrom.setText(String.format("%s %s", m.Sender.equals("") ? "Я" : m.Sender, new SimpleDateFormat("dd.MM.yy HH:mm").format(m.DateTime)));
        return convertView;
    }
}
