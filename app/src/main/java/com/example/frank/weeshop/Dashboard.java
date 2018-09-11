package com.example.frank.weeshop;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageButton;

public class Dashboard extends AppCompatActivity {
    CardView scanPage, cashUp, return_product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        scanPage = findViewById(R.id.scanPage);
        cashUp = findViewById(R.id.cashUp);
        return_product = findViewById(R.id.return_product);


        scanPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent scan = new Intent(Dashboard.this, ScanHome.class);
                startActivity(scan);
            }
        });

        cashUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cash = new Intent(Dashboard.this, CashUp.class);
                startActivity(cash);
            }
        });

        return_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returns = new Intent(Dashboard.this, Returns.class);
                startActivity(returns);
            }
        });
    }
}
