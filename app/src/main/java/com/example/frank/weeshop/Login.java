package com.example.frank.weeshop;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    Button Login;
    EditText Email, Password;
    String email, password;

    AlertDialog.Builder builder;
    String url = "http://sict-iis.nmmu.ac.za/weeshop/app/login.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Login = findViewById(R.id.login);
        Email = findViewById(R.id.email);
        Password = findViewById(R.id.password);

        builder = new AlertDialog.Builder(Login.this);

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = Email.getText().toString();
                password = Password.getText().toString();


                if (email.equals("") || password.equals("")) {
                    builder.setTitle("Something Went Wrong...");
                    builder.setMessage("Please fill in all fields");
                    displayAlert("input_error");
                } else {
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONArray jsonArray = new JSONArray(response);
                                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                                        String code = jsonObject.getString("code");
                                        String message = jsonObject.getString("message");

                                        if(code.equals("login_success")){
                                            String userID = jsonObject.getString("user_id");
                                            String userName = jsonObject.getString("user_name");
                                            createSessions(userID, userName);
                                        }
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

                            params.put("email", email);
                            params.put("password", password);

                            return params;
                        }
                    };
                    Singleton.getInstance(Login.this).addToRequestQueue(stringRequest);

                }
            }
        });

    }
    public void displayAlert(final String code) {
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (code.equals("input_error")) {
                    Password.setText("");
                }

                else if (code.equals("login_failed")) {
                    Password.setText("");
                }
                else if (code.equals("login_success")) {

                    Intent scannerPage = new Intent(Login.this, Dashboard.class);
                    startActivity(scannerPage);
                    Password.setText("");
                    Email.setText("");
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
}
