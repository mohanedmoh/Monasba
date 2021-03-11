package com.savvy.monasbat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kofigyan.stateprogressbar.StateProgressBar;
import com.savvy.monasbat.DB.Bus_types;
import com.savvy.monasbat.DB.Cities;
import com.savvy.monasbat.Model.req_info;
import com.savvy.monasbat.Network.Iokihttp;
import com.savvy.monasbat.payments.payment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RequestManager extends AppCompatActivity {
    public ListView listView;
    public ArrayList<req_info> dataModels;
    public RequestManager.req_status_list_adapter adapter;
    SharedPreferences shared;
    Iokihttp iokihttp;
    Boolean timerPage = false;
    boolean main = true;
    View hall_details_include, seera_details_include, limo_details_include;
    private long timeCountInMilliSeconds;
    private ProgressBar progressBarCircle;
    private TextView textViewTime;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_manager);
        init();
    }

    private void init() {
        shared = getSharedPreferences("com.savvy.monasbat", Context.MODE_PRIVATE);
        // method call to initialize the views
        iokihttp = new Iokihttp();
        initViews();
        System.out.println(shared.getString("user_id", "0"));
        hall_details_include = findViewById(R.id.hall_details_include);
        seera_details_include = findViewById(R.id.seera_details_include);
        limo_details_include = findViewById(R.id.limo_details_include);

        if (!shared.getString("user_id", "0").equals("0")) {
            getList(Integer.parseInt(shared.getString("user_id", "")));
        } else {
            shared.edit().putBoolean("login", false).apply();
            Intent i = new Intent(getApplicationContext(), Login.class);
            startActivity(i);
            finish();
        }


    }

    private void openPaymentMethod(String id, String req_type) {
        Intent intent = new Intent(getApplicationContext(), payment.class);
        intent.putExtra("id", id);
        intent.putExtra("req_type", req_type);
        startActivity(intent);
    }


    private void setTimerValues(Long value) {
        // assigning values after converting to milliseconds
        timeCountInMilliSeconds = value;
    }

    /**
     * method to start count down timer
     */
    private void startCountDownTimer() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                countDownTimer = new CountDownTimer(timeCountInMilliSeconds, 1000) {
                    @Override
                    public void onTick(final long millisUntilFinished) {

                        textViewTime.setText(hmsTimeFormatter(millisUntilFinished));


                        progressBarCircle.setProgress((int) (millisUntilFinished / 1000));

                    }

                    @Override
                    public void onFinish() {

                        textViewTime.setText(hmsTimeFormatter(timeCountInMilliSeconds));
                        // call to initialize the progress bar values
                        setProgressBarValues();
                    }

                }.start();
                countDownTimer.start();
            }
        });

    }

    private void setProgressBarValues() {

        progressBarCircle.setMax((int) timeCountInMilliSeconds / 1000);
        progressBarCircle.setProgress((int) timeCountInMilliSeconds / 1000);
    }

    private String hmsTimeFormatter(long milliSeconds) {

        String hms = String.format("%02d:%02d:%02d:%02d", TimeUnit.MILLISECONDS.toDays(milliSeconds),
                TimeUnit.MILLISECONDS.toHours(milliSeconds) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(milliSeconds)),
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));

        return hms;


    }

    /**
     * method to initialize the views
     */
    private void initViews() {
        progressBarCircle = findViewById(R.id.progressBarCircle);
        textViewTime = findViewById(R.id.textViewTime);
    }

    public void setList(JSONArray seera, JSONArray reservations, JSONArray limos) throws JSONException {
        System.out.println("11");
        listView = findViewById(R.id.reqTypeList);
        System.out.println("22");
        dataModels = new ArrayList<>();
        if (seera.length() != 0 || reservations.length() != 0 || limos.length() != 0) {
            if (seera.length() != 0) {
                for (int x = seera.length() - 1; x >= 0; x--) {
                    req_info dataModel = new req_info();
                    dataModel.setReq_type(9);
                    dataModel.setId(((JSONObject) seera.get(x)).getString("id"));
                    dataModel.setDate(((JSONObject) seera.get(x)).getString("date"));
                    dataModel.setPickup_address(((JSONObject) seera.get(x)).getString("pickup_address"));
                    dataModel.setDropoff_address(((JSONObject) seera.get(x)).getString("dropoff_address"));
                    dataModel.setPhone1(((JSONObject) seera.get(x)).getString("main_phone"));
                    dataModel.setPhone2(((JSONObject) seera.get(x)).getString("sub_phone"));
                    dataModel.setBus_type_id(((JSONObject) seera.get(x)).getString("bus_type_id"));
                    dataModel.setBus_type(getBusType(dataModel.getBus_type_id()));
                    dataModel.setNumber_of_buses(((JSONObject) seera.get(x)).getString("num_of_buses"));
                    if (((JSONObject) seera.get(x)).has("reservation_type")) {
                        dataModel.setReservation_type_id(((JSONObject) seera.get(x)).getString("reservation_type"));
                        dataModel.setReservation_type(Integer.parseInt(dataModel.getReservation_type_id()) == 1 ? getResources().getString(R.string.day) : getResources().getString(R.string.night));
                    }
                    dataModel.setPrice(((JSONObject) seera.get(x)).getString("price"));
                    dataModel.setPaid(((JSONObject) seera.get(x)).getString("paid"));
                    dataModel.setApproved(((JSONObject) seera.get(x)).getString("approved"));

                    System.out.println("ID=" + ((JSONObject) seera.get(x)).getString("price"));

                    dataModels.add(dataModel);
                }
            }
            if (reservations.length() != 0) {
                System.out.println("SS" + reservations.length());
                for (int x = reservations.length() - 1; x >= 0; x--) {
                    req_info dataModel = new req_info();
                    dataModel.setReq_type(1);
                    dataModel.setId(((JSONObject) reservations.get(x)).getString("id"));
                    dataModel.setHall_name((((JSONObject) reservations.get(x)).getJSONObject("hall").getString("name")));
                    dataModel.setNum_of_people(reservations.getJSONObject(x).getJSONObject("hall").getString("people_no"));
                    dataModel.setCity_name(reservations.getJSONObject(x).getJSONObject("city").getString("name"));
                    dataModel.setName1(((JSONObject) reservations.get(x)).getString("name"));
                    dataModel.setName2(((JSONObject) reservations.get(x)).getString("payment_type_id"));
                    dataModel.setPhone1(((JSONObject) reservations.get(x)).getString("phone1"));
                    dataModel.setPhone2(((JSONObject) reservations.get(x)).getString("phone2"));
                    dataModel.setAddress(((JSONObject) reservations.get(x)).getString("address"));
                    dataModel.setExtra_hours(((JSONObject) reservations.get(x)).getString("extra_hours"));
                    dataModel.setReservation_type_id(((JSONObject) reservations.get(x)).getString("reservation_type_id"));
                    dataModel.setReservation_type(Integer.parseInt(dataModel.getReservation_type_id()) == 1 ? getResources().getString(R.string.day) : getResources().getString(R.string.night));
                    dataModel.setPayment_type_id(((JSONObject) reservations.get(x)).getString("payment_type_id"));
                    dataModel.setPrice(((JSONObject) reservations.get(x)).getString("price"));
                    dataModel.setPaid(((JSONObject) reservations.get(x)).getString("paid"));
                    dataModel.setApproved("1");
                    dataModel.setDate(((JSONObject) reservations.get(x)).getString("reservation_date"));
                    if (!((JSONObject) reservations.get(x)).getString("meal").isEmpty() && !((JSONObject) reservations.get(x)).getString("meal").equals("null")) {
                        dataModel.setMeal_id(((JSONObject) reservations.get(x)).getJSONObject("meal").getString("id"));
                        dataModel.setMeal_name(((JSONObject) reservations.get(x)).getJSONObject("meal").getString("name"));
                        String[] items = new String[((JSONObject) reservations.get(x)).getJSONArray("meal_items").length()];
                        for (int i = 0; i < items.length; i++) {
                            items[i] = ((JSONObject) reservations.get(x)).getJSONArray("meal_items").getJSONObject(i).getString("name");
                        }
                        dataModel.setMeal_details(items);
                    }

                    dataModels.add(dataModel);
                }
            }
            if (limos.length() != 0) {
                for (int x = limos.length() - 1; x >= 0; x--) {

                    req_info dataModel = new req_info();
                    dataModel.setReq_type(3);
                    dataModel.setId(((JSONObject) limos.get(x)).getString("id"));
                    dataModel.setDate(((JSONObject) limos.get(x)).getString("date"));
                    dataModel.setPickup_address(((JSONObject) limos.get(x)).getString("pickup_address"));
                    dataModel.setPickup_time(((JSONObject) limos.get(x)).getString("pickup_time"));
                    dataModel.setDropoff_time(((JSONObject) limos.get(x)).getString("dropoff_time"));
                    dataModel.setPhone1(((JSONObject) limos.get(x)).getString("phone1"));
                    dataModel.setPhone2(((JSONObject) limos.get(x)).getString("phone2"));
                    dataModel.setName1(((JSONObject) limos.get(x)).getString("name1"));
                    dataModel.setName2(((JSONObject) limos.get(x)).getString("name2"));
                    dataModel.setPrice(((JSONObject) limos.get(x)).getJSONObject("car").getString("price"));
                    dataModel.setCar_model(((JSONObject) limos.get(x)).getJSONObject("car").getString("model"));
                    dataModel.setCar_name(((JSONObject) limos.get(x)).getJSONObject("car").getString("type"));
                    dataModel.setCar_details(((JSONObject) limos.get(x)).getJSONObject("car").getString("details"));

                    dataModel.setReq_status(((JSONObject) limos.get(x)).getString("status_id"));

                    dataModel.setPaid(((JSONObject) limos.get(x)).getString("paid"));
                    dataModel.setApproved(((JSONObject) limos.get(x)).getString("approved"));

                    dataModels.add(dataModel);
                }
            }
            adapter = new RequestManager.req_status_list_adapter(dataModels, getApplicationContext());
            runOnUiThread(new Runnable() {
                public void run() {
                    listView.setAdapter(adapter);
                }
            });
        } else showNoData();
    }
    private String getBusType(String id) {
        Bus_types.FeedReaderDbHelper dbHelper = new Bus_types.FeedReaderDbHelper(getApplicationContext());

        SQLiteDatabase db = dbHelper.getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                Bus_types.FeedEntry.GId,
                Bus_types.FeedEntry.name
        };


