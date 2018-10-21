package com.example.frank.weeshop;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

public class Dashboard extends AppCompatActivity {

    private Toolbar toolbar;
    LinearLayout dash_sales, dash_returns, dash_cash;
    Double total_sales;
    String total_sales_int;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        dash_cash = findViewById(R.id.dash_cash_up);
        dash_sales = findViewById(R.id.dash_sales);
        dash_returns = findViewById(R.id.dash_return);

        dash_sales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent scan = new Intent(Dashboard.this, ScanHome.class);
                startActivity(scan);
            }
        });

        dash_cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cash = new Intent(Dashboard.this, CashUp.class);
                startActivity(cash);
            }
        });

        dash_returns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returns = new Intent(Dashboard.this, Returns.class);
                startActivity(returns);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            Intent logout = new Intent(Dashboard.this, Login.class);
            startActivity(logout);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
