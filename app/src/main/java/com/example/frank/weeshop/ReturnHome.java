package com.example.frank.weeshop;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class ReturnHome extends AppCompatActivity{

    String FETCH_URL = "http://sict-iis.nmmu.ac.za/weeshop/app/fetch_product.php";
    String RETURN_URL ="http://sict-iis.nmmu.ac.za/weeshop/app/returns.php";
    Button return_product;
    ImageView btn_scan;
    EditText txt_product_id, txt_name, txt_quantity, txt_message;
    String userID, productID, productName, quantity, message;
    public String product_id, user_id;

    private RequestQueue queue;
    private android.support.v7.widget.Toolbar toolbar;


    IntentIntegrator scan;
    android.support.v7.app.AlertDialog.Builder builder;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_home);


        toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_home);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Dashboard.class));
            }
        });


        builder = new android.support.v7.app.AlertDialog.Builder(ReturnHome.this);

        queue = Volley.newRequestQueue(this);

        //********************************************
        SharedPreferences preferences = getSharedPreferences("MYPREFS", MODE_PRIVATE);

        userID = preferences.getString("user_id", "");
        productID = preferences.getString("product_id", "");
        productName = preferences.getString("name", "");

        //********************************************

        Intent intent=new Intent(getApplicationContext(),ReturnHome.class);
        final PendingIntent pi=PendingIntent.getActivity(getApplicationContext(), 0, intent,0);

        //txt_product_id =  findViewById(R.id.ret_product_id);
        txt_name = findViewById(R.id.ret_name);
        txt_quantity = findViewById(R.id.ret_quantity);
        return_product = findViewById(R.id.return_product);
        txt_message = findViewById(R.id.ret_sms);


        StringRequest stringRequest = new StringRequest(Request.Method.POST, FETCH_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            //txt_product_id.setText(jsonObject.getString("product_id"));
                            txt_name.setText(jsonObject.getString("name"));
                            txt_quantity.setText(jsonObject.getString("quantity"));


//
//                            String UserSession = jsonObject.getString("user_id");
//
//                            String ProdSession = jsonObject.getString("product_id");
//
//                            createUserSession(UserSession, ProdSession);
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
        //txt_product_id.setText(productID);
        txt_name.setText(productName);





        return_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                //productID = txt_product_id.getText().toString();
                productName = txt_name.getText().toString();
                quantity = txt_quantity.getText().toString();
                message = txt_message.getText().toString();


                if (productID.equals("")) {
                    builder.setTitle("Something Went Wrong...");
                    builder.setMessage("Quantity cannot be empty or 0.");
                    displayAlert("input_error");
                } else {
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, RETURN_URL,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONArray jsonArray = new JSONArray(response);
                                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                                        String code = jsonObject.getString("code");
                                        String message = jsonObject.getString("message");

                                        if (code.equals("return_success")) {
                                            String userID = jsonObject.getString("user_id");
                                            String productID = jsonObject.getString("product_id");
                                            createSessions(userID, productID);
                                        }
                                        builder.setTitle("WeeShop Response");
                                        builder.setMessage(message);
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

                            SharedPreferences preferences = getSharedPreferences("MYPREFS", MODE_PRIVATE);


                            userID = preferences.getString("user_id", "");
                            params.put("user_id", userID);
                            params.put("product_id", productID);
                            params.put("name", productName);
                            params.put("quantity", quantity);
                            params.put("message", message);
                            return params;
                        }
                    };
                    MySingleton.getInstance(ReturnHome.this).addToRequestque(stringRequest);

                }
            }
        });

        scan = new IntentIntegrator(this);
        btn_scan = findViewById(R.id.buttonScanner);
        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scan.initiateScan();
                //txt_product_id.getText().clear();
                txt_name.getText().clear();
                txt_quantity.getText().clear();
                txt_message.getText().clear();
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

    public void displayAlert(final String code) {
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (code.equals("input_error")) {
                    //Password.setText("");
                }

                else if (code.equals("return_failed")) {
                    //Password.setText("");
                }
                else if (code.equals("return_success")) {
                    //Password.setText("");
                    //Email.setText("");
                    Intent home = new Intent(ReturnHome.this, Dashboard.class);
                    startActivity(home);
                }

            }
        });

        android.support.v7.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void createSessions(String userID, String productID) {

        SharedPreferences preferences = getSharedPreferences("MYPREFS", MODE_PRIVATE);

        String userIDSession = preferences.getString(userID + "data", userID);
        String productIDSession = preferences.getString(productID + "data", productID);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("user_id", userIDSession);
        editor.putString("product_id", productIDSession);

        editor.commit();

    }

    //Getting results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null)
        {
            if (result.getContents() == null)
            {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            }
            else
            {
                //if qrcode contains data
                try
                {
                    //converting the data to json
                    JSONObject obj = new JSONObject(result.getContents());
                    //setting values to textviews
                    product_id = obj.getString("product_id");

                    //session
                    SharedPreferences preferences = getSharedPreferences("MYPREFS", MODE_PRIVATE);

                    String regNoSession = preferences.getString(product_id + "data", product_id);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("product_id", regNoSession);
                    //editor.putString("user_id", user_id);
                    editor.commit();

                    Intent mainPage = new Intent(ReturnHome.this, ReturnHome.class);
                    startActivity(mainPage);

                } catch (JSONException e) {
                    e.printStackTrace();
                    //if control comes here
                    //that means the encoded format not matches
                    //in this case you can display whatever data is available on the qrcode
                    //to a toast
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                }
            }
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
