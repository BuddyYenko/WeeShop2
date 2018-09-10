package com.example.frank.weeshop;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class ReturnHome extends AppCompatActivity implements View.OnClickListener {

    String url = "http://sict-iis.nmmu.ac.za/weeshop/app/fetch_product.php";
    String RETURN_URL = "http://sict-iis.nmmu.ac.za/weeshop/app/returns.php";
    Button return_product;
    ImageView btn_scan;
    EditText txt_product_id, txt_name, txt_quantity;
    String userID, productID, productName;

    private RequestQueue queue;

    IntentIntegrator scan;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_home);

        queue = Volley.newRequestQueue(this);

        //********************************************
        SharedPreferences preferences = getSharedPreferences("MYPREFS", MODE_PRIVATE);

        userID = preferences.getString("user_id", "");
        productID = preferences.getString("product_id", "");
        productName = preferences.getString("name", "");

        //********************************************

        Intent intent=new Intent(getApplicationContext(),ReturnHome.class);
        final PendingIntent pi=PendingIntent.getActivity(getApplicationContext(), 0, intent,0);

        txt_product_id =  findViewById(R.id.ret_product_id);
        txt_name = findViewById(R.id.ret_name);
        txt_quantity = findViewById(R.id.ret_quantity);
        return_product = findViewById(R.id.return_product);


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            txt_product_id.setText(jsonObject.getString("product_id"));
                            txt_name.setText(jsonObject.getString("name"));
                            txt_quantity.setText(jsonObject.getString("quantity"));

                            String UserSession = jsonObject.getString("user_id");

                            String ProdSession = jsonObject.getString("product_id");

                            createUserSession(UserSession, ProdSession);
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

                params.put("product_id", productID);
                params.put("name", productName);

                return params;
            }
        };
        MySingleton.getInstance(ReturnHome.this).addToRequestque(stringRequest);
        txt_product_id.setText(productID);
        txt_name.setText(productName);



        return_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringRequest stringRequest = new StringRequest(Request.Method.POST, RETURN_URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONArray jsonArray = new JSONArray(response);
                                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                                    String userID = jsonObject.getString("user_id");
                                    String productID = jsonObject.getString("product_id");
                                    createUserSession(userID, productID);
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

                        SharedPreferences preferences = getSharedPreferences("MYPREFS", MODE_PRIVATE);


                        userID = preferences.getString("user_id", "");
                        params.put("user_id", userID);
                        params.put("product_id", productID);
                        params.put("name", productName);
                        return params;
                    }
                };
                MySingleton.getInstance(ReturnHome.this).addToRequestque(stringRequest);


            }
        });

        scan = new IntentIntegrator(this);
        btn_scan = findViewById(R.id.buttonScanner);
        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scan.initiateScan();
                txt_product_id.getText().clear();
                txt_name.getText().clear();
                txt_quantity.getText().clear();
            }
        });

    }


    private void createUserSession(String userID, String productID) {
        //***************** Session *****************
        SharedPreferences preferences = getSharedPreferences("MYPREFS", MODE_PRIVATE);

        String userSession = preferences.getString(productID + "data", productID);
        String prodSession = preferences.getString(productID + "data", productID);


        SharedPreferences.Editor editor = preferences.edit();
        //editor.putString(username + "data", username);
        //textViewJourneyID.setText(driverID);

        editor.putString("user_id", userSession);
        editor.putString("product_id", prodSession);

        editor.commit();

    }

    @Override
    public void onClick(View view)
    {

    }

}
