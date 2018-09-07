package com.example.frank.weeshop;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class CashUp extends AppCompatActivity {

    String[] result = {""};
    EditText editText, editText1, editText2, edit1, edit2, edit3;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_up);

        editText = findViewById(R.id.editText1);
        editText1 = findViewById(R.id.editText2);
        editText2 = findViewById(R.id.editText3);
        button = findViewById(R.id.multiply);

        final ListView list = findViewById(R.id.listView);

        CashUpAdapter adapter = new CashUpAdapter(getApplicationContext(), result);
        list.setAdapter(adapter);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < list.getCount(); i++) {
                    int a = 0;
                    edit1 = getViewByPosition(i, list).findViewById(R.id.editText1);
                    edit2 = getViewByPosition(i, list).findViewById(R.id.editText2);
                    edit3 = getViewByPosition(i, list).findViewById(R.id.editText3);
                    if (!edit1.getText().toString().equals("") && !edit2.getText().toString().equals("")) {
                        a = Integer.parseInt(String.valueOf(edit1.getText())) * Integer.parseInt(String.valueOf(edit2.getText().toString()));
                    }
                    edit3.setText(String.valueOf(a));
                }
            }
        });

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
