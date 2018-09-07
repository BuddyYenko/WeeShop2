package com.example.frank.weeshop;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class CashUp extends AppCompatActivity {

    String[] result = {""};
    EditText editText, editText1, editText2, edit1, edit2, edit3;
    Button button;
    private List<CashValueModel> cashValueModelList;
    private CashUpAdapter cashUpAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_up);

        editText = findViewById(R.id.editText1);
        editText1 = findViewById(R.id.editText2);
        editText2 = findViewById(R.id.editText3);
        button = findViewById(R.id.multiply);

        cashValueModelList = new ArrayList<>();
        populateNote();

        final ListView list = findViewById(R.id.listView);

        final CashUpAdapter adapter = new CashUpAdapter(CashUp.this, cashValueModelList);
        list.setAdapter(adapter);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                HashMap<String, Object> objectHashMap = (HashMap<String, Object>)adapter.getItem()

                for (int i = 0; i < list.getCount(); i++) {
                    double a = 0;
                    edit1 = getViewByPosition(i, list).findViewById(R.id.editText1);
                    edit2 = getViewByPosition(i, list).findViewById(R.id.editText2);
                    edit3 = getViewByPosition(i, list).findViewById(R.id.editText3);
                    final CashValueModel cashValueModel = cashValueModelList.get(i);

                    if (!edit1.getText().toString().equals("") && !edit2.getText().toString().equals("")) {
                        a = Integer.parseInt(String.valueOf(edit1.getText())) * Double.parseDouble(String.valueOf(cashValueModel.getCashValue()));
                    }
                    edit3.setText(String.valueOf(a));
                }
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
        cashValueModelList.add(new CashValueModel("0.5", "50c"));
        cashValueModelList.add(new CashValueModel("0.2", "20c"));
        cashValueModelList.add(new CashValueModel("0.1", "10c"));


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
}
