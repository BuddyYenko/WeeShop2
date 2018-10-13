package com.example.frank.weeshop;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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
    String SALES_URL = "http://sict-iis.nmmu.ac.za/weeshop/app/sales.php";
    String product_id, userID;
    public static double total = 0;
    public static TextView tv_total;
    public static TextView grandTotal;
    public static EditText cashPaid;
    public double finalTotal;
    public List<Product> listItems;
    //    private BreakIterator txtCount;
    private android.support.v7.widget.Toolbar toolbar;
    AlertDialog.Builder builder;



    public PopupWindow popupWindow;
    TextView txt_grand_total, txt_difference, txt_cash_paid;
    EditText edt_cash_paid;
    Button btn_calculate, btn_save;
    Button placeOrder;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_home);

        builder = new AlertDialog.Builder(ScanHome.this);

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

        placeOrder = findViewById(R.id.btn_placeorder);
        placeOrder.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(ScanHome.this);
                View view = getLayoutInflater().inflate(R.layout.popup, null);


                String cash_paid;

                txt_grand_total = view.findViewById(R.id.pop_grand_total);
                txt_cash_paid = view.findViewById(R.id.pop_cash_paid);
                txt_difference = view.findViewById(R.id.pop_difference);
                btn_calculate = view.findViewById(R.id.calculate);
                btn_save = view.findViewById(R.id.save);
                txt_grand_total.setText(grandTotal.getText());
                edt_cash_paid = findViewById(R.id.edt_cash_paid);

                cash_paid = edt_cash_paid.getText().toString();
                txt_cash_paid.setText((CharSequence) cashPaid);
                txt_cash_paid.setText(cash_paid);

                builder.setView(view);
                final AlertDialog dialog = builder.create();
                dialog.show();

                btn_calculate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String cash_paid;
                        String sales;
                        Double salesTotal;
                        Double cashPaidTotal;
                        Double difference;

                        sales = String.valueOf(txt_grand_total.getText().toString());
                        cash_paid = String.valueOf(edt_cash_paid.getText().toString());

                        salesTotal = Double.parseDouble(sales);
                        cashPaidTotal = Double.parseDouble(cash_paid);
                        difference = cashPaidTotal - salesTotal;
                        txt_difference.setText(String.format("%.2f", difference));

                    }

                });

                //Save
                btn_save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Double grandTotal;
                        grandTotal = Double.parseDouble(txt_grand_total.getText().toString());

                        final Double finalGrandTotal = grandTotal;
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, SALES_URL,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {

                                            try {
                                                JSONArray jsonArray = new JSONArray(response);
                                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                                String code = jsonObject.getString("code");
                                                String message = jsonObject.getString("message");

                                                if(code.equals("sales_success")){
                                                    String userID = jsonObject.getString("user_id");
                                                    String userName = jsonObject.getString("user_name");
                                                    createSessions(userID, userName);
                                                }
//                                                builder.setTitle("WeeShop Response");
//                                                builder.setMessage(message);
                                                displayAlert(code);
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

                                    params.put("finalGrandTotal", finalGrandTotal.toString());
                                    params.put("user_id", userID);

                                    return params;
                                }
                            };
                            Singleton.getInstance(ScanHome.this).addToRequestQueue(stringRequest);


                    }
                });

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
        scan.setOrientationLocked(false);
        //scan.getCaptureActivity(Ca);
        scan.setBeepEnabled(false);
        scan.setCameraId(camId);

        btn_scan = findViewById(R.id.btn_scan);
        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scan.initiateScan();
            }
        });


    }

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

                    Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }



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

    public void createSessions(String userID, String userName) {

        SharedPreferences preferences = getSharedPreferences("MYPREFS", MODE_PRIVATE);

        String userIDSession = preferences.getString(userID + "data", userID);
        String userNameSession = preferences.getString(userName + "data", userName);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("user_id", userIDSession);
        editor.putString("user_name", userNameSession);

        editor.commit();

    }
    public void displayAlert(final String code) {
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (code.equals("input_error")) {
                    //Password.setText("");
                }

                else if (code.equals("cashup_failed")) {
                    //Password.setText("");
                }
                else if (code.equals("cashup_success")) {
                    //Password.setText("");
                    //Email.setText("");
                    Intent home = new Intent(ScanHome.this, ScanHome.class);
                    startActivity(home);
                }

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}