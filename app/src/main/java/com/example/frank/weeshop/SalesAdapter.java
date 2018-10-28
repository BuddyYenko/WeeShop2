package com.example.frank.weeshop;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tooltip.Tooltip;

import java.util.List;

public class SalesAdapter extends RecyclerView.Adapter<SalesAdapter.MyViewHolder> {
    private List<Product> list;
    private Context mCtx;
    private SalesActivity activity;
    public SalesAdapter(List<Product> list) {
        this.list = list;
    }

    public SalesAdapter(Context mCtx, List<Product> list, SalesActivity activity) {
        this.mCtx = mCtx;
        this.list = list;
        this.activity = activity;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, price, qty, total, qoh;
        public ImageView increase, decrease;

        public MyViewHolder(View view) {
            super(view);

            total = view.findViewById(R.id.tv_total);
            name = view.findViewById(R.id.tv_name);
            price = view.findViewById(R.id.tv_price);
            qty = view.findViewById(R.id.tv_quantity);
            qoh = view.findViewById(R.id.tv_qoh);
            increase = view.findViewById(R.id.increase);
            decrease = view.findViewById(R.id.decrease);

        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Product product = list.get(position);
        holder.name.setText(product.getName());
       // holder.price.setText(String.valueOf(product.getPrice()));
        String dbprice = String.format("%.2f", product.getPrice());
        holder.price.setText(dbprice);
        holder.qty.setText("1");
        holder.qoh.setText(product.getQuantity());

//        //update lista, b and grandtotal
//        String product_id = product.getProduct_id();
//        String sale_qty = "1";
//        Intent intent = new Intent("custom-message");
//        Double price = product.getPrice();
//        intent.putExtra("total",String.valueOf(price));
//        intent.putExtra("operation","increase");
//        intent.putExtra("product_id",product_id);
//        intent.putExtra("sale_qty", sale_qty);
//        LocalBroadcastManager.getInstance(mCtx).sendBroadcast(intent);

        holder.increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String stringQty = holder.qty.getText().toString();
                Integer currentQty = Integer.parseInt(stringQty);
                int dbQty = Integer.valueOf(product.getQuantity());
                String qohInc = holder.qoh.getText().toString();
                int quantityOnHand = Integer.parseInt(qohInc);
                if(currentQty < quantityOnHand)
                {
                    int qty = currentQty + 1;
                    holder.qty.setText(String.valueOf(qty));

                    double total = Double.valueOf(product.getPrice()) * (currentQty + 1);
                    holder.total.setText(String.format("%.2f", total));
                    String product_id = product.getProduct_id();
                    String sale_qty = holder.qty.getText().toString();

                    Intent intent = new Intent("custom-message");
                    Double price = product.getPrice();
                    intent.putExtra("total",String.valueOf(price));
                    intent.putExtra("operation","increase");
                    intent.putExtra("product_id",product_id);
                    intent.putExtra("sale_qty", sale_qty);
                    LocalBroadcastManager.getInstance(mCtx).sendBroadcast(intent);

                }
                else if(currentQty == quantityOnHand)
                {
                    //holder.increase();
                    switch (view.getId()){
                        case R.id.increase:
                            showTooltip(view, Gravity.TOP);
                            break;
                    }
                }
            }
        });
//
        holder.decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String stringQty = holder.qty.getText().toString();
                Integer currentQty = Integer.parseInt(stringQty);
                if(currentQty > 1)
                {
                    int qty = currentQty - 1;
                    double total = Double.valueOf(product.getPrice()) * (currentQty - 1);
                    holder.qty.setText(String.valueOf(qty));
                    holder.total.setText(String.format("%.2f", total));
                    String product_id = product.getProduct_id();
                    String sale_qty = holder.qty.getText().toString();

                    Intent intent = new Intent("custom-message");
                    Double price = product.getPrice();
                    intent.putExtra("total",String.valueOf(price));
                    intent.putExtra("operation","decrease");
                    intent.putExtra("product_id",product_id);
                    intent.putExtra("sale_qty", sale_qty);

                    LocalBroadcastManager.getInstance(mCtx).sendBroadcast(intent);
                }
            }
        });
        String dbt = String.format("%.2f", product.getPrice());
        holder.total.setText(dbt);


    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    private void showTooltip(View v, int gravity) {
        ImageView imgV =(ImageView) v;
        Tooltip tooltip = new Tooltip.Builder(imgV)
                .setText("Maximum quantity on hand has been reached")
                .setTextColor(Color.BLACK)
                .setGravity(gravity)
                .setCornerRadius(8f)
                .setBackgroundColor(Color.LTGRAY)
                .setDismissOnClick(true)
                .show();
    }
}
