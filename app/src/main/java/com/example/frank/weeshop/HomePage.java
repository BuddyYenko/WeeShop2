package com.example.frank.weeshop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

public class HomePage extends AppCompatActivity implements View.OnClickListener {
    private RequestQueue queue;

    //view objects
    ImageView buttonScan;
    public String product_id, user_id;


    //qr code scanner object
    private IntentIntegrator qrScan;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_home);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Dashboard.class));
            }
        });

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
                try
                {
                    JSONObject obj = new JSONObject(result.getContents());
                    product_id = obj.getString("product_id");

                    SharedPreferences preferences = getSharedPreferences("MYPREFS", MODE_PRIVATE);

                    String productIDSession = preferences.getString(product_id + "data", product_id);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("product_id", productIDSession);
                    editor.putString("user_id", user_id);
                    editor.commit();

                    Intent scanHome = new Intent(HomePage.this, ScanHome.class);
                    startActivity(scanHome);

                } catch (JSONException e) {
                    e.printStackTrace();

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
