package com.savvy.monasbat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
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
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.savvy.monasbat.Model.hall_meal;
import com.savvy.monasbat.Model.hall_package;
import com.savvy.monasbat.Model.hall_reservation_model;
import com.savvy.monasbat.Model.halls_result;

import org.json.JSONArray;
import org.json.JSONException;
import org.lucasr.twowayview.TwoWayView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import pl.polak.clicknumberpicker.ClickNumberPickerView;

public class hall_profile extends AppCompatActivity implements OnMapReadyCallback {
    View layout;
    private PopupWindow mPopupWindow;
    int package_id, meal_id, mealsNum, packagesNum = 0;
    protected GoogleMap mMap;
    String meal_name, package_name, meal_price, package_price = "";
    String[] meal_items;
    String[] package_details;
    View meal_list_include, package_list_include, map_include;
    halls_result hall;
    hall_reservation_model hallReservationModel;
    RecyclerView recyclerView;
    hall_profile.package_adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hall_profile);
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void init() throws JSONException {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        hallReservationModel = (hall_reservation_model) (getIntent().getExtras().getBundle("reservation").getSerializable("reservation"));
        layout = findViewById(R.id.layout);
        meal_list_include = findViewById(R.id.meal_include);
        package_list_include = findViewById(R.id.package_include);
        map_include = findViewById(R.id.map_include);
        Intent intent = getIntent();
        hall = ((halls_result) intent.getExtras().getBundle("hall").getSerializable("hall"));
        setImages(hall.getImages());
        setHallDetails(hall.getDetails());
        setPackages(intent.getExtras().getString("packages"));
        setMeals(intent.getExtras().getString("meals"));

        if (Integer.parseInt(hall.getFree()) == 0) {
            hideReserve();
        }
        findViewById(R.id.reserve).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reserve();
            }
        });
        map_include.findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareLocation(hall.getLatitude(), hall.getLongitude());
            }
        });
        map_include.findViewById(R.id.map).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLocation(hall.getLatitude(), hall.getLongitude());
            }
        });
        setPrice();

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sydney;
        if (hall.getLatitude() != null) {
            putMarker(hall.getName(), Double.valueOf(hall.getLatitude()), Double.valueOf(hall.getLongitude()));

            // Add a marker in Sydney and move the camera
            sydney = new LatLng(Double.valueOf(hall.getLatitude()), Double.valueOf(hall.getLongitude()));
            //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 10));
        }
    }
    private void putMarker(String name, double latit, double longit) {
        LatLng sydney = new LatLng(latit, longit);
        int height = 130;
        int width = 130;
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.mappin);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);

        mMap.addMarker(new MarkerOptions().position(sydney)
                .title(name).icon(smallMarkerIcon));
    }

    @SuppressLint("SetTextI18n")
    private void setPrice() {
        int type = Integer.parseInt(hallReservationModel.getReservation_type_id());
        if (type == 1) {
            ((TextView) findViewById(R.id.price)).setText(hall.getPrice_day() + " " + getResources().getString(R.string.sdg));
        } else {
            ((TextView) findViewById(R.id.price)).setText(hall.getPrice_night() + " " + getResources().getString(R.string.sdg));
        }
    }

    private static ArrayList<View> getViewsByTag(ViewGroup root, String tag) {
        ArrayList<View> views = new ArrayList<View>();
        final int childCount = root.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = root.getChildAt(i);
            if (child instanceof ViewGroup) {
                views.addAll(getViewsByTag((ViewGroup) child, tag));
            }

            final Object tagObj = child.getTag();
            if (tagObj != null && tagObj.equals(tag)) {
                views.add(child);
            }

        }
        return views;
    }

    private void setImages(String[] images) {
        ArrayList<SlideModel> imageList = new ArrayList<>();
        if (images != null) {
            System.out.println("inside not null");
            for (int x = 0; x < images.length; x++) {
                imageList.add(new SlideModel(getResources().getString(R.string.images_url) + "/" + images[x]));
            }
            ((ImageSlider) findViewById(R.id.image_slider)).setImageList(imageList, true);
        } else {
            for (int x = 0; x < 2; x++) {
                imageList.add(new SlideModel(R.drawable.logo, String.valueOf(x + 1)));
            }
            ((ImageSlider) findViewById(R.id.image_slider)).setImageList(imageList, true);
        }
    }

    private void openDetailsPopup(final String[] detailsM, final String name, final View cardView, final String id, int type, final String price) {
        boolean selected = false;
        // Initialize a new instance of LayoutInflater service
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        // Inflate the custom layout/view
        View customView = inflater.inflate(R.layout.package_popup, null);

                /*
                    public PopupWindow (View contentView, int width, int height)
                        Create a new non focusable popup window which can display the contentView.
                        The dimension of the window must be passed to this constructor.

                        The popup does not provide any background. This should be handled by
                        the content view.

                    Parameters
                        contentView : the popup's content
                        width : the popup's width
                        height : the popup's height
                */
        // Initialize a new instance of popup window
        mPopupWindow = new PopupWindow(
                customView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );

        // Set an elevation value for popup window
        // Call requires API level 21
        if (Build.VERSION.SDK_INT >= 21) {
            mPopupWindow.setElevation(5.0f);
        }

        // Get a reference for the custom view close button
        ((TextView) customView.findViewById(R.id.name)).setText(name);
        ImageView closeButton = customView.findViewById(R.id.close);
        final Button select = customView.findViewById(R.id.select);
        // Set a click listener for the popup window close button
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Dismiss the popup window
                mPopupWindow.dismiss();
            }
        });
        if ((type == 1 && Integer.parseInt(id) == meal_id) || (type == 2 && Integer.parseInt(id) == package_id)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    select.setText(getResources().getString(R.string.unselect));
                }
            });
            selected = true;
        }
        final int finalType = type;
        final boolean finalSelected = selected;
        if (finalType == 1) {
            select.setVisibility(View.INVISIBLE);
        }
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    unselect(finalType);
                } catch (Exception e) {
                }
                if (finalType == 1 && !finalSelected) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            cardView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        }
                    });
                    meal_name = name;
                    meal_items = detailsM;
                    meal_id = Integer.parseInt(id);
                    meal_price = price;
                } else if (finalType == 2 && !finalSelected) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            cardView.findViewById(R.id.card).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                            ((TextView) package_list_include.findViewById(R.id.package_selected)).setText(getResources().getString(R.string.package_selected) + " " + name);

                        }
                    });
                    package_details = detailsM;
                    package_name = name;
                    package_price = price;
                    package_id = Integer.parseInt(id);
                }
                mPopupWindow.dismiss();
            }
        });

        String[] details = detailsM;
        final ListView listView = customView.findViewById(R.id.list);
        if (0 == details.length) {
        } else {
            final package_details_adapter adapter = new package_details_adapter(details, getApplicationContext());
            runOnUiThread(new Runnable() {
                public void run() {
                    listView.setAdapter(adapter);
                }
            });
        }

                /*
                    public void showAtLocation (View parent, int gravity, int x, int y)
                        Display the content view in a popup window at the specified location. If the
                        popup window cannot fit on screen, it will be clipped.
                        Learn WindowManager.LayoutParams for more information on how gravity and the x
                        and y parameters are related. Specifying a gravity of NO_GRAVITY is similar
                        to specifying Gravity.LEFT | Gravity.TOP.

                    Parameters
                        parent : a parent view to get the getWindowToken() token from
                        gravity : the gravity which controls the placement of the popup window
                        x : the popup's x location offset
                        y : the popup's y location offset
                */
        // Finally, show the popup window at the center location of root relative layout
        mPopupWindow.showAtLocation(layout, Gravity.CENTER, 0, 0);
    }
    private void setMeals(final String meals) throws JSONException {
        JSONArray jsonArray = new JSONArray(meals);
        mealsNum = jsonArray.length();
        System.out.println("11");
        final TwoWayView listView = meal_list_include.findViewById(R.id.meals);
        System.out.println("22");
        final ArrayList<hall_meal> dataModels = new ArrayList<>();
        if (jsonArray.length() == 0) {
        } else {
            findViewById(R.id.meal_include).setVisibility(View.VISIBLE);
            // findViewById(R.id.meal_number).setVisibility(View.VISIBLE);

            // System.out.println("33"+y);
            for (int x = 0; x < jsonArray.length(); x++) {
                hall_meal dataModel = new hall_meal();
                dataModel.setName(jsonArray.getJSONObject(x).getString("name"));
                dataModel.setId(jsonArray.getJSONObject(x).getString("id"));
                dataModel.setPrice(jsonArray.getJSONObject(x).getString("price"));
                String[] items = new String[jsonArray.getJSONObject(x).getJSONArray("item").length()];
                for (int i = 0; i < items.length; i++) {
                    items[i] = jsonArray.getJSONObject(x).getJSONArray("item").getJSONObject(i).getString("name");
                }
                dataModel.setItems(items);
                dataModels.add(dataModel);
            }
            final hall_profile.meals_adapter adapter = new meals_adapter(dataModels, getApplicationContext());
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    hall_meal dataModel = dataModels.get(position);
                    openDetailsPopup(dataModel.getItems(), dataModel.getName(), view, dataModel.getId(), 1, dataModel.getPrice());
                }
            });

            runOnUiThread(new Runnable() {
                public void run() {
                    listView.setAdapter(adapter);
                }
            });
        }
    }
    private void setHallDetails(String details) {
        ((TextView) findViewById(R.id.hall_details)).setText(details);
    }
    private void shareLocation(String latitude, String longitude) {
        String uri = "http://maps.google.com/maps?q=loc:" + latitude + "," + longitude;
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, uri);
        startActivity(Intent.createChooser(sharingIntent, "Share in..."));
    }
    private void openLocation(String latitude, String longitude) {
        Uri location = Uri.parse("geo:" + latitude + "," + longitude + "?z=14");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);

    }
    private void reserve() {
        hall_reservation_model hallReservationModel = (hall_reservation_model) (getIntent().getExtras().getBundle("reservation").getSerializable("reservation"));
        hallReservationModel.setNum_of_meals(String.valueOf(((ClickNumberPickerView) meal_list_include.findViewById(R.id.num_of_meals)).getValue()));
        hallReservationModel.setHall_id(hall.getId());
        hallReservationModel.setHall_name(hall.getName());
        hallReservationModel.setAddress(hall.getAddress());
        hallReservationModel.setMeal_id(String.valueOf(meal_id));
        hallReservationModel.setPackage_id(String.valueOf(package_id));
        hallReservationModel.setMeal_name(meal_name);
        hallReservationModel.setMeal_details(meal_items);
        hallReservationModel.setPackage_name(package_name);
        hallReservationModel.setPackage_details(package_details);
        hallReservationModel.setPackage_price(package_price);
        hallReservationModel.setMeal_price(meal_price == null || meal_price.isEmpty() ? "0" : meal_price);
        hallReservationModel.setHall_price(Integer.parseInt(hallReservationModel.getReservation_type_id()) == 1 ? hall.getPrice_day() : hall.getPrice_night());
        hallReservationModel.setPolicy(hall.getPolicy());
        //change after insert meals
        if (checkMeals(Integer.parseInt(hallReservationModel.getInclude_meal()), hallReservationModel.getNum_of_meals()) || true) {
            Bundle b = new Bundle();
            b.putSerializable("reservation", hallReservationModel);
            Intent intent = new Intent(getApplicationContext(), hall_reservation.class);
            intent.putExtra("reservation", b);
            //change after insert meals
             intent.putExtra("meals", getIntent().getExtras().getString("meals"));
            if (getIntent().getExtras().containsKey("test")) {
                intent.putExtra("test", getIntent().getExtras().getBoolean("test"));
            }

            startActivityForResult(intent, 111);
        }
    }
    private boolean checkMeals(int meal_include, String numOfMeals) {
        // i made it false because no meals now
        if (meal_include == 1 && false) {
            if (meal_id == 0) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_meals), Toast.LENGTH_LONG).show();
                    }
                });
                return false;
            } else if (Double.parseDouble(numOfMeals) == 0 && false) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_meals_num), Toast.LENGTH_LONG).show();
                    }
                });
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111 && resultCode == 1) {
            setResult(1);
            finish();
        }
    }
    public static class package_details_adapter extends ArrayAdapter<String> implements View.OnClickListener {
        private final Context mContext;
        private int lastPosition = -1;
        private hall_profile.package_details_adapter.ViewHolder viewHolder;


        public package_details_adapter(String[] data, Context context) {
            super(context, R.layout.package_popup_details, data);
            this.mContext = context;

        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            // Get the data item for this position
            final String dataModel = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            // view lookup cache stored in tag

            final View result;

            if (convertView == null) {


                viewHolder = new hall_profile.package_details_adapter.ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.package_popup_details, parent, false);


                viewHolder.detail = convertView.findViewById(R.id.detail);

                //  viewHolder.more = convertView.findViewById(R.id.more);

                assert dataModel != null;
                //   viewHolder.datetime.setText(dataModel.getDate());
                // viewHolder.name.setText(dataModel.getName());
                //viewHolder.gender.setText(dataModel.getGender_title());




               /* viewHolder.more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openDetailsLayout(dataModel);
                    }
                });*/

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
            viewHolder.detail.setText(dataModel);
            // new LoadImageTask(this).execute(dataModel.getImage());
            return convertView;
        }

        @Override
        public void onClick(View v) {

        }

        private class ViewHolder {
            TextView detail;


        }

    }

    private void setPackages(String packages) throws JSONException {
        JSONArray jsonArray = new JSONArray(packages);
        packagesNum = jsonArray.length();
        recyclerView = package_list_include.findViewById(R.id.packages);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        final ArrayList<hall_package> dataModels = new ArrayList<>();
        if (jsonArray.length() == 0) {
        } else {
            package_list_include.setVisibility(View.VISIBLE);
            for (int x = 0; x < jsonArray.length(); x++) {
                hall_package dataModel = new hall_package();
                dataModel.setName(jsonArray.getJSONObject(x).getString("name"));
                dataModel.setId(jsonArray.getJSONObject(x).getString("id"));
                dataModel.setPrice(jsonArray.getJSONObject(x).getString("price"));
                String[] details = new String[jsonArray.getJSONObject(x).getJSONArray("items").length()];
                for (int i = 0; i < details.length; i++) {
                    details[i] = jsonArray.getJSONObject(x).getJSONArray("items").getJSONObject(i).getString("name");
                }
                dataModel.setDetails(details);
                dataModels.add(dataModel);
            }

            adapter = new package_adapter(dataModels, getApplicationContext());

          /*  listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    hall_package dataModel = dataModels.get(position);

                    openDetailsPopup(dataModel.getDetails(), dataModel.getName(), view, dataModel.getId(), 2, dataModel.getPrice());
                }
            });*/
            runOnUiThread(new Runnable() {
                public void run() {
                    recyclerView.setAdapter(adapter);
                }
            });
        }
    }

    public void unselect(int type) {
        int size = type == 1 ? mealsNum : packagesNum;
        if (type == 1) {
            meal_id = 0;
            meal_price = "0";
        } else {
            package_price = "0";
            package_id = 0;
        }
        System.out.println("size=" + size);
        // ArrayList<View> views=getViewsByTag(package_list_include,"package");
        ViewGroup viewGroup = ((RecyclerView) (package_list_include.findViewById(R.id.packages)));
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            child.findViewById(R.id.card).setBackgroundColor(getResources().getColor(R.color.white));
        }

        ((TextView) package_list_include.findViewById(R.id.package_selected)).setText("");
       /* for (int x = 0; x < size; x++) {
            if (type == 1) {
                meal_list_include.findViewWithTag("meal" + x).setBackgroundColor(getResources().getColor(R.color.white));
            } else {
                System.out.println("x=" + x);
                package_list_include.findViewWithTag("package").setBackgroundColor(getResources().getColor(R.color.white));
            }
        }*/
    }
    public static class meals_adapter extends ArrayAdapter<hall_meal> implements View.OnClickListener {
        private final Context mContext;
        private int lastPosition = -1;
        private hall_profile.meals_adapter.ViewHolder viewHolder;


        public meals_adapter(ArrayList<hall_meal> data, Context context) {
            super(context, R.layout.hall_meal, data);
            this.mContext = context;

        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            // Get the data item for this position
            final hall_meal dataModel = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            // view lookup cache stored in tag

            final View result;

            if (convertView == null) {


                viewHolder = new hall_profile.meals_adapter.ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.hall_meal, parent, false);


                viewHolder.name = convertView.findViewById(R.id.meal_name);
                viewHolder.price = convertView.findViewById(R.id.meal_price);

                //  viewHolder.more = convertView.findViewById(R.id.more);

                assert dataModel != null;
                //   viewHolder.datetime.setText(dataModel.getDate());
                // viewHolder.name.setText(dataModel.getName());
                //viewHolder.gender.setText(dataModel.getGender_title());




               /* viewHolder.more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openDetailsLayout(dataModel);
                    }
                });*/

                result = convertView;


                System.out.println("Position=" + position);
                convertView.findViewById(R.id.card).setTag("meal" + position);


            } else {
                // viewHolder = (ViewHolder) convertView.getTag();
                result = convertView;
            }

            Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.top_from_down : R.anim.down_from_top);
            result.startAnimation(animation);
            lastPosition = position;

            assert dataModel != null;
            viewHolder.name.setText(dataModel.getName());
            viewHolder.price.setText(dataModel.getPrice());
            //   viewHolder.image.setText(dataModel.getType());
            // new LoadImageTask(this).execute(dataModel.getImage());
            return convertView;
        }

        @Override
        public void onClick(View v) {

        }

        private class ViewHolder {
            TextView name, price;
            ImageView image;
        }

    }

    public class package_adapter extends RecyclerView.Adapter<package_adapter.UserAdapterVH> implements View.OnClickListener {
        private final List<hall_package> hall_package;
        private Context context;
        // private SelectedUser selectedUser;

        public package_adapter(List<hall_package> hall_package, Context context) {
            this.hall_package = hall_package;
            //   this.selectedUser = selectedUser;

        }

        /*  @NonNull
          @Override
          public View getView(int position, View convertView, @NonNull ViewGroup parent) {
              final hall_package dataModel = getItem(position);

              final View result;

              if (convertView == null) {


                  viewHolder = new hall_profile.package_adapter.ViewHolder();
                  LayoutInflater inflater = LayoutInflater.from(getContext());
                  convertView = inflater.inflate(R.layout.hall_package, parent, false);


                  viewHolder.name = convertView.findViewById(R.id.name);
                  viewHolder.card = convertView.findViewById(R.id.card);
                  assert dataModel != null;


                  result = convertView;


                  System.out.println("Position=" + position);
                  //convertView.findViewById(R.id.card).setTag("package" + position);


              } else {
                  result = convertView;
              }

              Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.top_from_down : R.anim.down_from_top);
              result.startAnimation(animation);
              lastPosition = position;

              assert dataModel != null;
              viewHolder.card.setTag("package" + position);
              System.out.println(viewHolder.card.getTag() + "inside adapter");
              viewHolder.name.setText(dataModel.getName());
              return convertView;
          }
  */
        @NonNull
        @Override
        public package_adapter.UserAdapterVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            context = parent.getContext();

            return new UserAdapterVH(LayoutInflater.from(context).inflate(R.layout.hall_package, null));
        }

        @Override
        public void onBindViewHolder(@NonNull package_adapter.UserAdapterVH holder, int position) {

            hall_package hall_package = this.hall_package.get(position);

            String name = hall_package.getName();

            holder.name.setText(name);

        }

        @Override
        public int getItemCount() {
            return hall_package.size();
        }

        @Override
        public void onClick(View v) {
        }

        public class UserAdapterVH extends RecyclerView.ViewHolder {

            TextView name;
            View card;

            public UserAdapterVH(@NonNull View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.name);
                card = itemView.findViewById(R.id.card);
                card.setTag("package");


                itemView.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onClick(View view) {
                        com.savvy.monasbat.Model.hall_package dataModel = hall_package.get(getAdapterPosition());
                        openDetailsPopup(dataModel.getDetails(), dataModel.getName(), view, dataModel.getId(), 2, dataModel.getPrice());
                        // ((TextView)package_list_include.findViewById(R.id.package_selected)).setText(getResources().getString(R.string.package_selected)+" "+dataModel.getName());
                        // selectedUser.selectedUser(userModelList.get(getAdapterPosition()));
                    }
                });


            }
        }
    }
    private void hideReserve() {
        findViewById(R.id.reserve).setVisibility(View.GONE);
    }
}
