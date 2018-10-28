package com.example.frank.weeshop;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.shockwave.pdfium.PdfDocument;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SalesActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener, OnPageChangeListener, OnLoadCompleteListener {
    String url = "http://sict-iis.nmmu.ac.za/weeshop/app/fetch.php";
    String SALES_URL = "http://sict-iis.nmmu.ac.za/weeshop/app/sales.php";

    String user_id, product_id;
    private List<Product> list;
    private RecyclerView recyclerView;
    private ReturnAdapter aAdapter;
    AlertDialog.Builder builder;
    Double grandTotal = 0.00, difference = 0.00, cash_paid = 0.00;
    TextView tv_grandTotal;
    Button submit;
    ArrayList<String> mylista = new ArrayList<String>();
    ArrayList<String> mylistb = new ArrayList<String>();
    ImageView btn_scan;
    IntentIntegrator scan;

    private android.support.v7.widget.Toolbar toolbar;
    TextView txt_grand_total, txt_difference, txt_cash_paid;
    EditText edt_cash_paid;
    Button btn_calculate, btn_save;
    Button placeOrder;


    //OnMyDialogResult mDialogResult; // the callback
    String name;
    private static String strEditText = null;

    private static final String TAG = SalesActivity.class.getSimpleName();
    public static final String SAMPLE_FILE = "weeshop_receipt.pdf";
    PDFView pdfView;
    Integer pageNumber = 0;
    String pdfFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);


        //pdfView = findViewById(R.id.pdfView);
        //displayFromAsset(SAMPLE_FILE);




        builder = new AlertDialog.Builder(SalesActivity.this);


        toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_home);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Dashboard.class));
            }
        });

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean beep = sharedPref.getBoolean("beep", true);
        Boolean frontCamera = sharedPref.getBoolean("frontCamera", false);

        //********************************************
        SharedPreferences preferences = getSharedPreferences("MYPREFS", MODE_PRIVATE);
        user_id = preferences.getString("user_id", "");
        //********************************************

        list = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        tv_grandTotal = findViewById(R.id.grand_total);
        edt_cash_paid = findViewById(R.id.edt_cash_paid);
        submit = findViewById(R.id.btn_placeorder);
        aAdapter = new ReturnAdapter(list);

        recyclerView.setLayoutManager(new LinearLayoutManager(SalesActivity.this));
        recyclerView.setAdapter(aAdapter);
        builder = new AlertDialog.Builder(SalesActivity.this);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-message"));

        int camId;
        if (frontCamera == false)
            camId = 0;
        else
            camId = 1;
        sharedPref.registerOnSharedPreferenceChangeListener(this);

        scan = new IntentIntegrator(this);
        scan.setBeepEnabled(false);
        scan.setCameraId(camId);

        btn_scan = findViewById(R.id.btn_scan);
        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scan.initiateScan();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int listSize = mylista.size();
                String res = edt_cash_paid.getText().toString();
                if (res.equals(""))
                {
                    difference=0.00;
                }
                else {
                    difference = Double.parseDouble(res) - grandTotal;
                }
                try {
                    //lista product_id
                    JSONObject mainObjectA = new JSONObject();
                    final JSONArray product_id = new JSONArray();
                    for ( int i = 0; i<listSize; i++) {
                        JSONObject idsJsonObjectA = new JSONObject();
                        idsJsonObjectA.put("product_id", mylista.get(i));
                        product_id.put(idsJsonObjectA);
                    }
                    mainObjectA.put("product_id", product_id);

                    //merge listA and listB quantity
                    JSONObject mainObjectB = new JSONObject();
                    final JSONArray quantity = new JSONArray();
                    for ( int i = 0; i<listSize; i++) {
                        JSONObject idsJsonObjectB = product_id.getJSONObject(i);
                        idsJsonObjectB.put("quantity", mylistb.get(i));
                        quantity.put(idsJsonObjectB);
                    }
                    mainObjectB.put("quantity", quantity);

                    JSONArray jsonArray = new JSONArray();
                    JSONArray jsonArray1 = new JSONArray();
                    for (int i = 0; i < jsonArray1.length(); i++) {
                        JSONObject jsonObject = jsonArray1.getJSONObject(i);
                        jsonArray.put(jsonObject);
                    }





                    StringRequest stringRequest = new StringRequest(Request.Method.POST, SALES_URL,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONArray jsonArray = new JSONArray(response);
                                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                                        String code = jsonObject.getString("code");
                                        String message = jsonObject.getString("message");

                                        builder.setTitle("WeeShop");
                                        builder.setMessage(message + "\n\nCustomer Change : " + String.format("%.2f", difference));
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

                            params.put("user_id", user_id);
                            params.put("grand_total", grandTotal.toString());
                            params.put("list", quantity.toString());
                            return params;
                        }
                    };
                    MySingleton.getInstance(SalesActivity.this).addToRequestque(stringRequest);
                }
                catch (JSONException ex) {
                    ex.printStackTrace();
                }


            }
        });
    }

