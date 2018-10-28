package com.example.frank.weeshop;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
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
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Dashboard extends AppCompatActivity {

    private Toolbar toolbar;
    LinearLayout dash_sales, dash_returns, dash_cash;
    Double total_sales;
    String total_sales_int;
    String NOTICE_URL = "http://sict-iis.nmmu.ac.za/weeshop/app/notice.php";
    String name, quantity;
    AlertDialog.Builder builder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        builder = new AlertDialog.Builder(Dashboard.this);
        toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        dash_cash = findViewById(R.id.dash_cash_up);
        dash_sales = findViewById(R.id.dash_sales);
        dash_returns = findViewById(R.id.dash_return);

        dash_sales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent scan = new Intent(Dashboard.this, SalesActivity.class);
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
                Intent returns = new Intent(Dashboard.this, ReturnHome.class);
                startActivity(returns);
            }
        });



        StringRequest stringRequest = new StringRequest(Request.Method.POST, NOTICE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String code = jsonObject.getString("code");
                            String message = jsonObject.getString("message");


                            if(code.equals("notice")){

                                Toast.makeText(Dashboard.this, message, Toast.LENGTH_SHORT).show();
                                builder.setTitle("WeeShop");
                                builder.setMessage(message);
                                //displayAlert(code);
                            }
                            builder.setTitle("WeeShop");
                            builder.setMessage(message);
                            //displayAlert(code);
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

                params.put("name", name);
                params.put("quantity", quantity);

                return params;
            }
        };
        Singleton.getInstance(Dashboard.this).addToRequestQueue(stringRequest);
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
        if (id == R.id.action_profile) {
            Intent logout = new Intent(Dashboard.this, Login.class);
            startActivity(logout);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void createNotification() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.plus)
                        .setContentTitle("Notifications Example")
                        .setContentText("This is a test notification");

        Intent notificationIntent = new Intent(this, Dashboard.class);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);


        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
        final String time = DateFormat.getTimeInstance().format(new Date()).toString();

    }
    public void showNotificationClicked(View v) {
        createNotification();
    }


}
