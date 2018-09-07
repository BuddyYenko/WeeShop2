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
    private List<CashValueModel> result;
    //new
    private List<CashValueModel> listCash;

    public CashUpAdapter(Context context, List<CashValueModel> result) {
        this.context = context;
        this.result = result;
    }
    private class ViewHolder {
        public EditText edit1, edit2, edit3;
    }
    @Override
    public int getCount() {
        return result.size() ;
    }

    @Override
    public Object getItem(int position) {
        return result.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
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

        final CashValueModel cashValueModel = result.get(position);
        ViewHolder viewHolder ;
        if (convertView == null){

            viewHolder = new ViewHolder();
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView =layoutInflater.inflate(R.layout.list_grid,parent,false);
            viewHolder.edit1 = convertView.findViewById(R.id.editText1);
            viewHolder.edit2 = convertView.findViewById(R.id.editText2);
            viewHolder.edit3 = convertView.findViewById(R.id.editText3);

            convertView.setTag(viewHolder);
            //viewHolder.edit2.setText(result.);

        }
        else {
            viewHolder =(ViewHolder) convertView.getTag();
        }
        viewHolder.edit2.setText(cashValueModel.getSymbol());

        return convertView;
    }


}