//    private void displayFromAsset(String assetFileName) {
//        pdfFileName = assetFileName;
//
//
//
//        pdfView.fromAsset(SAMPLE_FILE)
//                .defaultPage(pageNumber)
//                .enableSwipe(true)
//
//                .swipeHorizontal(false)
//                .onPageChange(this)
//                .enableAnnotationRendering(true)
//                .onLoad(this)
//                .scrollHandle(new DefaultScrollHandle(this))
//                .load();
//    }



    private void displayAlert(final String code) {
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (code.equals("sales_success")) {
                    Intent scannerPage = new Intent(SalesActivity.this, Dashboard.class);
                    startActivity(scannerPage);
                }
                else if (code.equals("sales_failed")) {

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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {

            if (result.getContents() == null) {
                Snackbar snackbar = Snackbar.make(findViewById(R.id.coordinate), "Result Not Found", Snackbar.LENGTH_LONG);

                snackbar.show();
            } else {
                try {
                    JSONObject obj = new JSONObject(result.getContents());

                    product_id = obj.getString("product_id");

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONArray jsonArray = new JSONArray(response);
                                        JSONObject object = jsonArray.getJSONObject(0);
//                                        for (int i = 0; i < jsonArray.length(); i++) {
//                                            //getting request object from json array
//
//                                            //adding the product to list
//
//                                        }
                                        JSONObject request = jsonArray.getJSONObject(0);

                                        list.add(new Product(
                                                request.getString("product_id"),
                                                request.getString("name"),
                                                request.getDouble("price"),
                                                request.getString("quantity")
                                        ));
                                        Double price = object.getDouble("price");
                                        product_id = object.getString("product_id");
                                        updateTotal(price, product_id, "1");

                                        if(list.size() > 1)
                                        {
                                            SalesAdapter adapter = new SalesAdapter(SalesActivity.this, list, SalesActivity.this);
                                            aAdapter = new ReturnAdapter(list);
                                            adapter.notifyDataSetChanged();

                                        }
                                        else
                                        {
                                            SalesAdapter adapter = new SalesAdapter(SalesActivity.this, list, SalesActivity.this);
                                            recyclerView.setAdapter(adapter);
                                        }


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

                            params.put("product_id", product_id);

                            return params;
                        }
                    };
                    MySingleton.getInstance(SalesActivity.this).addToRequestque(stringRequest);


                } catch (JSONException e) {
                    e.printStackTrace();

                    Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void updateTotal(Double price, String product_id, String sales_qty) {
        grandTotal = grandTotal + price;
        mylista.add(product_id);
        mylistb.add(sales_qty);
        tv_grandTotal.setText(String.format("%.2f", grandTotal));




    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("beep")) {
            scan.setBeepEnabled(sharedPreferences.getBoolean(key, true));
        }
        if (key.equals("frontCamera")) {
            int camId;
            if (sharedPreferences.getBoolean(key, false) == false)
                camId = 0;
            else
                camId = 1;
            scan.setCameraId(camId);
        }
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            Double total = Double.parseDouble(intent.getStringExtra("total"));
            String operation = intent.getStringExtra("operation");
            String product_id = intent.getStringExtra("product_id");
            String sale_qty = intent.getStringExtra("sale_qty");

            if(operation == "increase")
            {
                grandTotal = grandTotal + total;
            }
            else if(operation == "decrease")
            {
                grandTotal = grandTotal - total;
            }
//            tv_grandTotal.setText(String.valueOf(grandTotal));
            tv_grandTotal.setText(String.format("%.2f", grandTotal));
            boolean exists = false;
            int index = -1;

            if(mylista.size() != 0)
            {
                for(int i = 0; i < mylista.size(); i++)
                {
                    if(mylista.get(i) == product_id)
                    {
                        exists = true;
                        index = i;
                        break;
                    }
                }
            }

            if(exists == true)
            {
                mylistb.set(index, sale_qty);
            }
            else{
                mylista.add(product_id);
                mylistb.add(sale_qty);
            }
        }
    };

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
        setTitle(String.format("%s %s / %s", pdfFileName, page + 1, pageCount));
    }

    @Override
    public void loadComplete(int nbPages) {
        PdfDocument.Meta meta = pdfView.getDocumentMeta();
        printBookmarksTree(pdfView.getTableOfContents(), "-");

    }

    public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
        for (PdfDocument.Bookmark b : tree) {

            Log.e(TAG, String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()));

            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
        }
    }
}
