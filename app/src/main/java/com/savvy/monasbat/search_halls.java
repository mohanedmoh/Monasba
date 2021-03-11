package com.savvy.monasbat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.savvy.monasbat.DB.Cities;
import com.savvy.monasbat.Model.hall_reservation_model;
import com.savvy.monasbat.Network.Iokihttp;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import pl.polak.clicknumberpicker.ClickNumberPickerView;

public class search_halls extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    public DatePickerDialog dpd;
    public Calendar now;
    String date = "";
    Iokihttp iokihttp;
    SharedPreferences shared;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_halls);
        init();
    }
    private void init() {
        shared = this.getSharedPreferences("com.savvy.monasbat", Context.MODE_PRIVATE);

        iokihttp = new Iokihttp();
        now = Calendar.getInstance();

        dpd = DatePickerDialog.newInstance(
                search_halls.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        //Calendar c = Calendar.getInstance();
        //c.set(2022, 0, 31);//Year,Mounth -1,Day
        //dpd.setMaxDate(c);
        dpd.setMinDate(now);
        findViewById(R.id.reservation_date).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });
        findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (validate()) {
                        search();
                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.fill_error), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        findViewById(R.id.test_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (validate()) {
                        test_search();
                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.fill_error), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        getCities();
    }
    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        date = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
        ((TextView) (findViewById(R.id.reservation_date))).setText(date);
    }
    public void getCities() {
        Cities.FeedReaderDbHelper dbHelper = new Cities.FeedReaderDbHelper(getApplicationContext());

        SQLiteDatabase db = dbHelper.getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                Cities.FeedEntry.GId,
                Cities.FeedEntry.name
        };

        // How you want the results sorted in the resulting Cursor

        String sortOrder =
                Cities.FeedEntry.name + " DESC";
        Cursor cursor = db.query(
                Cities.FeedEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );
        ArrayList<StringWithTag> cities = new ArrayList<>();
        cities.add(new StringWithTag(getResources().getString(R.string.city), "0"));
        while (cursor.moveToNext()) {
            int id = cursor.getInt(
                    cursor.getColumnIndexOrThrow(Cities.FeedEntry.GId));
            String name = cursor.getString(
                    cursor.getColumnIndexOrThrow(Cities.FeedEntry.name));
            cities.add(new StringWithTag(name, String.valueOf(id)));
        }
        cursor.close();
        fillCities(cities);
    }

    public void fillCities(final ArrayList cities) {
        final Spinner country = findViewById(R.id.city);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.simple_spinner_item_custom, cities);
        country.setAdapter(adapter);
    }

    private void test_search() throws JSONException {
        shared.edit().putString("date", date).apply();
        final hall_reservation_model hallReservationModel = new hall_reservation_model();
        hallReservationModel.setDate(date);
        hallReservationModel.setInclude_meal(((RadioButton) findViewById(R.id.yes)).isChecked() ? "1" : "2");
        hallReservationModel.setReservation_type_id(((RadioButton) findViewById(R.id.day)).isChecked() ? "1" : "2");
        hallReservationModel.setNum_of_ppl(String.valueOf(((ClickNumberPickerView) findViewById(R.id.num_of_people)).getValue()));
        hallReservationModel.setCity_id(((StringWithTag) (((Spinner) findViewById(R.id.city)).getSelectedItem())).key);
        hallReservationModel.setCity_name(((StringWithTag) (((Spinner) findViewById(R.id.city)).getSelectedItem())).value);
        JSONArray halls = new JSONArray(loadJSONFromAsset(getApplicationContext()));
        openResult(halls, hallReservationModel, true);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String loadJSONFromAsset(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("halls.json");

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, StandardCharsets.UTF_8);


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }
    private void search() throws JSONException {
        shared.edit().putString("date", date).apply();
        final hall_reservation_model hallReservationModel = new hall_reservation_model();
        hallReservationModel.setDate(date);
        hallReservationModel.setInclude_meal(((RadioButton) findViewById(R.id.yes)).isChecked() ? "1" : "2");
        hallReservationModel.setReservation_type_id(((RadioButton) findViewById(R.id.day)).isChecked() ? "1" : "2");
        hallReservationModel.setNum_of_ppl(String.valueOf(((ClickNumberPickerView) findViewById(R.id.num_of_people)).getValue()));
        hallReservationModel.setCity_id(((StringWithTag) (((Spinner) findViewById(R.id.city)).getSelectedItem())).key);
        hallReservationModel.setCity_name(((StringWithTag) (((Spinner) findViewById(R.id.city)).getSelectedItem())).value);
        // hallReservationModel.setPayment_type_id(((RadioButton) findViewById(R.id.full)).isChecked() ? "1" : "2");

        JSONObject json = new JSONObject();
        final JSONObject subJSON = new JSONObject();
        subJSON.put("type", 1);
        subJSON.put("city", ((StringWithTag) (((Spinner) findViewById(R.id.city)).getSelectedItem())).key);
        subJSON.put("num_of_people", String.valueOf(((ClickNumberPickerView) findViewById(R.id.num_of_people)).getValue()));
        subJSON.put("include_meal", ((RadioButton) findViewById(R.id.yes)).isChecked() ? "1" : "2");
        subJSON.put("reservation_type", ((RadioButton) findViewById(R.id.day)).isChecked() ? "1" : "2");
        subJSON.put("user_id", shared.getString("user_id", "0"));
        subJSON.put("date", date);
        try {
            json.put("data", subJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (iokihttp.isNetworkConnected(getApplicationContext())) {
            showLoading();
            iokihttp.post(getString(R.string.url) + "entities", json.toString(), new Callback() {
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
                    if (response.isSuccessful()) {
                        String responseStr = response.body().string();
                        try {


                            JSONObject resJSON = new JSONObject(responseStr);

                            if (Integer.parseInt(resJSON.get("error").toString()) == 1) {
                                if (Integer.parseInt(resJSON.getString("code")) == 2) {
                                    hideLoading();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), getString(R.string.no_result), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    final JSONObject subresJSON = resJSON.getJSONObject("data");
                                    openResult(subresJSON.getJSONArray("Halls"), hallReservationModel, false);
                                    hideLoading();

                                }

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

    private void openResult(JSONArray halls, hall_reservation_model hallReservationModel, boolean test) {
        Bundle b = new Bundle();
        b.putSerializable("reservation", hallReservationModel);
        System.out.println("KKKKKKKK" + hallReservationModel.getDate());
        Intent intent = new Intent(getApplicationContext(), result.class);
        intent.putExtra("result", halls.toString());
        intent.putExtra("reservation", b);
        intent.putExtra("id", R.id.halls);
        intent.putExtra("test", test);

        startActivityForResult(intent, 112);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 112 && resultCode == 1) {
            finish();
        }
    }

    public void showLoading() {
        final View main = findViewById(R.id.layout);
        final View loading = findViewById(R.id.loading);
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
        final View loading = findViewById(R.id.loading);
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

    private boolean validate() {
        String city_id = ((StringWithTag) (((Spinner) findViewById(R.id.city)).getSelectedItem())).key;
        if (date.equals("") || date.isEmpty() || Integer.parseInt(city_id) == 0) {
            return false;
        } else if (!((RadioButton) findViewById(R.id.yes)).isChecked() && !((RadioButton) findViewById(R.id.no)).isChecked()) {
            Toast.makeText(getApplicationContext(), getString(R.string.choose_include_meal_error), Toast.LENGTH_SHORT).show();
            return false;
        } else if (!((RadioButton) findViewById(R.id.day)).isChecked() && !((RadioButton) findViewById(R.id.night)).isChecked()) {
            Toast.makeText(getApplicationContext(), getString(R.string.choose_reservation_type_error), Toast.LENGTH_SHORT).show();

            return false;
        } else {
            return true;
        }
    }
}
