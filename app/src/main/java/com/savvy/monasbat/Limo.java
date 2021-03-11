package com.savvy.monasbat;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.kofigyan.stateprogressbar.StateProgressBar;
import com.savvy.monasbat.Model.limo_req;
import com.savvy.monasbat.Model.limo_result;
import com.savvy.monasbat.Model.policy;
import com.savvy.monasbat.Network.Iokihttp;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class Limo extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {
    Iokihttp iokihttp;
    SharedPreferences shared;
    View limo_form_include, limo_req_pendding_include, limo_requirements_include, limo_reserver_info_include;
    StateProgressBar stateProgressBar;
    View before = null;
    View after = null;
    limo_req limo_requst;
    int current = 1;
    List<Object> list;
    boolean done = false;
    limo_result limo_details;
    private String time, time2;
    private boolean main = true;
    private LIFOqueue lifo;
    public DatePickerDialog dpd;
    public Calendar now;
    String date = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_limo);
        init();
    }

    private void init() {
        now = Calendar.getInstance();

        dpd = DatePickerDialog.newInstance(
                Limo.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

        dpd.setMinDate(now);
        shared = this.getSharedPreferences("com.savvy.monasbat", Context.MODE_PRIVATE);
        limo_details = (limo_result) getIntent().getExtras().getBundle("limo").getSerializable("limo");
        setImages(limo_details.getImages());
        setPrice(limo_details.getPrice());
        stateProgressBar = findViewById(R.id.status_bar);
        limo_requst = new limo_req();
        limo_requst.setCar_id(limo_details.getId());
        String[] description = new String[3];
        description[0] = getResources().getString(R.string.limo_req1);
        description[1] = getResources().getString(R.string.limo_req2);
        description[2] = getResources().getString(R.string.limo_req3);

        stateProgressBar.setStateDescriptionData(description);
        stateProgressBar.setCurrentStateNumber(getStateNumber(current));


        limo_form_include = findViewById(R.id.limo_form_include);
        limo_reserver_info_include = findViewById(R.id.limo_reserver_info_include);
        limo_requirements_include = findViewById(R.id.limo_requirements_include);
        limo_req_pendding_include = findViewById(R.id.limo_req_pendding);
        iokihttp = new Iokihttp();
        limo_req_pendding_include.findViewById(R.id.done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(111);
                finish();
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
        findViewById(R.id.arrival_time).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog tpd = TimePickerDialog.newInstance(Limo.this, Calendar.HOUR_OF_DAY, Calendar.MINUTE, true);
                tpd.show(getFragmentManager(), "Timepickerdialog");
            }
        });
        findViewById(R.id.reservation_date).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });
        list = new ArrayList<Object>();
        list.add(R.id.limo_form_include);
        list.add(R.id.limo_requirements_include);
        list.add(R.id.limo_reserver_info_include);
        lifo = new LIFOqueue(list);
        lifo.pop();
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        date = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
        ((TextView) (limo_form_include.findViewById(R.id.reservation_date))).setText(date);
    }

    public void onBackPressed() {
        if (main) {
            finish();
        } else if (done) {
            finish();
        } else {
            ((TextView) findViewById(R.id.next_form)).setText(getResources().getString(R.string.next));

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
        if (validateReserverInfo() && lifo.size() == 0) {
            sendLimoReq();
        } else if (lifo.size() != 0) {
            before = findViewById((Integer) lifo.scndpeek());
            after = null;
            if (before.getId() == R.id.limo_form_include) {
                if (validate()) {
                    current++;
                    stateProgressBar.setCurrentStateNumber(getStateNumber(current));
                    setPolicy(limo_details.getPolicies());
                    after = findViewById((Integer) lifo.pop());
                    animateLayout(before, after);
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), getString(R.string.fill_error), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else if (before.getId() == R.id.limo_requirements_include) {
                if (((CheckBox) limo_requirements_include.findViewById(R.id.requirement_checkbox)).isChecked()) {
                    current++;
                    stateProgressBar.setCurrentStateNumber(getStateNumber(current));
                    after = findViewById((Integer) lifo.pop());
                    ((TextView) findViewById(R.id.next_form)).setText(getResources().getString(R.string.reserve));
                    animateLayout(before, after);
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.agree_term), Toast.LENGTH_LONG).show();
                }
            } else if (before.getId() == R.id.limo_reserver_info_include) {
                if (validateReserverInfo()) {
                    sendLimoReq();
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), getString(R.string.fill_error), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }
    }
    private boolean validate() {
        String address = ((TextView) limo_form_include.findViewById(R.id.delivery_location)).getText().toString();
        if (time.isEmpty() || address.isEmpty() || date.isEmpty()) return false;
        else {
            limo_requst.setPickup_time(time);
            limo_requst.setDropoff_time(time2);
            limo_requst.setPickup_address(address);
            limo_requst.setDate(date);
            return true;
        }
    }
    private boolean validateReserverInfo() {
        String name1, name2, phone1, phone2;
        name1 = ((EditText) limo_reserver_info_include.findViewById(R.id.name1)).getText().toString();
        phone1 = ((EditText) limo_reserver_info_include.findViewById(R.id.phone1)).getText().toString();
        name2 = ((EditText) limo_reserver_info_include.findViewById(R.id.name2)).getText().toString();
        phone2 = ((EditText) limo_reserver_info_include.findViewById(R.id.phone2)).getText().toString();
        if (name1.isEmpty() || name2.isEmpty() || phone1.isEmpty() || phone2.isEmpty()) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.fill_error), Toast.LENGTH_LONG).show();
            return false;
        } else if(phone1.length()<10||phone2.length()<10){
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.phone_length_error), Toast.LENGTH_LONG).show();
            return false;
        }
        else {
            limo_requst.setName1(name1);
            limo_requst.setName2(name2);
            limo_requst.setPhone1(phone1);
            limo_requst.setPhone2(phone2);
            return true;
        }
    }

    @SuppressLint("SetTextI18n")
    private void setPrice(String price) {
        ((TextView) findViewById(R.id.price)).setText(price + " " + getResources().getString(R.string.sdg));
    }

    private void setImages(String[] images) {
        ArrayList<SlideModel> imageList = new ArrayList<>();
        if (images != null) {
            System.out.println("inside not null");
            for (int x = 0; x < images.length; x++) {
                imageList.add(new SlideModel(images[x]));
            }
        } else {
            for (int x = 0; x < 2; x++) {
                imageList.add(new SlideModel(R.drawable.logo, String.valueOf(x + 1)));
            }
        }
        ((ImageSlider) findViewById(R.id.image_slider)).setImageList(imageList, true);
    }

    private void setPolicy(String[] policies) {
        final ArrayList<policy> dataModels = new ArrayList<>();
        final ListView listView = limo_requirements_include.findViewById(R.id.limo_policy);

        if (policies.length == 0) {
        } else {
            for (int x = 0; x < policies.length; x++) {
                policy dataModel = new policy();
                dataModel.setNum(String.valueOf(x + 1));
                dataModel.setPolicy(policies[x]);
                dataModels.add(dataModel);
            }
            final policy_adapter adapter = new policy_adapter(dataModels, getApplicationContext());
            runOnUiThread(new Runnable() {
                public void run() {
                    listView.setAdapter(adapter);
                }
            });
        }
    }

    private void sendLimoReq() throws JSONException {
        JSONObject json = new JSONObject();
        final JSONObject subJSON = new JSONObject();
        System.out.println("name=" + limo_requst.getName1());
        subJSON.put("app", "1");
        subJSON.put("car_id", limo_requst.getCar_id());
        subJSON.put("pickup_address", limo_requst.getPickup_address());
        subJSON.put("pickup_time", limo_requst.getPickup_time() + ":00");
        subJSON.put("dropoff_time", limo_requst.getDropoff_time() + ":00");
        subJSON.put("name1", limo_requst.getName1());
        subJSON.put("name2", limo_requst.getName2());
        subJSON.put("phone1", limo_requst.getPhone1());
        subJSON.put("phone2", limo_requst.getPhone2());
        subJSON.put("date", limo_requst.getDate());
        subJSON.put("user_id", shared.getString("user_id", "0"));
        try {
            json.put("data", subJSON);
            System.out.println("json=" + json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (iokihttp.isNetworkConnected(getApplicationContext())) {
            showLoading();
            iokihttp.post(getString(R.string.url) + "saveLimoReq", json.toString(), new Callback() {
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
                                if (Integer.parseInt(resJSON.get("code").toString()) == 1) {
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
                                            findViewById(R.id.limo_req_pendding).setVisibility(View.VISIBLE);
                                            findViewById(R.id.frame_done).setVisibility(View.VISIBLE);
                                        }
                                    });

                                    done = true;
                                } else if (Integer.parseInt(resJSON.get("code").toString()) == 3) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), getString(R.string.date_error), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), getString(R.string.try_later), Toast.LENGTH_SHORT).show();
                                        }
                                    });
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

    /*  private void fillBill() {
          ((TextView) seera_bill_include.findViewById(R.id.bus_type)).setText(busReq.getBus_type());
          ((TextView) seera_bill_include.findViewById(R.id.num_of_buses)).setText(busReq.getNumber_of_buses());
          ((TextView) seera_bill_include.findViewById(R.id.reservation_type)).setText(busReq.getReservation_type());
          ((TextView) seera_bill_include.findViewById(R.id.reservation_date)).setText(busReq.getDate());
          ((TextView) seera_bill_include.findViewById(R.id.main_phone)).setText(busReq.getMain_phone());
          ((TextView) seera_bill_include.findViewById(R.id.sub_phone)).setText(busReq.getSub_phone());
          ((TextView) seera_bill_include.findViewById(R.id.pickup_location)).setText(busReq.getPickup_address());
          ((TextView) seera_bill_include.findViewById(R.id.dropoff_location)).setText(busReq.getDropoff_address());
          ((TextView) seera_bill_include.findViewById(R.id.price)).setText(busReq.getPrice());

      }*/
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

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {

        time = (hourOfDay==0?"00":hourOfDay ) + ":" +( minute==0?"00":minute) ;
        int h2 = (hourOfDay + 8);
        h2 = h2 >= 24 ? h2 - 24 : h2;
        time2 = (h2==0?"00":h2)  + ":" + (minute==0?"00":minute) ;

        ((TextView) findViewById(R.id.arrival_time)).setText(time);
        ((TextView) findViewById(R.id.departure_time)).setText(time2);

    }

    public class policy_adapter extends ArrayAdapter<policy> implements View.OnClickListener {
        private final Context mContext;
        private int lastPosition = -1;
        private Limo.policy_adapter.ViewHolder viewHolder;


        public policy_adapter(ArrayList<policy> data, Context context) {
            super(context, R.layout.policy_list_item, data);
            this.mContext = context;

        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            // Get the data item for this position
            final policy dataModel = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            // view lookup cache stored in tag

            final View result;

            if (convertView == null) {


                viewHolder = new policy_adapter.ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.policy_list_item, parent, false);


                viewHolder.num = convertView.findViewById(R.id.num);
                viewHolder.policy = convertView.findViewById(R.id.policy);
                assert dataModel != null;


                result = convertView;


                convertView.setTag("companion" + position);


            } else {
                // viewHolder = (ViewHolder) convertView.getTag();
                result = convertView;
            }
            assert dataModel != null;
            viewHolder.num.setText(dataModel.getNum());
            viewHolder.policy.setText(dataModel.getPolicy());


            Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.top_from_down : R.anim.down_from_top);
            result.startAnimation(animation);
            lastPosition = position;

            assert dataModel != null;
            return convertView;
        }

        @Override
        public void onClick(View v) {

        }

        private class ViewHolder {
            TextView num, policy;
        }

    }

}