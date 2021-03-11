package com.savvy.monasbat;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kofigyan.stateprogressbar.StateProgressBar;
import com.savvy.monasbat.DB.Bus_types;
import com.savvy.monasbat.DB.Cities;
import com.savvy.monasbat.Model.bus_req;
import com.savvy.monasbat.Network.Iokihttp;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import pl.polak.clicknumberpicker.ClickNumberPickerView;

public class seera extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    public DatePickerDialog dpd;
    public Calendar now;
    String date;
    Iokihttp iokihttp;
    SharedPreferences shared;
    View seera_form_include, seera_bill_include, seera_req_pendding_include, requirements_include;
    StateProgressBar stateProgressBar;
    View before = null;
    View after = null;
    int current = 1;
    List<Object> list;
    bus_req busReq;
    private boolean main = true;
    private LIFOqueue lifo;
    boolean done = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seera);
        init();
    }

    private void init() {
        shared = this.getSharedPreferences("com.savvy.monasbat", Context.MODE_PRIVATE);
        stateProgressBar = findViewById(R.id.status_bar);
        busReq = new bus_req();
        String[] description = new String[4];
        description[0] = getResources().getString(R.string.seera_req1);
        description[1] = getResources().getString(R.string.seera_req2);
        description[2] = getResources().getString(R.string.seera_req3);
        description[3] = getResources().getString(R.string.seera_req4);

        stateProgressBar.setStateDescriptionData(description);
        stateProgressBar.setCurrentStateNumber(getStateNumber(current));


        seera_form_include = findViewById(R.id.seera_form_include);
        seera_bill_include = findViewById(R.id.seera_bill_include);
        seera_req_pendding_include = findViewById(R.id.seera_req_pendding);
        requirements_include = findViewById(R.id.requirements_include);
        iokihttp = new Iokihttp();
        now = Calendar.getInstance();
        seera_req_pendding_include.findViewById(R.id.done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        dpd = DatePickerDialog.newInstance(
                seera.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        seera_form_include.findViewById(R.id.reservation_date).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });
        findViewById(R.id.next_form).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    openNextForm();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        findViewById(R.id.back_form).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    current--;
                    stateProgressBar.setCurrentStateNumber(getStateNumber(current));
                    onBackPressed();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        fillReservationTypesTime();
        getBuses();
        list = new ArrayList<Object>();
        list.add(R.id.seera_form_include);
        list.add(R.id.requirements_include);
        list.add(R.id.seera_bill_include);

        lifo = new LIFOqueue(list);
        lifo.pop();
    }

    public void onBackPressed() {
        if (main) {
            finish();
        } else if (done) {
            finish();
        } else {
            View after = findViewById((Integer) lifo.scndpop());
            View before = findViewById((Integer) lifo.scndpeek());

            final View finalBefore = before;
            final View finalAfter = after;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    finalBefore.setVisibility(View.VISIBLE);
                    finalAfter.setVisibility(View.GONE);
                }
            });


            before.setAlpha(1f);
        }
        if (lifo.scndsize() == 1) {
            main = true;
        }
    }

    private void openNextForm() throws JSONException {
        if (lifo.size() == 0) {
            sendSeeraReq();
        } else if (lifo.size() != 0) {
            before = findViewById((Integer) lifo.scndpeek());
            after = null;
            if (before.getId() == R.id.seera_form_include) {
                if (validate()) {
                    current++;
                    stateProgressBar.setCurrentStateNumber(getStateNumber(current));
                    // fillBill();
                    after = findViewById((Integer) lifo.pop());
                    animateLayout(before, after);
                } else {
                }
            } else if (before.getId() == R.id.requirements_include) {
                if (((CheckBox) requirements_include.findViewById(R.id.requirement_checkbox)).isChecked()) {
                    getSeeraPrice();
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.agree_term), Toast.LENGTH_LONG).show();
                }
            } else if (before.getId() == R.id.seera_bill_include) {
                sendSeeraReq();
            }
        } else {
        }
    }
    private void sendSeeraReq() throws JSONException {
        JSONObject json = new JSONObject();
        final JSONObject subJSON = new JSONObject();
        subJSON.put("date", busReq.getDate());
        subJSON.put("pickup_address", busReq.getPickup_address());
        subJSON.put("dropoff_address", busReq.getDropoff_address());
        subJSON.put("main_phone", busReq.getMain_phone());
        subJSON.put("sub_phone", busReq.getSub_phone());
        subJSON.put("bus_type_id", busReq.getBus_type_id());
        subJSON.put("num_of_buses", busReq.getNumber_of_buses());
        subJSON.put("reservation_type", busReq.getReservation_type_id());
        subJSON.put("user_id", shared.getString("user_id", "0"));
        try {
            json.put("data", subJSON);
            System.out.println("json=" + json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (iokihttp.isNetworkConnected(getApplicationContext())) {
            showLoading();
            iokihttp.post(getString(R.string.url) + "saveSeeraReq", json.toString(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    hideLoading();
                    System.out.println("FAIL");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), getString(R.string.try_later), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    hideLoading();
                    if (response.isSuccessful()) {
                        String responseStr = response.body().string();
                        try {

                            JSONObject resJSON = new JSONObject(responseStr);

                            if (Integer.parseInt(resJSON.get("error").toString()) == 1) {
                                final JSONObject subresJSON = resJSON.getJSONObject("data");
                                // fillBill();
                                current++;
                                stateProgressBar.setCurrentStateNumber(getStateNumber(current));
                                //after = findViewById((Integer) lifo.pop());

                                //animateLayout(before, after);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        findViewById(R.id.layout).setVisibility(View.GONE);
                                        findViewById(R.id.seera_req_pendding).setVisibility(View.VISIBLE);
                                        findViewById(R.id.frame_done).setVisibility(View.VISIBLE);
                                    }
                                });

                                done = true;


                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), getString(R.string.try_later), Toast.LENGTH_SHORT).show();
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

    private void fillReservationTypesTime() {
        ((RadioButton) seera_form_include.findViewById(R.id.night)).setText(getResources().getString(R.string.night) + " " + getResources().getString(R.string.seera_night_time));
        ((RadioButton) seera_form_include.findViewById(R.id.day)).setText(getResources().getString(R.string.day) + " " + getResources().getString(R.string.seera_day_time));

    }

    private void getSeeraPrice() throws JSONException {
        JSONObject json = new JSONObject();
        final JSONObject subJSON = new JSONObject();
        subJSON.put("bus_type_id", busReq.getBus_type_id());
        subJSON.put("num_of_buses", busReq.getNumber_of_buses());
        subJSON.put("reservation_type", busReq.getReservation_type_id());
        try {
            json.put("data", subJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (iokihttp.isNetworkConnected(getApplicationContext())) {
            showLoading();
            iokihttp.post(getString(R.string.url) + "getSeeraPrice", json.toString(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    hideLoading();
                    System.out.println("FAIL");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), getString(R.string.try_later), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    hideLoading();
                    if (response.isSuccessful()) {
                        String responseStr = response.body().string();
                        try {


                            JSONObject resJSON = new JSONObject(responseStr);

                            if (Integer.parseInt(resJSON.get("error").toString()) == 1) {
                                final JSONObject subresJSON = resJSON.getJSONObject("data");
                                busReq.setPrice(subresJSON.getString("price"));
                                fillBill();
                                current++;
                                stateProgressBar.setCurrentStateNumber(getStateNumber(current));
                                after = findViewById((Integer) lifo.pop());
                                animateLayout(before, after);

                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), getString(R.string.try_later), Toast.LENGTH_SHORT).show();
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

    public void animateLayout(final View before, final View after) {
        Animator.AnimatorListener animatorListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                System.out.println("SHOW " + after.getId());
                before.setVisibility(View.GONE);
                after.setVisibility(View.VISIBLE);
                //  finalAfter.animate().alpha(1f).setDuration(700);

                main = false;


            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        };
        before.animate().alpha(0f).setDuration(0).setListener(animatorListener);
        //hideLoading();
    }

    private boolean validate() {
        String req_date = date;
        String num_of_bus = String.valueOf(((ClickNumberPickerView) seera_form_include.findViewById(R.id.num_of_buses)).getValue());
        @SuppressLint("CutPasteId") String bus_type_id = ((StringWithTag) ((Spinner) seera_form_include.findViewById(R.id.bus_type)).getSelectedItem()).key;
        @SuppressLint("CutPasteId") String bus_type = ((StringWithTag) ((Spinner) seera_form_include.findViewById(R.id.bus_type)).getSelectedItem()).value;
        String main_phone = ((EditText) seera_form_include.findViewById(R.id.main_phone)).getText().toString();
        String sub_phone = ((EditText) seera_form_include.findViewById(R.id.sub_phone)).getText().toString();
        String pickup_address = ((EditText) seera_form_include.findViewById(R.id.pickup_location)).getText().toString();
        String dropoff_address = ((EditText) seera_form_include.findViewById(R.id.dropoff_location)).getText().toString();
        @SuppressLint("CutPasteId") String reservation_type_id = (seera_form_include.findViewById(R.id.day).isSelected() ? "1" : "2");
        @SuppressLint("CutPasteId") String reservation_type = (seera_form_include.findViewById(R.id.day).isSelected() ? getResources().getString(R.string.day) : getResources().getString(R.string.night));
        if (!((RadioButton) seera_form_include.findViewById(R.id.day)).isChecked() && !((RadioButton) seera_form_include.findViewById(R.id.night)).isChecked()) {
            Toast.makeText(getApplicationContext(), getString(R.string.fill_error), Toast.LENGTH_SHORT).show();
            return false;
        } else if (num_of_bus.equals("0") || num_of_bus.isEmpty() || bus_type_id.equals("0") || main_phone.isEmpty() || sub_phone.isEmpty() || pickup_address.isEmpty() || dropoff_address.isEmpty() || req_date.isEmpty()) {
            Toast.makeText(getApplicationContext(), getString(R.string.fill_error), Toast.LENGTH_SHORT).show();
            return false;
        } else {
            busReq.setBus_type_id(bus_type_id);
            busReq.setBus_type(bus_type);
            busReq.setMain_phone(main_phone);
            busReq.setSub_phone(sub_phone);
            busReq.setPickup_address(pickup_address);
            busReq.setDropoff_address(dropoff_address);
            busReq.setReservation_type(reservation_type);
            busReq.setReservation_type_id(reservation_type_id);
            busReq.setNumber_of_buses(num_of_bus);
            busReq.setDate(date);
            return true;
        }
    }

    private void fillBill() {
        ((TextView) seera_bill_include.findViewById(R.id.bus_type)).setText(busReq.getBus_type());
        ((TextView) seera_bill_include.findViewById(R.id.num_of_buses)).setText(busReq.getNumber_of_buses());
        ((TextView) seera_bill_include.findViewById(R.id.reservation_type)).setText(busReq.getReservation_type());
        ((TextView) seera_bill_include.findViewById(R.id.reservation_date)).setText(busReq.getDate());
        ((TextView) seera_bill_include.findViewById(R.id.main_phone)).setText(busReq.getMain_phone());
        ((TextView) seera_bill_include.findViewById(R.id.sub_phone)).setText(busReq.getSub_phone());
        ((TextView) seera_bill_include.findViewById(R.id.pickup_location)).setText(busReq.getPickup_address());
        ((TextView) seera_bill_include.findViewById(R.id.dropoff_location)).setText(busReq.getDropoff_address());
        ((TextView) seera_bill_include.findViewById(R.id.price)).setText(busReq.getPrice());

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

    public void getBuses() {
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
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );
        ArrayList<seera.StringWithTag> buses = new ArrayList<>();
        buses.add(new seera.StringWithTag(getResources().getString(R.string.bus_type), "0"));
        while (cursor.moveToNext()) {
            int id = cursor.getInt(
                    cursor.getColumnIndexOrThrow(Cities.FeedEntry.GId));
            String name = cursor.getString(
                    cursor.getColumnIndexOrThrow(Cities.FeedEntry.name));
            buses.add(new seera.StringWithTag(name, String.valueOf(id)));
        }
        cursor.close();
        fillBuses(buses);
    }

    public void fillBuses(final ArrayList buses) {

        final Spinner bus = seera_form_include.findViewById(R.id.bus_type);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.simple_spinner_item_custom, buses);
        bus.setAdapter(adapter);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        date = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
        ((TextView) (seera_form_include.findViewById(R.id.reservation_date))).setText(date);
    }

    public void showLoading() {
        final View main = findViewById(R.id.layout);
        final ProgressBar loading = findViewById(R.id.loading);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                main.setClickable(false);

                main.setVisibility(View.GONE);

                loading.setVisibility(View.VISIBLE);
            }
        });
    }

    public void hideLoading() {
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

    private static class StringWithTag {
        public String key;
        public String value;

        public StringWithTag(String value, String key) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }
}
