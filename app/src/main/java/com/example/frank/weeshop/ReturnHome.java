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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReturnHome extends AppCompatActivity{

    String FETCH_URL = "http://sict-iis.nmmu.ac.za/weeshop/app/fetch_product.php";
    String RETURN_URL ="http://sict-iis.nmmu.ac.za/weeshop/app/returns.php";
    Button get_products;
    ImageView btn_get;
    EditText txt_sales_id;
    String sales_id, userID;
    private RequestQueue queue;
    private android.support.v7.widget.Toolbar toolbar;
    private List<Product> list;


    IntentIntegrator scan;
    android.support.v7.app.AlertDialog.Builder builder;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_home);

        list = new ArrayList<>();

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

        //********************************************

        final Intent intent=new Intent(getApplicationContext(),ReturnHome.class);
        final PendingIntent pi=PendingIntent.getActivity(getApplicationContext(), 0, intent,0);

        //txt_product_id =  findViewById(R.id.ret_product_id);
        txt_sales_id = findViewById(R.id.sales_id);
        get_products = findViewById(R.id.get_products);



        //txt_product_id.setText(productID);





        get_products.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sales_id = txt_sales_id.getText().toString();
                final Intent i =new Intent(getApplicationContext(),ReturnActivity.class);
                //Intent i = new Intent(this, ReturnActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("sales_id", sales_id);
                i.putExtras(bundle);
                startActivity(i);

            }
        });

    }


}
