package com.example.frank.weeshop;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

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

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScanHome extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    IntentIntegrator scan;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    ImageView btn_scan;
    String url = "http://sict-iis.nmmu.ac.za/weeshop/app/fetch.php";
    String product_id;
    public static double total = 0;
    public static TextView tv_total;
    public static TextView grandTotal;
    public double finalTotal;
    public List<Product> listItems;
    //    private BreakIterator txtCount;
    private android.support.v7.widget.Toolbar toolbar;


    public PopupWindow popupWindow;
    TextView txt_grand_total, txt_difference, txt_cash_paid;
    EditText edt_cash_paid;
    Button btn_calculate, btn_save;
    Button placeOrder;

    public final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


            Double grandTotalFinal = 0.00;
            grandTotalFinal = intent.getDoubleExtra("grandTotalFinal", grandTotalFinal);


            Double tsum = 0.00;
            for (int i = 0; i < listItems.size(); i++) {
                tsum = tsum + grandTotalFinal;
            }
            Log.d("total pay : ", String.valueOf(tsum));
            Toast.makeText(ScanHome.this, grandTotalFinal.toString(), Toast.LENGTH_SHORT).show();

            grandTotal.setText(grandTotalFinal.toString());


        }
    };

    private void updateValue() {
        String str1 = edt_cash_paid.getText().toString();
        TextView txt_cash_paid = findViewById(R.id.pop_cash_paid_tv);

        //String result = edit.getText().toString();
        txt_cash_paid.setText(str1);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_home);

        toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_home);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Dashboard.class));
            }
        });


//        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean beep = sharedPref.getBoolean("beep", true);
        Boolean frontCamera = sharedPref.getBoolean("frontCamera", false);



        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("Total"));


//        btn_calculate = findViewById(R.id.calculate);
//        btn_save = findViewById(R.id.save);
//        txt_cashUp = findViewById(R.id.pop_grand_total);
//        txt_difference = findViewById(R.id.pop_difference);
//        txt_total_sales = findViewById(R.id.pop_cash_paid);
        placeOrder = findViewById(R.id.btn_placeorder);


        placeOrder.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(ScanHome.this);
                View view = getLayoutInflater().inflate(R.layout.popup, null);

                //grandTotal.setText(String.format());
                //grandTotal.setText();
                String cash_paid;


                txt_grand_total = view.findViewById(R.id.pop_grand_total);


                txt_difference = view.findViewById(R.id.pop_difference);
                btn_calculate = view.findViewById(R.id.calculate);
                btn_save = view.findViewById(R.id.save);





                txt_grand_total.setText(grandTotal.getText());

                builder.setView(view);
                final AlertDialog dialog = builder.create();
                dialog.show();

                btn_calculate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(ScanHome.this);
                        final View view1 = getLayoutInflater().inflate(R.layout.popup, null);
                        String cash_paid, cash_pay;
                        String sales;
                        Double salesTotal;
                        Double cashPaidTotal;
                        Double difference;

                        final EditText edt_cash_paid = view1.findViewById(R.id.pop_cash_paid);

                        edt_cash_paid.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                ScanHome.this.updateValue();
                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }


                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });




                        edt_cash_paid.getText().toString();
                        //cash_paid = edt_cash_paid.getText().toString();
                        //final TextView txt_cash_paid = view1.findViewById(R.id.pop_cash_paid_tv);

                        //txt_cash_paid.setText(str1);
                        //edt_cash_paid.setText();
                      //  txt_cash_paid.setText(edt_cash_paid.getText());

                        sales = String.valueOf(txt_grand_total.getText().toString());
                        cash_pay = String.valueOf(edt_cash_paid.getText().toString());

                        salesTotal = Double.parseDouble(sales);
                        cashPaidTotal = Double.parseDouble(cash_pay);
                        difference = salesTotal - cashPaidTotal;
                        txt_difference.setText(String.format("%.2f", difference));



                    }

                });

//                //Save
//                btn_save.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        total_sales = Double.valueOf(txt_total_sales.getText().toString());
//                        cash = Double.valueOf(txt_cashUp.getText().toString());
//                        difference = Double.valueOf(txt_difference.getText().toString());
//
//
//                        if (total_sales.equals("") || cash.equals("")) {
//                            builder.setTitle("Something Went Wrong...");
//                            builder.setMessage("There was no sales for the day");
//                            displayAlert("input_error");
//                        } else {
//                            StringRequest stringRequest = new StringRequest(Request.Method.POST, CashUp_URL,
//                                    new Response.Listener<String>() {
//                                        @Override
//                                        public void onResponse(String response) {
//                                            try {
//                                                JSONArray jsonArray = new JSONArray(response);
//                                                JSONObject jsonObject = jsonArray.getJSONObject(0);
//                                                String code = jsonObject.getString("code");
//                                                String message = jsonObject.getString("message");
//
//                                                if(code.equals("cashup_success")){
//                                                    String userID = jsonObject.getString("user_id");
//                                                    String userName = jsonObject.getString("user_name");
//                                                    createSessions(userID, userName);
//                                                }
//                                                builder.setTitle("WeeShop Response");
//                                                builder.setMessage(message);
//                                                displayAlert(code);
//                                            } catch (JSONException e) {
//                                                e.printStackTrace();
//                                            }
//                                        }
//                                    }, new Response.ErrorListener() {
//                                @Override
//                                public void onErrorResponse(VolleyError error) {
//
//                                }
//                            }) {
//                                @Override
//                                protected Map<String, String> getParams() throws AuthFailureError {
//                                    Map<String, String> params = new HashMap<String, String>();
//
//                                    params.put("total_sales", total_sales.toString());
//                                    params.put("cash", cash.toString());
//                                    params.put("difference", difference.toString());
//                                    //params.put("user_id", userID);
//
//                                    return params;
//                                }
//                            };
//                            Singleton.getInstance(CashUp.this).addToRequestQueue(stringRequest);
//
//                        }
//
//                        txt_total_sales.setText("");
//                        txt_difference.setText("");
//                        txt_cashUp.setText("");
//                    }
//                });

            }
        });



        int camId;
        if (frontCamera == false)
            camId = 0;
        else
            camId = 1;
        sharedPref.registerOnSharedPreferenceChangeListener(this);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listItems = new ArrayList<>();
        adapter = new ScanAdapter(listItems, this);//Changes
        recyclerView.setAdapter(adapter);
        CardView cardView = findViewById(R.id.cardView);
        tv_total = findViewById(R.id.tv_total);
        grandTotal = findViewById(R.id.grand_total);


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
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


