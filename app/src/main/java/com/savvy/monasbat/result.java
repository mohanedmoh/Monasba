package com.savvy.monasbat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.savvy.monasbat.Model.grace_result;
import com.savvy.monasbat.Model.hall_reservation_model;
import com.savvy.monasbat.Model.halls_result;
import com.savvy.monasbat.Model.limo_result;
import com.savvy.monasbat.Network.Iokihttp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class result extends AppCompatActivity {
    private static final int MAKE_CALL_PERMISSION_REQUEST_CODE = 1;
    Iokihttp iokihttp;
    SharedPreferences shared;
    DisplayImageOptions options;
    boolean test = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        try {
            init();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void init() throws JSONException {
        iokihttp = new Iokihttp();
        shared = this.getSharedPreferences("com.savvy.monasbat", Context.MODE_PRIVATE);
        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.logo)
                .showImageOnFail(R.drawable.logo)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new RoundedBitmapDisplayer(20))
                .build();
        initAdapter();
        if (getIntent().getExtras().containsKey("test")) {
            test = getIntent().getExtras().getBoolean("test");
        }
    }

    @SuppressLint("NonConstantResourceId")
    private void initAdapter() throws JSONException {
        Intent intent = getIntent();
        switch (intent.getExtras().getInt("id")) {
            case R.id.nemasave: {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        findViewById(R.id.grace_saving_desc).setVisibility(View.VISIBLE);
                    }
                });
                setGraceList(intent.getExtras().getString("result"));
            }
            break;
            case R.id.halls: {
                setHallList(intent.getExtras().getString("result"));
            }
            break;
            case R.id.zafa: {

                setLimoList(intent.getExtras().getString("result"));
            }
            break;

            default: {

            }
            break;
        }
    }
    private void setGraceList(String result) throws JSONException {
        JSONArray jsonArray = new JSONArray(result);
        System.out.println("11");
        final ListView listView = findViewById(R.id.result_list);
        System.out.println("22");
        final ArrayList<grace_result> dataModels = new ArrayList<>();
        if (jsonArray.length() == 0) {
        } else {
            // System.out.println("33"+y);
            for (int x = 0; x < jsonArray.length(); x++) {
                grace_result dataModel = new grace_result();
                dataModel.setName(jsonArray.getJSONObject(x).getString("name"));
                dataModel.setAddress(jsonArray.getJSONObject(x).getString("address"));
                dataModel.setPhone(jsonArray.getJSONObject(x).getString("phone"));
                //  dataModel.setImage_url("storage/" + jsonArray.getJSONObject(x).getString("img_url").split("/", 2)[1]);
                dataModel.setImage_url(jsonArray.getJSONObject(x).getString("img_url"));

                dataModels.add(dataModel);
            }

            final grace_adapter adapter = new grace_adapter(dataModels, getApplicationContext());

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    grace_result dataModel = dataModels.get(position);
                    call(dataModel.getPhone());
                }
            });
            runOnUiThread(new Runnable() {
                public void run() {
                    listView.setAdapter(adapter);
                }
            });
        }
    }
    private void setLimoList(String result) throws JSONException {
        JSONArray jsonArray = new JSONArray(result);
        final ListView listView = findViewById(R.id.result_list);
        final ArrayList<limo_result> dataModels = new ArrayList<>();
        if (jsonArray.length() == 0) {
        } else {
            for (int x = 0; x < jsonArray.length(); x++) {
                JSONArray cars = jsonArray.getJSONObject(x).getJSONArray("cars");
                JSONArray policies = new JSONArray(jsonArray.getJSONObject(x).getString("policy"));

                for (int z = 0; z < cars.length(); z++) {
                    limo_result dataModel = new limo_result();

                    dataModel.setId(cars.getJSONObject(z).getString("id"));
                    dataModel.setName(cars.getJSONObject(z).getString("type") + " " + cars.getJSONObject(z).getString("model"));
                    dataModel.setPrice(cars.getJSONObject(z).getString("price"));
                    dataModel.setDetails(cars.getJSONObject(z).getString("details"));
                    System.out.println("namecar=" + dataModel.getName());
                    JSONArray images = new JSONArray(cars.getJSONObject(z).getString("images"));
                    String[] imagesS = new String[images.length()];
                    System.out.println("sssss" + images.toString());
                    for (int y = 0; y < images.length(); y++) {
                        System.out.println("sssss" + images.getJSONObject(y).getString("url"));

                        imagesS[y] = images.getJSONObject(y).getString("url");
                    }
                    dataModel.setImages(imagesS);
                    String[] policiesS = new String[policies.length()];
                    for (int y = 0; y < policies.length(); y++) {
                        policiesS[y] = policies.getJSONObject(y).getString("content");
                    }
                    dataModel.setPolicies(policiesS);
                    dataModels.add(dataModel);
                }
            }
           // System.out.println("111" + dataModels.get(0).getName() + dataModels.get(1).getName());
            final limo_adapter adapter = new limo_adapter(dataModels, getApplicationContext());

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    limo_result dataModel = dataModels.get(position);
                    try {
                        openLimoReservation(dataModel);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            runOnUiThread(new Runnable() {
                public void run() {
                    listView.setAdapter(adapter);
                }
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void call(String phone) {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, MAKE_CALL_PERMISSION_REQUEST_CODE);
        System.out.println("call");
        if (checkPermission(Manifest.permission.CALL_PHONE)) {
            String dial = "tel:" + phone;
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
        } else {
            Toast.makeText(result.this, "Permission Call Phone denied", Toast.LENGTH_SHORT).show();
        }
    }
    private boolean checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }
    private void setHallList(String result) throws JSONException {
        JSONArray jsonArray = new JSONArray(result);
        System.out.println("11");
        final ListView listView = findViewById(R.id.result_list);
        System.out.println("22");
        final ArrayList<halls_result> dataModels = new ArrayList<>();
        if (jsonArray.length() == 0) {
            showNoData();
        } else {
            for (int x = 0; x < jsonArray.length(); x++) {
                halls_result dataModel = new halls_result();
                dataModel.setId(jsonArray.getJSONObject(x).getString("id"));
                dataModel.setName(jsonArray.getJSONObject(x).getString("name"));
                dataModel.setAddress(jsonArray.getJSONObject(x).getString("address"));
                dataModel.setDetails(jsonArray.getJSONObject(x).getString("text"));
                dataModel.setLatitude(jsonArray.getJSONObject(x).getString("lat"));
                dataModel.setLongitude(jsonArray.getJSONObject(x).getString("lng"));
                dataModel.setFree(jsonArray.getJSONObject(x).getString("free"));
                dataModel.setPolicy(jsonArray.getJSONObject(x).getString("policy"));
                System.out.println("PM=="+jsonArray.getJSONObject(x));
                JSONObject PM = jsonArray.getJSONObject(x).getJSONObject("PM");
                dataModel.setPackages(PM.getJSONArray("Packages").toString());
                dataModel.setMeals(PM.getJSONArray("Meals").toString());
                if (jsonArray.getJSONObject(x).getJSONArray("prices").length() == 2) {
                    dataModel.setPrice_day(jsonArray.getJSONObject(x).getJSONArray("prices").getJSONObject(0).getString("price"));
                    dataModel.setPrice_night(jsonArray.getJSONObject(x).getJSONArray("prices").getJSONObject(1).getString("price"));
                }
                JSONArray images = jsonArray.getJSONObject(x).getJSONArray("images");
                String[] imageArray = new String[images.length()];
                for (int y = 0; y < imageArray.length; y++) {
                    imageArray[y] = images.getJSONObject(y).getString("url");
                }
                dataModel.setImages(imageArray);
                dataModels.add(dataModel);
            }
            final halls_adapter adapter = new halls_adapter(dataModels, getApplicationContext());
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    halls_result dataModel = dataModels.get(position);
                    try {
                        openHallsDetails(dataModel.getPackages(), dataModel.getMeals(), dataModel);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            runOnUiThread(new Runnable() {
                public void run() {
                    listView.setAdapter(adapter);
                }
            });
        }
    }

    private void openHallsDetails(String packages, String meals, halls_result halls_result) {
        Bundle b = new Bundle();
        b.putSerializable("hall", halls_result);
        hall_reservation_model hallReservationModel = (hall_reservation_model) getIntent().getExtras().getBundle("reservation").getSerializable("reservation");
        hallReservationModel.setHall_id(halls_result.getId());
        System.out.println("FFFFFFFFFFFFFFFFF" + hallReservationModel.getDate());
        b.putSerializable("reservation", hallReservationModel);
        Intent intent = new Intent(getApplicationContext(), hall_profile.class);
        intent.putExtra("hall", b);
        intent.putExtra("packages", packages);
        intent.putExtra("meals", meals);
        intent.putExtra("reservation", b);
        intent.putExtra("test", test);

        startActivityForResult(intent, 123);
        hideLoading();

    }

    private void openLimoReservation(limo_result result) {
        Bundle b = new Bundle();
        b.putSerializable("limo", result);
        Intent intent = new Intent(getApplicationContext(), Limo.class);
        intent.putExtra("limo", b);
        startActivityForResult(intent, 123);
        hideLoading();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == 1) {
            setResult(1);
            finish();
        } else if (requestCode == 123 && resultCode == 111) {
            // setResult(1);
            finish();
        }
    }

    private void showNoData() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.no_data).setVisibility(View.VISIBLE);
            }
        });
    }
    public void showLoading() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.layout).setVisibility(View.GONE);
                findViewById(R.id.loading).setVisibility(View.VISIBLE);

            }

        });


    }
    public void hideLoading() {

        final View main = findViewById(R.id.layout);
        final View loading = findViewById(R.id.loading);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                main.setVisibility(View.VISIBLE);
                loading.setVisibility(View.GONE);
            }
        });
    }
    public class grace_adapter extends ArrayAdapter<grace_result> implements View.OnClickListener {
        private final Context mContext;
        private int lastPosition = -1;
        private ViewHolder viewHolder;


        public grace_adapter(ArrayList<grace_result> data, Context context) {
            super(context, R.layout.grace_result, data);
            this.mContext = context;

        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            // Get the data item for this position
            final grace_result dataModel = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            // view lookup cache stored in tag

            final View result;

            if (convertView == null) {


                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.grace_result, parent, false);


                viewHolder.name = convertView.findViewById(R.id.org_name);
                viewHolder.address = convertView.findViewById(R.id.org_address);
                viewHolder.image = convertView.findViewById(R.id.org_image);
                viewHolder.phone = convertView.findViewById(R.id.org_phone);

                assert dataModel != null;

                result = convertView;

                System.out.println("Position=" + position);
                convertView.setTag("companion" + position);


            } else {
                // viewHolder = (ViewHolder) convertView.getTag();
                result = convertView;
            }
            assert dataModel != null;
            viewHolder.address.setText(dataModel.getAddress());
            viewHolder.name.setText(dataModel.getName());
            viewHolder.phone.setText(dataModel.getPhone());
            if (!(dataModel.getImage_url().isEmpty())) {
                ImageLoader imageLoader = ImageLoader.getInstance(); // Get singleton instance
                ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getContext())
                        .writeDebugLogs()
                        .build();
                imageLoader.init(config);
                imageLoader.displayImage(getResources().getString(R.string.images_url) + "/" + dataModel.getImage_url(), viewHolder.image);
            }

            System.out.println(dataModel.getPhone() + "::::::");
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

        @Override
        public void onClick(View v) {

        }

        private class ViewHolder {
            TextView name, phone, address;
            ImageView image;
            Button more;


        }

    }

    public class limo_adapter extends ArrayAdapter<limo_result> implements View.OnClickListener {
        private final Context mContext;
        private int lastPosition = -1;
        private ViewHolder viewHolder;


        public limo_adapter(ArrayList<limo_result> data, Context context) {
            super(context, R.layout.limo_result, data);
            this.mContext = context;

        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            // Get the data item for this position
            final limo_result dataModel = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            // view lookup cache stored in tag

            final View result;

            if (convertView == null) {


                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.limo_result, parent, false);


                viewHolder.name = convertView.findViewById(R.id.car_name);
                viewHolder.details = convertView.findViewById(R.id.car_details);
                viewHolder.price = convertView.findViewById(R.id.car_price);
                viewHolder.image = convertView.findViewById(R.id.car_image);
                viewHolder.reserve = convertView.findViewById(R.id.reserve);

                //  viewHolder.more = convertView.findViewById(R.id.more);

                assert dataModel != null;
                //   viewHolder.datetime.setText(dataModel.getDate());
                // viewHolder.name.setText(dataModel.getName());
                //viewHolder.gender.setText(dataModel.getGender_title());

                viewHolder.reserve.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openLimoReservation(dataModel);
                    }
                });

                result = convertView;


                convertView.setTag("companion" + position);


            } else {
                // viewHolder = (ViewHolder) convertView.getTag();
                result = convertView;
            }
            assert dataModel != null;
            viewHolder.name.setText(dataModel.getName());
            viewHolder.price.setText(dataModel.getName());
            System.out.println("CAR=" + dataModel.getName());
            viewHolder.details.setText(dataModel.getDetails());
            if ((dataModel.getImages().length > 0)) {
                ImageLoader imageLoader = ImageLoader.getInstance(); // Get singleton instance
                ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getContext())
                        .writeDebugLogs()
                        .build();
                imageLoader.init(config);
                System.out.println("IMAGE=" + dataModel.getImages()[0]);
                imageLoader.displayImage(dataModel.getImages()[0], viewHolder.image);
            }

            Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.top_from_down : R.anim.down_from_top);
            result.startAnimation(animation);
            lastPosition = position;

            //  assert dataModel != null;
            return convertView;
        }

        @Override
        public void onClick(View v) {

        }

        private class ViewHolder {
            TextView name, price, details, reserve;
            ImageView image;


        }

    }

    public class halls_adapter extends ArrayAdapter<halls_result> implements View.OnClickListener {
        private final Context mContext;
        private int lastPosition = -1;
        private ViewHolder viewHolder;


        public halls_adapter(ArrayList<halls_result> data, Context context) {
            super(context, R.layout.hall_result, data);
            this.mContext = context;

        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            // Get the data item for this position
            final halls_result dataModel = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            // view lookup cache stored in tag

            final View result;

            if (convertView == null) {


                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.hall_result, parent, false);
                viewHolder.name = convertView.findViewById(R.id.hall_name);
                viewHolder.date = convertView.findViewById(R.id.hall_date);
                viewHolder.image = convertView.findViewById(R.id.hall_image);
                viewHolder.address = convertView.findViewById(R.id.hall_address);
                viewHolder.status = convertView.findViewById(R.id.status);

                assert dataModel != null;
                result = convertView;
                System.out.println("Position=" + position);
                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
                result = convertView;
            }
            assert dataModel != null;

            viewHolder.name.setText(dataModel.getName());
            viewHolder.date.setText(dataModel.getDate());
            viewHolder.address.setText(dataModel.getAddress());
            viewHolder.date.setText(shared.getString("date", ""));
            if (dataModel.getImages().length > 0) {
                ImageLoader imageLoader = ImageLoader.getInstance(); // Get singleton instance
                ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getContext())
                        .writeDebugLogs()
                        .build();
                imageLoader.init(config);
                imageLoader.displayImage(getResources().getString(R.string.images_url) + "/" + dataModel.getImages()[0], viewHolder.image);
            }
            viewHolder.status.setText(Integer.parseInt(dataModel.getFree()) == 0 ? getResources().getString(R.string.reserved) : getResources().getString(R.string.free));

            Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.top_from_down : R.anim.down_from_top);
            result.startAnimation(animation);
            lastPosition = position;

            return convertView;
        }

        @Override
        public void onClick(View v) {
        }

        private class ViewHolder {
            TextView name, date, address, status;
            ImageView image;
        }

    }
}
