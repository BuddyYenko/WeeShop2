package com.example.frank.weeshop;

import android.app.Activity;
import android.app.LauncherActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Filter;
import android.widget.Filterable;
import java.util.ArrayList;
import java.util.List;
import android.util.Log;

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
        public TextView quantity;
        public TextView tv_total;
        public TextView grandTotal;
        CardView cardView;
        public Button increase, decrease;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            name = itemView.findViewById(R.id.tv_name);
            price = itemView.findViewById(R.id.tv_price);
            quantity = itemView.findViewById(R.id.tv_quantity);
            tv_total = itemView.findViewById(R.id.tv_total);
            grandTotal = itemView.findViewById(R.id.grand_total);



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
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Product productList = callListResponses.get(position);
        product_id = productList.getProduct_id();
        holder.name.setText(productList.getName());
        holder.quantity.setText(productList.getQuantity());
        //holder.quantity.setText("" + productList.getQuantity(position).quantity);

        String stringPrice= Double.toString(productList.getPrice());
        holder.price.setText(stringPrice);
        productList.tv_total = Double.valueOf(stringPrice) * Double.valueOf(productList.quantity);

        String stringTotal = Double.toString(productList.getTotal());
        holder.tv_total.setText(stringTotal);
    }

    @Override
    public int getItemCount() {
        if (callListResponses != null){
            return callListResponses.size();
        }
        return 0;
    }
}

