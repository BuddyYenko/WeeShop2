package com.example.frank.weeshop;

import android.app.Activity;
import android.app.LauncherActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Filter;
import android.widget.Filterable;
import java.util.ArrayList;
import java.util.List;
import android.util.Log;

import com.tooltip.Tooltip;

import static android.content.Context.MODE_PRIVATE;
import static com.example.frank.weeshop.ScanHome.tv_total;


public class ScanAdapter extends RecyclerView.Adapter<ScanAdapter.ViewHolder> {
    static List<Product> listItems;
    Context context;
    Activity activity;
    public String product_id;
    public static List<Product> selecteditems;
    final List<Product> templist=new ArrayList<>();
    private List<Product> callListResponses = new ArrayList<>();

    int count=0;




    public ScanAdapter(List<Product> callListResponses, Context context) {
        super();
        this.callListResponses = callListResponses;
        this.context = context;
        selecteditems= new ArrayList<>();
        templist.addAll(callListResponses);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView name;
        public TextView price;
        public TextView quantity, qoh;
        public TextView tv_total;
        public TextView grandTotal;
        CardView cardView;
        public ImageView increase, decrease;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            name = itemView.findViewById(R.id.tv_name);
            price = itemView.findViewById(R.id.tv_price);
            quantity = itemView.findViewById(R.id.tv_quantity);
            tv_total = itemView.findViewById(R.id.tv_total);
            grandTotal = itemView.findViewById(R.id.grand_total);
            qoh = itemView.findViewById(R.id.tv_qoh);
            increase = itemView.findViewById(R.id.increase);
            decrease = itemView.findViewById(R.id.decrease);



        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(v);
    }
    public void init(){
        templist.addAll(callListResponses);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Product productList = callListResponses.get(position);
        product_id = productList.getProduct_id();
        holder.name.setText(productList.getName());
        holder.qoh.setText(productList.getQuantity());
        holder.quantity.setText("1");

        String stringPrice= Double.toString(productList.getPrice());
        holder.price.setText(stringPrice);
        productList.tv_total = Double.valueOf(stringPrice) * Double.valueOf(productList.quantity);

        String stringTotal = Double.toString(productList.getTotal());
        holder.tv_total.setText(stringTotal);

        holder.increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String qtyInc = holder.quantity.getText().toString();
                int quantityInc = Integer.parseInt(qtyInc);
                String qohInc = holder.qoh.getText().toString();
                int quantityOnHand = Integer.parseInt(qohInc);
                if (quantityInc < quantityOnHand)
                {
                    int qty = Integer.parseInt(holder.quantity.getText().toString())+1;
                    holder.quantity.setText(String.valueOf(qty));
                }
                else if (quantityInc == quantityOnHand)
                {
                   // holder.increase();
                    switch (view.getId()){
                        case R.id.increase:
                            showTooltip(view, Gravity.TOP);
                            break;
                    }
                }
            }
        });
        holder.decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String qtyDec = holder.quantity.getText().toString();
                if (Integer.parseInt(qtyDec) > 1)
                {
                    int qty = Integer.parseInt(holder.quantity.getText().toString())-1;
                    holder.quantity.setText(String.valueOf(qty));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (callListResponses != null){
            return callListResponses.size();
        }
        return 0;
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