//        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
//                new IntentFilter("Total"));


    }

//    public final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//
//
//            Double grandTotalFinal = 0.00;
//            grandTotalFinal = intent.getDoubleExtra("grandTotalFinal", grandTotalFinal);
//
//
//            Double tsum = 0.00;
//            for (int i = 0; i < listItems.size(); i++) {
//                tsum = tsum + grandTotalFinal;
//            }
//            Log.d("total pay : ", String.valueOf(tsum));
//            Toast.makeText(ScanHome.this, grandTotalFinal.toString(), Toast.LENGTH_SHORT).show();
//
//            grandTotal.setText(tsum.toString());
//
//
//        }
//    };


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {

            if (result.getContents() == null) {
                Snackbar snackbar = Snackbar.make(findViewById(R.id.coordinate), "Result Not Found", Snackbar.LENGTH_LONG);

                snackbar.show();
            } else {
                try {
                    JSONObject obj = new JSONObject(result.getContents());

                    product_id = obj.getString("product_id");

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONArray jsonArray = new JSONArray(response);
                                        JSONObject jsonObject = jsonArray.getJSONObject(0);

                                        jsonObject.put("product_id", jsonArray);

                                        String name = jsonObject.getString("name");
//                                        String product_id = jsonObject.getString("product_id");
                                        Double price = jsonObject.getDouble("price");
                                        String qoh = jsonObject.getString("quantity");


                                        Product productList = new Product(product_id, name, price, qoh, 1);

                                        listItems.add(productList);
                                        adapter = new ScanAdapter(listItems, ScanHome.this);
                                        recyclerView.setAdapter(adapter);
//


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


                } catch (JSONException e) {
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
        if (key.equals("beep")) {
            scan.setBeepEnabled(sharedPreferences.getBoolean(key, true));
        }
        if (key.equals("frontCamera")) {
            int camId;
            if (sharedPreferences.getBoolean(key, false) == false)
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


//    private void callPopup() {
//
//        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
//                .getSystemService(LAYOUT_INFLATER_SERVICE);
//
//        View popupView = layoutInflater.inflate(R.layout.popup, null);
//
//        final PopupWindow popupWindow = new PopupWindow(popupView,
//                Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.MATCH_PARENT,
//                true);
//        final TextView Name;
//
//        popupWindow.setTouchable(true);
//        popupWindow.setFocusable(true);
//
//        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
//        Name = (EditText) popupView.findViewById(R.id.pop_cash_paid);
//
//        (popupView.findViewById(R.id.save))
//                .setOnClickListener(new View.OnClickListener() {
//
//                    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
//                    public void onClick(View arg0) {
//                        Toast.makeText(getApplicationContext(),
//                                Name.getText().toString(), Toast.LENGTH_LONG).show();
//
//                        popupWindow.dismiss();
//
//                    }
//
//                });
//        (popupView.findViewById(R.id.save)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Double money  = 0.00;
////                for (int i = 0; i < list.getCount(); i++) {
////                    double cashup_value = 0;
////                    quantity1 = getViewByPosition(i, list).findViewById(R.id.quantity);
////                    price1 = getViewByPosition(i, list).findViewById(R.id.price);
////                    edit3 = getViewByPosition(i, list).findViewById(R.id.editText3);
////                    final CashValueModel cashValueModel = cashValueModelList.get(i);
////
////                    if (!quantity1.getText().toString().equals("") && !price1.getText().toString().equals("")) {
////                        cashup_value = Integer.parseInt(String.valueOf(quantity1.getText())) * Double.parseDouble(String.valueOf(cashValueModel.getCashValue()));
////                    }
////                    edit3.setText(String.format("%.2f", cashup_value));
////                    money += Double.valueOf(edit3.getText().toString());
////                }
//
//                txt_cashUp.setText(String.format("%.2f", money));
//
//                //difference = sales - money;
//                //txt_difference.setText(String.format("%.2f", difference));
//
//
//
//            }
//
//        });
//
//        (popupView.findViewById(R.id.calculate))
//                .setOnClickListener(new View.OnClickListener() {
//
//                    public void onClick(View arg0) {
//
//                        popupWindow.dismiss();
//                    }
//                });
//    }
}