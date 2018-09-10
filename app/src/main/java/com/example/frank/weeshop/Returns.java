package com.example.frank.weeshop;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
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

public class Returns extends AppCompatActivity implements View.OnClickListener {
    private RequestQueue queue;

    //view objects
    ImageView buttonScan;
    public String product_id, user_id;


    //qr code scanner object
    private IntentIntegrator qrScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_returns);

        SharedPreferences preferences = getSharedPreferences("MYPREFS", MODE_PRIVATE);

        user_id = preferences.getString("user_id", "");

        queue = Volley.newRequestQueue(this);
        buttonScan = findViewById(R.id.buttonScanner);
        qrScan = new IntentIntegrator(this);
        buttonScan.setOnClickListener(this);


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

                    Intent mainPage = new Intent(Returns.this, ReturnHome.class);
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
    @Override
    public void onClick(View view)
    {
        qrScan.initiateScan();
    }

}
