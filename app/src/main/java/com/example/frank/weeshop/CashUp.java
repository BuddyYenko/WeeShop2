package com.example.frank.weeshop;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CashUp extends AppCompatActivity {

    String[] result = {""};
    EditText quantity, price, editText2, quantity1, price1, edit3;
    Button btn_calculate, btn_cashUp;
    private List<CashValueModel> cashValueModelList;
    private CashUpAdapter cashUpAdapter;
    TextView txt_cashUp, txt_total_sales, txt_difference;
    Double money = 0.00, diff =0.00, sales = 0.00;

    AlertDialog.Builder builder;
    String CashUp_URL = "http://sict-iis.nmmu.ac.za/weeshop/app/cash_up.php";
    Date cashup_date = new Date();
    Double total_sales, cash, difference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_up);

        builder = new AlertDialog.Builder(CashUp.this);

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

                                        if(code.equals("cashup_success")){
                                            String userID = jsonObject.getString("user_id");
                                            String userName = jsonObject.getString("user_name");
                                            createSessions(userID, userName);
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

                            params.put("total_sales", total_sales.toString());
                            params.put("cash", cash.toString());
                            params.put("difference", difference.toString());
                            //params.put("user_id", userID);

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
                    //Password.setText("");
                }

                else if (code.equals("cashup_failed")) {
                    //Password.setText("");
                }
                else if (code.equals("cashup_success")) {
                    //Password.setText("");
                    //Email.setText("");
                    Intent home = new Intent(CashUp.this, Dashboard.class);
                    startActivity(home);
                }

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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
}
