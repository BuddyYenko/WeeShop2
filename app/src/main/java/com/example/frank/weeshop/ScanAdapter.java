package com.example.frank.weeshop;

import android.app.LauncherActivity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Shubham on 29-06-2017.
 */

public class ScanAdapter extends RecyclerView.Adapter<ScanAdapter.ViewHolder> {
    List<Product> listItems;
    Context context;
    public String product_id;


    public ScanAdapter(List<Product> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Product productList = listItems.get(position);
        product_id = productList.getProduct_id();
        holder.name.setText(productList.getName());
        String stringprice= Double.toString(productList.getPrice());

        holder.price.setText(stringprice);
        Linkify.addLinks(holder.name, Linkify.ALL);
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView name;
        public TextView price;

//        //public TextView getTextView_Id() {
//            return textView_Id;
//        }

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView)itemView.findViewById(R.id.tv_name);
            price = (TextView)itemView.findViewById(R.id.tv_price);
        }
    }
}

