package com.example.frank.weeshop;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReturnActivity extends AppCompatActivity {
    String FETCH_URL = "http://sict-iis.nmmu.ac.za/weeshop/app/sales_product.php";
    String RETURN_URL = "http://sict-iis.nmmu.ac.za/weeshop/app/returns.php";

    String sales_id, comment;
    private List<Product> list;
    private RecyclerView recyclerView;
    private ReturnAdapter aAdapter;
    AlertDialog.Builder builder;
    Double grandTotal = 0.00;
    TextView tv_grandTotal;
    EditText et_comment;
    Button submit;
    ArrayList<String> mylista = new ArrayList<String>();
    ArrayList<String> mylistb = new ArrayList<String>();
    Map<String,String> myMap = new HashMap<String,String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return);
        Bundle bundle = getIntent().getExtras();
        sales_id = bundle.getString("sales_id");

        list = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        tv_grandTotal = findViewById(R.id.grand_total);
        submit = findViewById(R.id.btn_return);
        et_comment = findViewById(R.id.comment);
        aAdapter = new ReturnAdapter(list);

        recyclerView.setLayoutManager(new LinearLayoutManager(ReturnActivity.this));
        recyclerView.setAdapter(aAdapter);
        builder = new AlertDialog.Builder(ReturnActivity.this);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-message"));
        get_products();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int listSize = mylista.size();

                try {
                    //lista product_id
                    JSONObject mainObjectA = new JSONObject();
                    final JSONArray product_id = new JSONArray();
                    for ( int i = 0; i<listSize; i++) {
                        JSONObject idsJsonObjectA = new JSONObject();
                        idsJsonObjectA.put("product_id", mylista.get(i));
                        product_id.put(idsJsonObjectA);
                    }
                    mainObjectA.put("product_id", product_id);

                    //listB quantity
                    JSONObject mainObjectB = new JSONObject();
                    final JSONArray quantity = new JSONArray();
                    for ( int i = 0; i<listSize; i++) {
                        JSONObject idsJsonObjectB = product_id.getJSONObject(i);
                        idsJsonObjectB.put("quantity", mylistb.get(i));
                        quantity.put(idsJsonObjectB);
                    }
                    mainObjectB.put("quantity", quantity);

                    JSONArray jsonArray = new JSONArray();
                    JSONArray jsonArray1 = new JSONArray();
                        for (int i = 0; i < jsonArray1.length(); i++) {
                            JSONObject jsonObject = jsonArray1.getJSONObject(i);
                            jsonArray.put(jsonObject);
                        }


                    comment = et_comment.getText().toString();

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, RETURN_URL,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONArray jsonArray = new JSONArray(response);
                                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                                        String code = jsonObject.getString("code");
                                        String message = jsonObject.getString("message");

                                        builder.setTitle("WeeShop Response");
                                        builder.setMessage(message);
                                        displayAlert(code);
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

                            params.put("sales_id", sales_id);
                            params.put("comment", comment);
                            params.put("product_list", product_id.toString());
                            params.put("quantity_list", quantity.toString());


                            return params;
                        }
                    };
                    MySingleton.getInstance(ReturnActivity.this).addToRequestque(stringRequest);
                }
                catch (JSONException ex) {
                    ex.printStackTrace();
                }            }
        });
    }

    private void get_products() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, FETCH_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            //txt_product_id.setText(jsonObject.getString("product_id"));
                            //txt_name.setText(jsonObject.getString("name"));
                            // txt_quantity.setText(jsonObject.getString("quantity"));
                            for (int i = 0; i < jsonArray.length(); i++) {

                                //getting request object from json array
                                JSONObject request = jsonArray.getJSONObject(i);

                                //adding the request to request list_open

                                list.add(new Product(
                                        request.getString("sales_id"),
                                        request.getString("product_id"),
                                        request.getString("name"),
                                        request.getDouble("price"),
                                        request.getString("quantity")
                                ));
                            }
                            ReturnAdapter adapter = new ReturnAdapter(ReturnActivity.this, list, ReturnActivity.this);
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
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("sales_id", sales_id);

                return params;
            }
        };
        MySingleton.getInstance(ReturnActivity.this).addToRequestque(stringRequest);
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            Double total = Double.parseDouble(intent.getStringExtra("total"));
            String operation = intent.getStringExtra("operation");
            String product_id = intent.getStringExtra("product_id");
            String return_qty = intent.getStringExtra("return_qty");

            if(operation == "increase")
            {
                grandTotal = grandTotal + total;
            }
            else if(operation == "decrease")
            {
                grandTotal = grandTotal - total;
            }
            tv_grandTotal.setText(String.valueOf(grandTotal));
            boolean exists = false;
            int index = -1;

            if(mylista.size() != 0)
            {
                for(int i = 0; i < mylista.size(); i++)
                {
                    if(mylista.get(i) == product_id)
                    {
                        exists = true;
                        index = i;
                        break;
                    }
                }
            }

            if(exists == true)
            {
                mylistb.set(index, return_qty);
            }
            else{
                mylista.add(product_id);
                mylistb.add(return_qty);
            }


            for (Map.Entry<String, String> entry : myMap.entrySet()) {
                System.out.println(entry.getKey() + " - " + entry.getValue());
                Toast.makeText(context, entry.getValue(), Toast.LENGTH_SHORT).show();
            }
        }
    };

    public void displayAlert(final String code) {
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


//                if (code.equals("return_failed")) {
//                    et_comment.setText("");
//                }
//                else
                if (code.equals("return_success")) {
                    et_comment.setText("");
                    Intent scannerPage = new Intent(ReturnActivity.this, Dashboard.class);
                    startActivity(scannerPage);
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
