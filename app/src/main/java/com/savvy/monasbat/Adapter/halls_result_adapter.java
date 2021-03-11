package com.savvy.monasbat.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.savvy.monasbat.Model.halls_result;
import com.savvy.monasbat.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;


public class halls_result_adapter extends ArrayAdapter<halls_result> implements View.OnClickListener {
    private Context mContext;
    private int lastPosition = -1;
    private halls_result_adapter.ViewHolder viewHolder;


    public halls_result_adapter(ArrayList<halls_result> data, Context context) {
        super(context, R.layout.hall_result, data);
        this.mContext = context;

    }

    @Override
    public void onClick(View v) {

    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        halls_result dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        // view lookup cache stored in tag

        final View result;

        if (convertView == null) {


            viewHolder = new halls_result_adapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.hall_result, parent, false);

            viewHolder.name = convertView.findViewById(R.id.hall_name);
            viewHolder.date = convertView.findViewById(R.id.hall_date);
            viewHolder.address = convertView.findViewById(R.id.hall_address);
            viewHolder.image = convertView.findViewById(R.id.hall_image);

            assert dataModel != null;
            viewHolder.name.setText(dataModel.getName());
            viewHolder.date.setText(dataModel.getDate());
            viewHolder.address.setText(dataModel.getAddress());
            //  viewHolder.date.setText(dataModel.getDate());

            result = convertView;


            System.out.println("Position=" + position);
            convertView.setTag("companion" + position);


        } else {
            // viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.top_from_down : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        assert dataModel != null;
          /*  viewHolder.txtLocation.setText(dataModel.getArea());
            viewHolder.txtPrice.setText(dataModel.getPrice());
            viewHolder.txtType.setText(dataModel.getType());
          */
        // new LoadImageTask(this).execute(dataModel.getImage());
        return convertView;
    }

    private static class ViewHolder {
        TextView name;
        TextView address;
        TextView date;
        ImageView image;

    }

}
