package com.example.frank.weeshop;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ReturnAdapter extends RecyclerView.Adapter<ReturnAdapter.MyViewHolder> {
    private List<Product> list;
    private Context mCtx;
    private ReturnActivity activity;
    public ReturnAdapter(List<Product> list) {
        this.list = list;
    }

    public ReturnAdapter(Context mCtx, List<Product> list, ReturnActivity activity) {
        this.mCtx = mCtx;
        this.list = list;
        this.activity = activity;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, price, qty, return_qty, total;
        public ImageView increase, decrease;

        public MyViewHolder(View view) {
            super(view);

            total = view.findViewById(R.id.tv_total);
            name = view.findViewById(R.id.tv_name);
            price = view.findViewById(R.id.tv_price);
            qty = view.findViewById(R.id.tv_qty);
            return_qty =  view.findViewById(R.id.return_qty);
            increase = view.findViewById(R.id.increase);
            decrease = view.findViewById(R.id.decrease);

        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_return, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Product product = list.get(position);
        holder.name.setText(product.getName());
        holder.price.setText(String.valueOf(product.getPrice()));
        holder.qty.setText(product.getQuantity());
        holder.return_qty.setText("0");
        holder.increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String stringQty = holder.return_qty.getText().toString();
                Integer currentQty = Integer.parseInt(stringQty);
                int dbQty = Integer.valueOf(product.getQuantity());
                if(currentQty < dbQty)
                {
                    int qty = currentQty + 1;
                    holder.return_qty.setText(String.valueOf(qty));

                    double total = Double.valueOf(product.getPrice()) * (currentQty + 1);
                    holder.total.setText(String.valueOf(total));
                    String product_id = product.getProduct_id();
                    String return_qty = holder.return_qty.getText().toString();

                    Intent intent = new Intent("custom-message");
                    Double price = product.getPrice();
                    intent.putExtra("total",String.valueOf(price));
                    intent.putExtra("operation","increase");
                    intent.putExtra("product_id",product_id);
                    intent.putExtra("return_qty", return_qty);
                    LocalBroadcastManager.getInstance(mCtx).sendBroadcast(intent);

                }
            }
        });
//
        holder.decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String stringQty = holder.return_qty.getText().toString();
                Integer currentQty = Integer.parseInt(stringQty);
                if(currentQty != 0)
                {
                    int qty = currentQty - 1;
                    double total = Double.valueOf(product.getPrice()) * (currentQty - 1);
                    holder.return_qty.setText(String.valueOf(qty));
                    holder.total.setText(String.valueOf(total));
                    String product_id = product.getProduct_id();
                    String return_qty = holder.return_qty.getText().toString();

                    Intent intent = new Intent("custom-message");
                    Double price = product.getPrice();
                    intent.putExtra("total",String.valueOf(price));
                    intent.putExtra("operation","decrease");
                    intent.putExtra("product_id",product_id);
                    intent.putExtra("return_qty", return_qty);

                    LocalBroadcastManager.getInstance(mCtx).sendBroadcast(intent);
                }
            }
        });
        holder.total.setText("0.00");

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
