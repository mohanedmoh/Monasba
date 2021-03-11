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

import com.savvy.monasbat.Model.grace_result;
import com.savvy.monasbat.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;


public class grace_result_adapter extends ArrayAdapter<grace_result> implements View.OnClickListener {
    private Context mContext;
    private int lastPosition = -1;
    private grace_result_adapter.ViewHolder viewHolder;


    public grace_result_adapter(ArrayList<grace_result> data, Context context) {
        super(context, R.layout.grace_result, data);
        this.mContext = context;

    }

    @Override
    public void onClick(View v) {

    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        grace_result dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        // view lookup cache stored in tag

        final View result;

        if (convertView == null) {


            viewHolder = new grace_result_adapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.grace_result, parent, false);

            viewHolder.name = convertView.findViewById(R.id.org_name);
            viewHolder.phone = convertView.findViewById(R.id.org_phone);
            viewHolder.address = convertView.findViewById(R.id.org_address);
            viewHolder.image = convertView.findViewById(R.id.org_image);

            assert dataModel != null;
            viewHolder.name.setText(dataModel.getName());
            viewHolder.phone.setText(dataModel.getPhone());
            viewHolder.address.setText(dataModel.getPhone());
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
        TextView phone;
        ImageView image;

    }

}
