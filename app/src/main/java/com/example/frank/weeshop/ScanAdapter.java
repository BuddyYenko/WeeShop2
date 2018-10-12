package com.example.frank.weeshop;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import com.tooltip.Tooltip;

import static com.example.frank.weeshop.ScanHome.grandTotal;


public class ScanAdapter extends RecyclerView.Adapter<ScanAdapter.ViewHolder> {

    private List<Product> listItems;
    Context context;

    Double grandTotal = 0.00;


    public ScanAdapter(List<Product> listItems) {
        this.listItems = listItems;

    }


    public ScanAdapter(List<Product> listItems, Context context) {

        this.listItems = listItems;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView name;
        public TextView price;
        public TextView quantity, qoh;
        public TextView tv_total;
        public TextView tv_grandTotal;
        CardView cardView;
        public ImageView increase, decrease;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            name = itemView.findViewById(R.id.tv_name);
            price = itemView.findViewById(R.id.tv_price);
            quantity = itemView.findViewById(R.id.tv_quantity);
            tv_total = itemView.findViewById(R.id.tv_total);
            tv_grandTotal = itemView.findViewById(R.id.grand_total);
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


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Product productList = listItems.get(position);
        holder.name.setText(productList.getName());
        holder.qoh.setText(productList.getQuantity());


        holder.quantity.setText("1");
        int quantityValue;
        final String stringPrice = Double.toString(productList.getPrice());
        holder.price.setText(stringPrice);
        String calc =  String.valueOf(Double.valueOf(stringPrice) * Double.valueOf(holder.quantity.getText().toString()));
        holder.tv_total.setText(calc);

        // = productList.tv_total + Double.valueOf(productList.quantity);
        //finalTotal = finalTotal  + (price * Double.parseDouble(quantity));

        String stringTotal = Double.toString(productList.getTotal());
       // holder.tv_total.setText(stringTotal);

        //finalTotal = finalTotal  + (price * Double.parseDouble(quantity));
       // int qty = Integer.parseInt(holder.quantity.getText().toString())+1;

       // holder.quantity.setText(String.valueOf(qty));
       // Double calc =  Double.valueOf(stringPrice);
        holder.tv_total.setText(Double.toString(productList.getPrice()));
        grandTotal = grandTotal + productList.getPrice();
       //grandTotalFinal += calc;
        Intent intent = new Intent("Total");
        intent.putExtra("grandTotalFinal",grandTotal);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);





        String stringGrandTotal = Double.toString(productList.getGrandTotal());
        stringGrandTotal += stringTotal;
//        holder.tv_grandTotal.setText(stringGrandTotal);

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
                   // holder.quantity.setText(String.valueOf(qty));
                    //Double calc =  Double.valueOf(stringPrice) * Double.valueOf(holder.quantity.getText().toString());
                    Double calc = productList.price * productList.custQuantity;

                    productList.setCustQuantity(productList.custQuantity++);
                    holder.quantity.setText(String.valueOf(productList.custQuantity));

                    holder.tv_total.setText(calc.toString());

                    //Double grandTotalFinal = 0.00;
                    grandTotal += calc;

                    Intent intent = new Intent("Total");
                    intent.putExtra("grandTotalFinal",grandTotal);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                }
                //String tt = holder.tv_grandTotal.getText().toString();

//                else if (quantityInc == quantityOnHand)
//                {
//                   // holder.increase();
//                    switch (view.getId()){
//                        case R.id.increase:
//                            showTooltip(view, Gravity.TOP);
//                            break;
//                    }
//                }
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
                    productList.setCustQuantity(productList.custQuantity--);
                    Double calc = productList.price * productList.custQuantity;
                    holder.tv_total.setText(calc.toString());

                   // Double grandTotalFinal = 0.00;
                    grandTotal = grandTotal - calc;

                    Intent intent = new Intent("Total");
                    intent.putExtra("grandTotalFinal",grandTotal);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);




                    //String finalTotal = String.valueOf(holder.tv_grandTotal.getText(Double.valueOf(grandTotalFinal)));
                }
                //String tt = holder.tv_grandTotal.getText().toString();
            }
        });

//        Double grandTotalFinal = 0.00;
//        Intent intent = new Intent("Total");
//        intent.putExtra("grandTotalFinal",grandTotalFinal);
//        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);




        //String finalTotal = String.valueOf(holder.tv_grandTotal.getText(grandTotalFinal));

//        String ItemName = tv.getText().toString();
//        String qty = quantity.getText().toString();
        //String qty = quantity.getText().toString();




//
//        String grandTotalString = stringGrandTotal.getText().toString();
//        String qty = quantity.getText().toString();
//
//        Double grandTotalFinal = gra
//        Intent intent = new Intent("custom-message");
//        //            intent.putExtra("quantity",Integer.parseInt(quantity.getText().toString()));
//        intent.putExtra("quantity",qty);
//        intent.putExtra("item",ItemName);
//        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    @Override
    public int getItemCount() {

            return listItems.size();

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