// How you want the results sorted in the resulting Cursor
        String sortOrder =
                Bus_types.FeedEntry.name + " DESC";

        Cursor cursor = db.query(
                Bus_types.FeedEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                Bus_types.FeedEntry.GId + "=?",              // The columns for the WHERE clause
                new String[]{id},          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );
        String name = "";
        while (cursor.moveToNext()) {
            name = cursor.getString(
                    cursor.getColumnIndexOrThrow(Cities.FeedEntry.name));
            cursor.close();
        }
        return name;
    }

    public void getTime(final req_info dataModel, View view) {
        JSONObject json = new JSONObject();
        JSONObject subJSON = new JSONObject();
        try {

            json.put("method", "getStatus");
            subJSON.put("type", dataModel.getReq_type());
            subJSON.put("id", dataModel.getId());
            json.put("data", subJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (iokihttp.isNetworkConnected(getApplicationContext())) {
            showLoading();
            iokihttp.post(getResources().getString(R.string.url) + "getStatus", json.toString(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    hideLoading();
                    System.out.println("FAIL");
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.try_later), Toast.LENGTH_SHORT).show();
                        }
                    });
                    //  hideLoading();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    hideLoading();
                    // hideLoading();
                    if (response.isSuccessful()) {
                        String responseStr = response.body().string();
                        try {
                            JSONObject resJSON = new JSONObject(responseStr);
                            JSONObject subresJSON = new JSONObject(resJSON.getString("data"));
                            if (Integer.parseInt(resJSON.get("error").toString()) == 1) {
                                Long counter = TimeUnit.DAYS.toMillis(Long.parseLong(subresJSON.get("d").toString())) + TimeUnit.HOURS.toMillis(Long.parseLong(subresJSON.get("h").toString())) + TimeUnit.MINUTES.toMillis(Long.parseLong(subresJSON.get("m").toString())) + TimeUnit.SECONDS.toMillis(Long.parseLong(subresJSON.get("s").toString()));
                                // int status = Integer.parseInt(dataModel.getReq_status());
                                // openTimer(status, Integer.parseInt(dataModel.getReq_type()), counter);
                            } else {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.try_later), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        System.out.println("Response=" + responseStr);

                    } else {
                        System.out.println("Response=" + "jjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj");
                    }
                }

            });
        }
    }

    public void onBackPressed() {

        if (main) {
            finish();
        } else {
            if (timerPage) {
                try {
                    countDownTimer.cancel();
                } catch (Exception e) {
                }
                timerPage = false;
            }
            final View req_list = findViewById(R.id.req_list);
            final View status_linear = findViewById(R.id.status_linear);
            //   final View  = view.findViewById(R.id.req_details);
            // final View req_details2 = view.findViewById(R.id.service_details);


            req_list.setVisibility(View.VISIBLE);
            status_linear.setVisibility(View.GONE);
            findViewById(R.id.hall_details).setVisibility(View.GONE);
            findViewById(R.id.limo_details).setVisibility(View.GONE);
            findViewById(R.id.seera_details).setVisibility(View.GONE);

            main = true;
            req_list.setAlpha(1f);
        }
    }

    private StateProgressBar.StateNumber getStateNumber(int number) {
        switch (number) {
            case 1: {
                return StateProgressBar.StateNumber.ONE;
            }
            case 2: {
                return StateProgressBar.StateNumber.TWO;
            }
            default: {
                return StateProgressBar.StateNumber.THREE;
            }
        }
    }

    private void openTimer(int status, int type, Long counter) {
        StateProgressBar stateProgressBar = findViewById(R.id.status_bar);

        String[] description = new String[3];
        description[0] = getResources().getString(R.string.status1);
        description[1] = getResources().getString(R.string.status2);
        description[2] = getResources().getString(R.string.status3);

        stateProgressBar.setStateDescriptionData(description);
        stateProgressBar.setCurrentStateNumber(getStateNumber(status));

        changeMsg(status, type);
        setTimerValues(counter);
        startCountDownTimer();
        // app:spb_currentStateNumber="three"

        final View req_list = findViewById(R.id.req_list);
        final View status_linear = findViewById(R.id.status_linear);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                req_list.setVisibility(View.GONE);
                status_linear.setVisibility(View.VISIBLE);
                status_linear.animate().alpha(1f).setDuration(700);
                main = false;
            }
        });


    }

    private void changeMsg(int status, int type) {
        TextView msg = findViewById(R.id.status_msg);

        switch (type) {
            case 1: {
                switch (status) {
                    case 1: {
                        msg.setText(getResources().getString(R.string.hall_status1));
                    }
                    break;
                    case 2: {
                        msg.setText(getResources().getString(R.string.hall_status2));
                    }
                    break;
                    case 3: {
                        msg.setText(getResources().getString(R.string.hall_status3));
                    }
                    break;
                }
            }
            break;
            case 2: {
                switch (status) {
                    case 1: {
                        msg.setText(getResources().getString(R.string.seera_status1));
                    }
                    break;
                    case 2: {
                        msg.setText(getResources().getString(R.string.seera_status2));
                    }
                    break;
                    case 3: {
                        msg.setText(getResources().getString(R.string.seera_status3));
                    }
                    break;
                }
            }
            break;
        }
    }

    private void getList(int id) {
        System.out.println("ID IS : " + id);
        JSONObject json = new JSONObject();
        JSONObject subJSON = new JSONObject();
        try {

            json.put("method", "currentRequests");
            subJSON.put("user_id", id);
            json.put("data", subJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (iokihttp.isNetworkConnected(getApplicationContext())) {
            showLoading();
            iokihttp.post(getResources().getString(R.string.url) + "currentRequests", json.toString(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    hideLoading();
                    System.out.println("FAIL");
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.try_later), Toast.LENGTH_SHORT).show();
                        }
                    });
                    //  hideLoading();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    hideLoading();
                    // hideLoading();
                    if (response.isSuccessful()) {
                        String responseStr = response.body().string();
                        try {
                            JSONObject resJSON = new JSONObject(responseStr);
                            JSONObject subresJSON = new JSONObject(resJSON.getString("data"));
                            if (Integer.parseInt(resJSON.get("error").toString()) == 1) {
                                JSONArray seeras = new JSONArray(subresJSON.get("seeras").toString());
                                JSONArray reservations = new JSONArray(subresJSON.get("reservations").toString());
                                JSONArray limos = new JSONArray(subresJSON.get("limo").toString());
                                setList(seeras, reservations, limos);
                            } else {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.try_later), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        System.out.println("Response=" + responseStr);

                    } else {
                        System.out.println("Response=" + "jjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj");
                    }
                }

            });
        }
    }

    private void setInfo(req_info model) {
        hideAll();
        switch (model.getReq_type()) {
            case 1: {
                main = false;
                findViewById(R.id.hall_details).setVisibility(View.VISIBLE);
                fillHallBill(model);
            }
            break;
            case 3: {
                main = false;
                findViewById(R.id.limo_details).setVisibility(View.VISIBLE);
                fillLimoBill(model);
            }
            break;
            case 9: {
                main = false;
                findViewById(R.id.seera_details).setVisibility(View.VISIBLE);
                fillSeeraBill(model);
            }
            break;
        }
    }

    private void fillSeeraBill(final req_info busReq) {
        ((TextView) seera_details_include.findViewById(R.id.bus_type)).setText(busReq.getBus_type());
        ((TextView) seera_details_include.findViewById(R.id.num_of_buses)).setText(busReq.getNumber_of_buses());
        ((TextView) seera_details_include.findViewById(R.id.reservation_type)).setText(busReq.getReservation_type());
        ((TextView) seera_details_include.findViewById(R.id.reservation_date)).setText(busReq.getDate());
        ((TextView) seera_details_include.findViewById(R.id.main_phone)).setText(busReq.getPhone1());
        ((TextView) seera_details_include.findViewById(R.id.sub_phone)).setText(busReq.getPhone2());
        ((TextView) seera_details_include.findViewById(R.id.pickup_location)).setText(busReq.getPickup_address());
        ((TextView) seera_details_include.findViewById(R.id.dropoff_location)).setText(busReq.getDropoff_address());
        ((TextView) seera_details_include.findViewById(R.id.price)).setText(busReq.getPrice());
        if (Integer.parseInt(busReq.getApproved()) == 1 && Integer.parseInt(busReq.getPaid()) == 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    seera_details_include.findViewById(R.id.pay).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openPaymentMethod(busReq.getId(), "9");
                        }
                    });
                    seera_details_include.findViewById(R.id.pay).setVisibility(View.VISIBLE);
                }
            });
        }
    }

    private void fillHallBill(req_info hallReservationModel) {
        ((TextView) hall_details_include.findViewById(R.id.b_reservation_type)).setText(Integer.parseInt(hallReservationModel.getReservation_type_id()) == 1 ? getResources().getString(R.string.day) : getResources().getString(R.string.night));
        ((TextView) hall_details_include.findViewById(R.id.b_hall_name)).setText(hallReservationModel.getHall_name());
        ((TextView) hall_details_include.findViewById(R.id.b_city)).setText(hallReservationModel.getCity_name());
        ((TextView) hall_details_include.findViewById(R.id.b_date)).setText(hallReservationModel.getDate());
        ((TextView) hall_details_include.findViewById(R.id.b_package)).setText(hallReservationModel.getPackage_name());
        ((TextView) hall_details_include.findViewById(R.id.b_meal)).setText(hallReservationModel.getMeal_name());
        ((TextView) hall_details_include.findViewById(R.id.b_meal_details)).setText(Integer.parseInt(hallReservationModel.getMeal_id()) == 0 ? "" : getDetailsString(hallReservationModel.getMeal_details()));
        ((TextView) hall_details_include.findViewById(R.id.b_package_details)).setText(Integer.parseInt(hallReservationModel.getPackage_id()) == 0 ? "" : getDetailsString(hallReservationModel.getPackage_details()));
        ((TextView) hall_details_include.findViewById(R.id.b_num_of_people)).setText(hallReservationModel.getNum_of_people());
        ((TextView) hall_details_include.findViewById(R.id.b_include_meal)).setText(Integer.parseInt(hallReservationModel.getInclude_meal()) == 1 ? getResources().getString(R.string.yes) : getResources().getString(R.string.no));
        ((TextView) hall_details_include.findViewById(R.id.hall_price)).setText(hallReservationModel.getPrice());
        ((TextView) hall_details_include.findViewById(R.id.package_price)).setText(hallReservationModel.getPackage_price());
        System.out.println(hallReservationModel.getMeal_price() + " *" + hallReservationModel.getNum_of_meals());
        Float T_meal_price = Float.parseFloat(hallReservationModel.getMeal_price()) * Float.parseFloat(hallReservationModel.getNum_of_meals());
        ((TextView) hall_details_include.findViewById(R.id.meal_price)).setText(hallReservationModel.getMeal_price() + " X " + hallReservationModel.getNum_of_meals() + " = " + T_meal_price);
        Float T_price = T_meal_price + Float.parseFloat(hallReservationModel.getPrice()) + Float.parseFloat(hallReservationModel.getPackage_price());
        ((TextView) hall_details_include.findViewById(R.id.b_price)).setText(String.valueOf(T_price));

    }

    private void fillLimoBill(final req_info limoReq) {
        ((TextView) limo_details_include.findViewById(R.id.req_id)).setText(limoReq.getId());
        ((TextView) limo_details_include.findViewById(R.id.car_name)).setText(limoReq.getCar_name());
        ((TextView) limo_details_include.findViewById(R.id.car_model)).setText(limoReq.getCar_model());
        ((TextView) limo_details_include.findViewById(R.id.reservation_date)).setText(limoReq.getDate());
        ((TextView) limo_details_include.findViewById(R.id.price)).setText(limoReq.getPrice());
        ((TextView) limo_details_include.findViewById(R.id.pickup_time)).setText(limoReq.getPickup_time());
        ((TextView) limo_details_include.findViewById(R.id.dropoff_time)).setText(limoReq.getDropoff_time());
        ((TextView) limo_details_include.findViewById(R.id.pickup_location)).setText(limoReq.getPickup_address());
        ((TextView) limo_details_include.findViewById(R.id.name1)).setText(limoReq.getName1());
        ((TextView) limo_details_include.findViewById(R.id.name2)).setText(limoReq.getName2());
        ((TextView) limo_details_include.findViewById(R.id.phone1)).setText(limoReq.getPhone1());
        ((TextView) limo_details_include.findViewById(R.id.phone2)).setText(limoReq.getPhone2());


    }

    private String getDetailsString(String[] details) {
        String detailsString = "";
        assert details != null;
        for (String detail : details) {
            detailsString = detailsString + " - " + detail;
        }
        return detailsString;
    }

    private void removeRequest(final String type, final String reqId) {
        System.out.println("hhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
        final AlertDialog.Builder builder = new AlertDialog.Builder(RequestManager.this);
        builder.setMessage(getString(R.string.delete_verfication) + reqId)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        showLoading();
                        JSONObject json = new JSONObject();
                        JSONObject subJSON = new JSONObject();
                        try {
                            json.put("method", "deleteRequest");
                            subJSON.put("req_type", type);
                            subJSON.put("req_id", reqId);
                            json.put("data", subJSON);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (iokihttp.isNetworkConnected(getApplicationContext())) {
                            iokihttp.post(getResources().getString(R.string.url) + "deleteRequest", json.toString(), new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    System.out.println("FAIL");
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.try_later), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    //  hideLoading();
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    hideLoading();
                                    if (response.isSuccessful()) {
                                        String responseStr = response.body().string();
                                        try {
                                            JSONObject resJSON = new JSONObject(responseStr);
                                            if (Integer.parseInt(resJSON.get("error").toString()) == 1) {
                                                runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.deleted_successfully), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                                getList(Integer.parseInt(shared.getString("user_id", "")));

                                            } else {
                                                runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.try_later), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        System.out.println("Response=" + responseStr);

                                    } else {
                                        System.out.println("Response=" + "jjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj");
                                    }
                                }

                            });
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private String getStatusString(int approved, int paid) {
        String statusS = "";
        if (approved == 0 && paid == 0)
            statusS = getResources().getString(R.string.waiting_approval);
        else if (approved != 0 && paid == 0)
            statusS = getResources().getString(R.string.waiting_payment);
        else if (approved == 0 && paid != 0) statusS = getResources().getString(R.string.payed);
        else if (approved != 0 && paid != 0) statusS = getResources().getString(R.string.approved);
        return statusS;
    }

    private String getStatusid(int approved, int paid) {
        String statusS = "1";
        if (approved == 0 && paid == 0)
            statusS = "1";
        else if (approved != 0 && paid == 0)
            statusS = "1";
        else if (approved == 0 && paid != 0) statusS = "2";
        else if (approved != 0 && paid != 0) statusS = "3";
        return statusS;
    }

    private void hideAll() {
        findViewById(R.id.hall_details).setVisibility(View.GONE);
        findViewById(R.id.seera_details).setVisibility(View.GONE);
        findViewById(R.id.limo_details).setVisibility(View.GONE);
        findViewById(R.id.req_list).setVisibility(View.GONE);
        findViewById(R.id.status_linear).setVisibility(View.GONE);
    }

    private String getReqType(int req_type) {

        String req_type_s = "";
        switch (req_type) {
            case 1: {
                req_type_s = getApplicationContext().getResources().getString(R.string.halls);
            }
            break;
            case 2: {
                req_type_s = getApplicationContext().getResources().getString(R.string.nemaSaving);
            }
            break;
            case 3: {
                req_type_s = getApplicationContext().getResources().getString(R.string.zfa);
            }
            break;
            case 4: {
                req_type_s = getApplicationContext().getResources().getString(R.string.photographer);
            }
            break;
            case 5: {
                req_type_s = getApplicationContext().getResources().getString(R.string.barber);
            }
            break;
            case 6: {
                req_type_s = getApplicationContext().getResources().getString(R.string.hairdresser);
            }
            break;
            case 7: {
                req_type_s = getApplicationContext().getResources().getString(R.string.hotels);
            }
            break;
            case 8: {
                req_type_s = getApplicationContext().getResources().getString(R.string.honey_moon);
            }
            break;
            case 9: {
                req_type_s = getApplicationContext().getResources().getString(R.string.bus);
            }
            break;
            case 10: {
                req_type_s = getApplicationContext().getResources().getString(R.string.meeting_halls);
            }
            break;
            case 11: {
                req_type_s = getApplicationContext().getResources().getString(R.string.org_group);
            }
            break;
            case 12: {
                req_type_s = getApplicationContext().getResources().getString(R.string.kitchens);
            }
            break;
            case 13: {
                req_type_s = getApplicationContext().getResources().getString(R.string.outdoor_halls);
            }
            break;
            case 14: {
                req_type_s = getApplicationContext().getResources().getString(R.string.party_designer);
            }
            break;
        }
        return req_type_s;
    }

    public void showLoading() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                System.out.println("inside run");
                findViewById(R.id.layout).setVisibility(View.GONE);
                findViewById(R.id.loading).setVisibility(View.VISIBLE);
                System.out.println("after inside run");

            }

        });
        System.out.println("after run");


    }

    public void hideLoading() {
        System.out.println("inside hide");

        final View main = findViewById(R.id.layout);
        final ProgressBar loading = findViewById(R.id.loading);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                main.setClickable(true);
                main.setVisibility(View.VISIBLE);
                loading.setVisibility(View.GONE);
            }
        });
    }

    public class req_status_list_adapter extends ArrayAdapter<req_info> implements View.OnClickListener {
        private final Context mContext;
        private int lastPosition = -1;
        private RequestManager.req_status_list_adapter.ViewHolder viewHolder;

        public req_status_list_adapter(ArrayList<req_info> data, Context context) {
            super(context, R.layout.req_info, data);
            System.out.println("DATAMODELS INSIDE ADAPTER=" + data.size());
            this.mContext = context;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            // Get the data item for this position
            final req_info dataModel = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            // view lookup cache stored in tag

            final View result;
            if (convertView == null) {
                viewHolder = new RequestManager.req_status_list_adapter.ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.req_info, parent, false);

                viewHolder.req_id = convertView.findViewById(R.id.req_id);
                // viewHolder.datetime = convertView.findViewById(R.id.date_status);
                viewHolder.req_status = convertView.findViewById(R.id.req_status);
                viewHolder.req_type = convertView.findViewById(R.id.req_type);
                viewHolder.status = convertView.findViewById(R.id.rel_status);
                viewHolder.more = convertView.findViewById(R.id.rel_more);
                viewHolder.delete = convertView.findViewById(R.id.deleteBtn);

                result = convertView;

                System.out.println("Position=" + position);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (RequestManager.req_status_list_adapter.ViewHolder) convertView.getTag();
                result = convertView;
            }
            assert dataModel != null;

            viewHolder.req_id.setText(dataModel.getId());

            //   viewHolder.datetime.setText(dataModel.getDate());
            viewHolder.req_status.setText(getStatusString(Integer.parseInt(dataModel.getApproved()), Integer.parseInt(dataModel.getPaid())));
            viewHolder.req_status.setTextColor(getColor(Integer.parseInt(dataModel.getApproved()), Integer.parseInt(dataModel.getPaid())));

            viewHolder.req_type.setText(getReqType(dataModel.getReq_type()));
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    1.5f
            );
            if (Integer.parseInt(dataModel.getPaid()) == 1) {
                viewHolder.delete.setVisibility(View.GONE);
                viewHolder.status.setLayoutParams(param);
                viewHolder.more.setLayoutParams(param);
            }
            viewHolder.status.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //  timerPage = true;
                    //  getTime(dataModel, view);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.service_soon), Toast.LENGTH_LONG).show();
                        }
                    });
                    // openTimer(getStatusid(Integer.parseInt(dataModel.getApproved()),Integer.parseInt(dataModel.getPaid())), 1, counter);
                }
            });
            viewHolder.more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setInfo(dataModel);
                }
            });
            viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeRequest(String.valueOf(dataModel.getReq_type()), dataModel.getId());
                }
            });

            lastPosition = position;

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

        public int getColor(int approved, int paid) {
            if (approved == 0 && paid == 0) return getResources().getColor(R.color.under_process);
            else if (approved != 0 && paid == 0)
                return getResources().getColor(R.color.under_process);
            else if (approved == 0 && paid != 0)
                return getResources().getColor(R.color.success_green);
            else return getResources().getColor(R.color.success_green);

        }

        private class ViewHolder {
            TextView req_id, datetime, req_status, req_type, order_noT, dateT, statusT, typeT;
            RelativeLayout status, more, delete;


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

}