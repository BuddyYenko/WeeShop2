package com.example.frank.weeshop;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.JsonObject;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.frank.weeshop.ScanAdapter.listItems;

public class ScanHome extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    IntentIntegrator scan;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    ImageView btn_scan;
    String url = "http://sict-iis.nmmu.ac.za/weeshop/app/fetch.php";
    String product_id;
    public static double total=0;
    public static TextView tv_total;
    public static TextView grandTotal;
    public double finalTotal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_home);

//        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean beep = sharedPref.getBoolean("beep",true);
        Boolean frontCamera = sharedPref.getBoolean("frontCamera",false);


        //********************************************

        //********************************************


        int camId;
        if(frontCamera == false)
            camId = 0;
        else
            camId = 1;
        sharedPref.registerOnSharedPreferenceChangeListener(this);

        recyclerView =findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listItems = new ArrayList<>();
        adapter = new ScanAdapter(listItems, this);//Changes
        recyclerView.setAdapter(adapter);
        CardView cardView = findViewById(R.id.cardView);
        tv_total = findViewById(R.id.tv_total);
        grandTotal = findViewById(R.id.grand_total);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                Product product = listItems.get(position);

                // Another line of code....
                listItems.remove(position);
                adapter.notifyItemRemoved(position);
                adapter.notifyItemRangeChanged(position, listItems.size());

            }
        }).attachToRecyclerView(recyclerView);

        scan = new IntentIntegrator(this);
        scan.setBeepEnabled(beep);
        scan.setCameraId(camId);

        btn_scan = findViewById(R.id.btn_scan);
        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scan.initiateScan();
            }
        });



//        calculateTotal(listItems);

    }





    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {

            if (result.getContents() == null) {
                Snackbar snackbar = Snackbar.make(findViewById(R.id.coordinate), "Result Not Found", Snackbar.LENGTH_LONG);

                snackbar.show();
            } else {
                try
                {
                    JSONObject obj = new JSONObject(result.getContents());
                    //setting values to textviews
                    product_id = obj.getString("product_id");

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONArray jsonArray = new JSONArray(response);
                                        JSONObject jsonObject = jsonArray.getJSONObject(0);

                                        for (int i = 0; i < listItems.size(); i++)
                                        {
                                            JSONObject productJObject = new JSONObject();

                                            productJObject.put("name", listItems.get(i).name);
                                            productJObject.put("price", listItems.get(i).price);
                                            productJObject.put("quantity", listItems.get(i).quantity);
                                            productJObject.put("total", listItems.get(i).tv_total);
                                            jsonArray.put(productJObject);

                                        }
                                        jsonObject.put("product_id", jsonArray);
                                        jsonObject.put("total", total);

                                        String name = jsonObject.getString("name");
                                        //String id = jsonObject.getString("product_id");
                                        Double price = jsonObject.getDouble("price");
                                        String quantity = jsonObject.getString("quantity");
                                        Double total = jsonObject.getDouble("total");
//                                        Double grandTotal = jsonObject.getDouble("grandTotal");

                                        //Product productList = new Product(name, price, quantity, total, grandTotal);

                                        Product productList = new Product(name, price, quantity, total);
                                        listItems.add(productList);
                                        adapter = new ScanAdapter(listItems,ScanHome.this);
                                        recyclerView.setAdapter(adapter);
//                                        SharedPreferences preferences = getSharedPreferences("MYPREFS", MODE_PRIVATE);
//                                        if(preferences.getString("finalTotal", "") != null){
//                                            finalTotal = Double.parseDouble(preferences.getString("finalTotal", "0"));
//
//                                        }

                                        finalTotal = finalTotal  + (price * Double.parseDouble(quantity));

                                        grandTotal.setText(String.valueOf(finalTotal));

//                                        productList.setGrandTotal(total);
//                                        grandTotal = findViewById(R.id.grand_total);
//                                        Toast.makeText("Total : " + calculateTotal(), Toast.LENGTH_LONG).show();


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();

                            params.put("product_id", product_id);

                            return params;
                        }
                    };
                    MySingleton.getInstance(ScanHome.this).addToRequestque(stringRequest);


                }
                catch (JSONException e) {
                    e.printStackTrace();
                    //if control comes here
                    //that means the encoded format not matches
                    //in this case you can display whatever data is available on the qrcode
                    //to a toast
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                }
              }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

//    public double calculateTotal(){
//        int total = 0;
//        for(Product product: listItems){
//            total+=  product.getGrandTotal();
//        }
//        return total;
//    }


//    private int calculateTotal(List<Product> items){
//
//        int grandTotal = 0;
//        for(int i = 0 ; i < items.size(); i++) {
//            grandTotal += items.get(i).getTotal();
//        }
//
//        return grandTotal;
//    }


    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("beep")){
            scan.setBeepEnabled(sharedPreferences.getBoolean(key, true));
        }
        if (key.equals("frontCamera")){
            int camId;
            if (sharedPreferences.getBoolean(key, false)== false)
                camId = 0;
            else
                camId = 1;
            scan.setCameraId(camId);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }
}
