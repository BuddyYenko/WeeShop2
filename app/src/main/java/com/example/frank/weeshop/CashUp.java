package com.example.frank.weeshop;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CashUp extends AppCompatActivity {

    String[] result = {""};
    CashUpAdapter cashUpAdapter;

    EditText quantity, price, editText2, quantity1, price1, edit3;
    Button btn_calculate, btn_cashUp;
    private List<CashValueModel> cashValueModelList;
    TextView txt_cashUp, txt_total_sales, txt_difference;
    Double money = 0.00, diff =0.00, sales = 0.00;

    AlertDialog.Builder builder;
    String CashUp_URL = "http://sict-iis.nmmu.ac.za/weeshop/app/cash_up.php";
    String FETCH_TOTAL_SALES = "http://sict-iis.nmmu.ac.za/weeshop/app/fetch_grand_total.php";
    Date cashup_date = new Date();
    Double total_sales;
    String total_sales_int, user_id;
    Double cash;
    Double difference;
    private android.support.v7.widget.Toolbar toolbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_up);

//        final ListView listView = findViewById(R.id.listView);
//        List<String> prod_list = new ArrayList<String>(Arrays.asList(result));
//
        //cashUpAdapter = new CashUpAdapter<Integer>(getApplicationContext(),R.layout.list_grid, result);
        //********************************************
        SharedPreferences preferences = getSharedPreferences("MYPREFS", MODE_PRIVATE);

        user_id = preferences.getString("user_id", "");

        //********************************************
        builder = new AlertDialog.Builder(CashUp.this);
        toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_home);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Dashboard.class));
            }
        });

        quantity = findViewById(R.id.quantity);
        price = findViewById(R.id.price);
        editText2 = findViewById(R.id.editText3);
        btn_calculate = findViewById(R.id.multiply);
        btn_cashUp  = findViewById(R.id.cashUp);
        txt_cashUp = findViewById(R.id.grand_total);
        txt_difference = findViewById(R.id.difference);
        txt_total_sales = findViewById(R.id.total_sales);

        cashValueModelList = new ArrayList<>();
        populateNote();

        final ListView list = findViewById(R.id.listView);

        final CashUpAdapter adapter = new CashUpAdapter(CashUp.this, cashValueModelList);
        list.setAdapter(adapter);

        txt_total_sales.setText(String.format("%.2f", sales));



        //Sales From Database
         StringRequest stringRequest = new StringRequest(Request.Method.GET, FETCH_TOTAL_SALES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);

                            txt_total_sales.setText(String.format("%.2f", jsonObject.getDouble("sales")));
                            Double dBsales = jsonObject.getDouble("sales");
                            setSales(dBsales);

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

                params.put("sales", sales.toString());

                return params;
            }
        };
        MySingleton.getInstance(CashUp.this).addToRequestque(stringRequest);
        txt_total_sales.setText(String.format("%.2f",sales));



        btn_calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                money  = 0.00;
                for (int i = 0; i < list.getCount(); i++) {
                    double cashup_value = 0;
                    quantity1 = getViewByPosition(i, list).findViewById(R.id.quantity);
                    price1 = getViewByPosition(i, list).findViewById(R.id.price);
                    edit3 = getViewByPosition(i, list).findViewById(R.id.editText3);
                    final CashValueModel cashValueModel = cashValueModelList.get(i);

                    if (!quantity1.getText().toString().equals("") && !price1.getText().toString().equals("")) {
                        cashup_value = Integer.parseInt(String.valueOf(quantity1.getText())) * Double.parseDouble(String.valueOf(cashValueModel.getCashValue()));
                    }
                    edit3.setText(String.format("%.2f", cashup_value));
                    money += Double.valueOf(edit3.getText().toString());
                }

                txt_cashUp.setText(String.format("%.2f", money));

                difference = sales - money;
                txt_difference.setText(String.format("%.2f", difference));



            }

        });


        //Cash Up
        btn_cashUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                total_sales = Double.valueOf(txt_total_sales.getText().toString());
                cash = Double.valueOf(txt_cashUp.getText().toString());
                difference = Double.valueOf(txt_difference.getText().toString());


                if (total_sales.equals("") || cash.equals("")) {
                    builder.setTitle("Something Went Wrong...");
                    builder.setMessage("There was no sales for the day");
                    displayAlert("input_error");
                } else {
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, CashUp_URL,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONArray jsonArray = new JSONArray(response);
                                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                                        String code = jsonObject.getString("code");
                                        String message = jsonObject.getString("message");

//                                        if(code.equals("cashup_success")){
//                                            String userID = jsonObject.getString("user_id");
//                                            String userName = jsonObject.getString("user_name");
//                                            createSessions(userID, userName);
//                                        }
                                        builder.setTitle("WeeShop");
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

                            params.put("total_sales", sales.toString());
                            params.put("cash", cash.toString());
                            params.put("difference", difference.toString());
                            params.put("user_id", user_id);

                            return params;
                        }
                    };
                    Singleton.getInstance(CashUp.this).addToRequestQueue(stringRequest);

                }

                txt_total_sales.setText("");
                txt_difference.setText("");
                txt_cashUp.setText("");
            }
        });
    }

    private void setSales(Double dBsales) {
        sales =dBsales;
    }

    private void populateNote() {
        cashValueModelList.add(new CashValueModel("200", "R200"));
        cashValueModelList.add(new CashValueModel("100", "R100"));
        cashValueModelList.add(new CashValueModel("50", "R50"));
        cashValueModelList.add(new CashValueModel("20", "R20"));
        cashValueModelList.add(new CashValueModel("10", "R10"));
        cashValueModelList.add(new CashValueModel("5", "R5"));
        cashValueModelList.add(new CashValueModel("2", "R2"));
        cashValueModelList.add(new CashValueModel("1", "R1"));
        cashValueModelList.add(new CashValueModel("0.50", "50c"));
        cashValueModelList.add(new CashValueModel("0.20", "20c"));
        cashValueModelList.add(new CashValueModel("0.10", "10c"));


        cashUpAdapter = new CashUpAdapter(CashUp.this, cashValueModelList);
    }



    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    public void displayAlert(final String code) {
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (code.equals("input_error")) {

                }

                else if (code.equals("cashup_failed")) {

                }
                else if (code.equals("cashup_success")) {
                    Intent home = new Intent(CashUp.this, Dashboard.class);
                    startActivity(home);
                }

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        //for negative side button
        alertDialog.getButton(alertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.deeppurple));
        //for positive side button
        alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.deeppurple));
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
                    total_sales_int = obj.getString("sales");



                    Intent mainPage = new Intent(CashUp.this, CashUp.class);
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
