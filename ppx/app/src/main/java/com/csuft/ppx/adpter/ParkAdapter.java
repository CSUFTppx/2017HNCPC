package com.csuft.ppx.adpter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.csuft.ppx.R;
import com.csuft.ppx.bean.BeanSearch;

import java.util.List;


/**
 * Created by ling on 2017/5/2.
 */

public class ParkAdapter extends ArrayAdapter<BeanSearch> {
    private int resourceId;
    public ParkAdapter(Context context, int textViewResourceId, List<BeanSearch> object){
        super(context,textViewResourceId,object);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BeanSearch search = getItem(position);
        View view;
        if(convertView ==null)
            view = LayoutInflater.from(getContext()).inflate(resourceId,null);
        else
            view = convertView;
        TextView names = (TextView)view.findViewById(R.id.names);
        TextView place = (TextView)view.findViewById(R.id.place);
        names.setText(search.getNames());
        place.setText(search.getPlace());
        return view;
    }
}
