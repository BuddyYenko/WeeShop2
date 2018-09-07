package com.example.frank.weeshop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;

import java.util.List;

public class CashUpAdapter extends BaseAdapter {

    private Context context;
    private String[] result;
    //new
    private List<CashValue> listCash;

    public CashUpAdapter(Context context, String[] result) {
        this.context = context;
        this.result = result;
    }

    @Override
    public int getCount() {
        return 18 ;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 500;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null){

            viewHolder = new ViewHolder();
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView =layoutInflater.inflate(R.layout.list_grid,parent,false);
            viewHolder.edit1 = convertView.findViewById(R.id.editText1);
            viewHolder.edit2 = convertView.findViewById(R.id.editText2);
            viewHolder.edit3 = convertView.findViewById(R.id.editText3);

            convertView.setTag(viewHolder);
        }
        return convertView;
    }

    private class ViewHolder {
        public EditText edit1, edit2, edit3;
    }
}
