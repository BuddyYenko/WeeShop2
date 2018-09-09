package com.example.frank.weeshop;

import android.app.Activity;
import android.app.LauncherActivity;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Filter;
import android.widget.Filterable;
import java.util.ArrayList;
import java.util.List;
import android.util.Log;

import static com.example.frank.weeshop.ScanHome.tv_total;


public class ScanAdapter extends RecyclerView.Adapter<ScanAdapter.ViewHolder> implements Filterable{
    static List<Product> listItems;
    Context context;
    Activity activity;
    public String product_id;
    public static List<Product> selecteditems;
    int lastPosition=0;
    final List<Product> templist=new ArrayList<>();
    private List<Product> callListResponses = new ArrayList<>();






    public ScanAdapter(List<Product> callListResponses, Context context) {
        super();
        this.callListResponses = callListResponses;
        this.context = context;
        selecteditems= new ArrayList<>();
        templist.addAll(callListResponses);
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
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Product productList = callListResponses.get(position);
        product_id = productList.getProduct_id();
        holder.name.setText(productList.getName());
        holder.quantity.setText(productList.getQuantity());
       // 1 holder.quantity.setText(call.get);
        String stringprice= Double.toString(productList.getPrice());

        holder.price.setText(stringprice);

    }

    @Override
    public int getItemCount() {
        if (callListResponses != null){
            return callListResponses.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView name;
        public TextView price;
        public TextView quantity;
        CardView cardView;
        CheckBox checkBox;

//        //public TextView getTextView_Id() {
//            return textView_Id;
//        }

        public ViewHolder(View itemView) {
            super(itemView);
            cardView= (CardView)itemView.findViewById(R.id.cardView);
            name = (TextView)itemView.findViewById(R.id.tv_name);
            price = (TextView)itemView.findViewById(R.id.tv_price);
            quantity = (TextView)itemView.findViewById(R.id.tv_quantity);
            tv_total = (TextView)itemView.findViewById(R.id.tv_total);



        }
    }
    public Filter getFilter() {

        Filter mfilter = new Filter() {
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                callListResponses.clear();
                callListResponses.addAll((ArrayList<Product>)results.values);
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults result = new FilterResults();
                ArrayList<Product> res;
                Log.e("Charswq",String.valueOf(constraint));
                Log.e("Lenght",String.valueOf(constraint.length()));

                res = new ArrayList<>();
                if(constraint.length()>0){
                    for(Product d: callListResponses){

                        if(d.getName().toUpperCase().contains(constraint.toString().toUpperCase())){

                            res.add(d);
                            Log.e("Name",d.getName());

                        }
                    }
                    result.count = res.size();
                    result.values = res;
                }

                if(constraint.length() == 0){
                    res.addAll(templist);
                    result.count = res.size();
                    result.values = res;
                    Log.e("value of templeis",""+templist.size());
                }

                return result;
            }
        };

        return mfilter;
    }

    private int grandTotal(List<Product> items){

        int totalPrice = 0;
        for(int i = 0 ; i < items.size(); i++) {
            totalPrice += items.get(i).getPrice();
        }

        return totalPrice;
    }
}

