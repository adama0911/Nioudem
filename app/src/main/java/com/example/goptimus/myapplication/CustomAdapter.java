package com.example.goptimus.myapplication;


import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {

    Context context;
    ArrayList<ItemRow> arrayList;

    public CustomAdapter(Context context,JSONObject jsonObj) throws JSONException {
        this.context = context;
        arrayList   =   new ArrayList<>();

        int[] images    =   {R.drawable.taxi,R.drawable.moto,R.drawable.moto};

        String[] tokens = {jsonObj.getString("token") ,jsonObj.getString("token"),jsonObj.getString("token")};

        String[]  texts   =   {jsonObj.getString("begin") ,jsonObj.getString("begin"),jsonObj.getString("begin")};

        for (int i=0; i< texts.length ; i++){
            arrayList.add(new ItemRow(images[i],texts[i], tokens[i]));
        }
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return arrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        LayoutInflater layoutInflater   =    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View    item    =   layoutInflater.inflate(R.layout.customitemlist,viewGroup,false);
        TextView text = (TextView) item.findViewById(R.id.textViewItem);
        ImageView   image   =   (ImageView) item.findViewById(R.id.imageItem);
        ItemRow itemRow =   arrayList.get(i);
        text.setText(itemRow.text);
        image.setImageResource(itemRow.image);
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("getView", "==================Click========>"+  (arrayList.get(i)).text );
                
            }
        });

        return item;
    }
}