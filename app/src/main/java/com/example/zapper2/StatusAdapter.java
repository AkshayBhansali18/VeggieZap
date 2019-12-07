package com.example.zapper2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class StatusAdapter extends BaseAdapter {
    ArrayList<String> orders;
    ArrayList<String> item;
    ArrayList<Integer> images;
    ArrayList<String> datetime;
    ArrayList<String> amounts;
    ArrayList<String> status;
    Context context;
    FirebaseFirestore db= FirebaseFirestore.getInstance();
    public StatusAdapter(Context context, ArrayList<String> orders, ArrayList<String> item, ArrayList<Integer> images, ArrayList<String> datetime, ArrayList<String> amounts, ArrayList<String> status) {
        this.orders=orders;
        this.datetime=datetime;
        this.context=context;
        this.amounts=amounts;
        this.images=images;
        this.item=item;
        this.status=status;
    }

    @Override
    public int getCount() {
        return orders.size();
    }

    @Override
    public Object getItem(int position) {
        return orders.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        convertView = LayoutInflater.from(context).inflate(R.layout.orders_list_layout, parent, false);
        CircleImageView dp=convertView.findViewById(R.id.circleImageView);
        TextView date_textView = (TextView)convertView.findViewById(R.id.datetime);
        TextView orderno=convertView.findViewById(R.id.orderno);
        TextView items=convertView.findViewById(R.id.items);
        TextView amount=convertView.findViewById(R.id.amount);
        date_textView.setText(datetime.get(position));
        orderno.setText("Order Number "+orders.get(position));
        items.setText("Number of items "+item.get(position));
        amount.setText("Amount ₹"+amounts.get(position));
        dp.setImageResource(images.get(position));

        return convertView;
    }

}
