package com.savvy.monasbat;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bushrapay.api.BushraResult;
import com.bushrapay.api.DirectPayUI;
import com.bushrapay.api.bone.BushraRequest;
import com.bushrapay.api.bone.Currency;
import com.bushrapay.api.bone.ResultCode;
import com.bushrapay.api.bone.Tags;
import com.kofigyan.stateprogressbar.StateProgressBar;
import com.savvy.monasbat.Model.hall_meal;
import com.savvy.monasbat.Model.hall_reservation_model;
import com.savvy.monasbat.Network.Iokihttp;
import com.savvy.monasbat.payments.payment_webview;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.gridlayout.widget.GridLayout;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class hall_reservation extends AppCompatActivity {
    List<Object> list;
    View before = null;
    View after = null;
    int current = 1;
    SharedPreferences shared;
    Iokihttp iokihttp;
    View reserver_info_include, requirements_include, reservation_bill_include, payment_method_include, hall_req_pendding_include,meals_include;
    private LIFOqueue lifo;
    hall_reservation_model hallReservationModel;
    private boolean main = true;
    StateProgressBar stateProgressBar;
    int paymentMethod = 0;
    String uuid = "";
    boolean done = false;
    Float T_price;
    boolean test = false;
    int meal_id, mealsNum = 0;
    String[] description;
    private ArrayList<hall_meal> meals;
    boolean paid = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hall_reservation);
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() throws JSONException {
        shared = this.getSharedPreferences("com.savvy.monasbat", Context.MODE_PRIVATE);
        iokihttp = new Iokihttp();
        hallReservationModel = (hall_reservation_model) getIntent().getExtras().getBundle("reservation").getSerializable("reservation");
        //change after insert meal page

        if (getIntent().getExtras().containsKey("meals")) {

            //setMeals(getIntent().getExtras().getString("meals"));
            description = new String[5];
            description[0] = getResources().getString(R.string.meals);
            description[1] = getResources().getString(R.string.hall_res1);
            description[2] = getResources().getString(R.string.hall_res2);
            description[3] = getResources().getString(R.string.hall_res3);
            description[4] = getResources().getString(R.string.payment);
        } else {
            description = new String[4];
            description[0] = getResources().getString(R.string.hall_res1);
            description[1] = getResources().getString(R.string.hall_res2);
            description[2] = getResources().getString(R.string.hall_res3);
            description[3] = getResources().getString(R.string.payment);
        }
        stateProgressBar = findViewById(R.id.status_bar);


        stateProgressBar.setStateDescriptionData(description);
        stateProgressBar.setCurrentStateNumber(getStateNumber(current));
        meals_include=findViewById(R.id.meals_include);
        reserver_info_include = findViewById(R.id.reserver_info_include);
        reservation_bill_include = findViewById(R.id.reservation_bill_include);
        requirements_include = findViewById(R.id.requirements_include);
        payment_method_include = findViewById(R.id.payment_method_include);
        hall_req_pendding_include = findViewById(R.id.hall_req_pendding);

        hall_req_pendding_include.findViewById(R.id.done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(1);
                finish();
            }
        });
        payment_method_include.findViewById(R.id.syberCheckbox).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePayment(view);
            }
        });
        payment_method_include.findViewById(R.id.syberImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePayment(view);
            }
        });
        payment_method_include.findViewById(R.id.bushraCheckbox).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePayment(view);
            }
        });
        payment_method_include.findViewById(R.id.bushraImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePayment(view);
            }
        });
        payment_method_include.findViewById(R.id.cashCheckbox).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePayment(view);
            }
        });
        payment_method_include.findViewById(R.id.cashImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePayment(view);
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
        payment_method_include.findViewById(R.id.check_payment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    afterPayment();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        hall_req_pendding_include.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeRequest("1", hallReservationModel.getReq_id());
            }
        });
        hall_req_pendding_include.findViewById(R.id.done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    doneRequest();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        list = new ArrayList<Object>();

        if (getIntent().getExtras().containsKey("meals")) {
            JSONArray meals=new JSONArray(getIntent().getExtras().getString("meals"));
            if(meals.length()>0) {
                list.add(R.id.meals_include);
                setMeals(getIntent().getExtras().getString("meals"));
                System.out.println("meals=" + getIntent().getExtras().getString("meals"));
                meals_include.setVisibility(View.VISIBLE);
            }
            else {
                reserver_info_include.setVisibility(View.VISIBLE);
            }

        }
        else {
            reserver_info_include.setVisibility(View.VISIBLE);
        }
        list.add(R.id.reserver_info_include);
        list.add(R.id.requirements_include);
        list.add(R.id.reservation_bill_include);
        list.add(R.id.payment_method_include);

        lifo = new LIFOqueue(list);
        lifo.pop();
        if (getIntent().getExtras().containsKey("test")) {
            test = getIntent().getExtras().getBoolean("test");
        }
    }

    private void removeRequest(final String type, final String reqId) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(hall_reservation.this);
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

    public void onBackPressed() {
        if (main) {
            finish();
        } else if (done) {
            finish();
        } else {
            findViewById(R.id.next_linear).setVisibility(View.VISIBLE);
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
        System.out.println(lifo.scndsize() + "=SIZE=" + lifo.size());
        if (lifo.size() == 0) {
            setPaymentMethod();
        } else if (lifo.size() != 0) {
            before = findViewById((Integer) lifo.scndpeek());
            after = null;

            if (before.getId() == R.id.reserver_info_include) {
                if (validate()) {
                    fillPolicy();
                    current++;
                    stateProgressBar.setCurrentStateNumber(getStateNumber(current));
                    after = findViewById((Integer) lifo.pop());
                    animateLayout(before, after);
                }
            } else if (before.getId() == R.id.requirements_include) {
                if (((CheckBox) requirements_include.findViewById(R.id.requirement_checkbox)).isChecked()) {
                    makeReservation();

                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.agree_term), Toast.LENGTH_LONG).show();
                }
            } else if (before.getId() == R.id.reservation_bill_include) {
                current++;
                stateProgressBar.setCurrentStateNumber(getStateNumber(current));
                after = findViewById((Integer) lifo.pop());
                animateLayout(before, after);
            } else if (before.getId() == R.id.payment_method_include) {
                setPaymentMethod();
            }
        } else {

        }
    }
    @SuppressLint("SetTextI18n")
    private void fillBill() {
        ((TextView) reservation_bill_include.findViewById(R.id.b_reservation_type)).setText(Integer.parseInt(hallReservationModel.getReservation_type_id()) == 1 ? getResources().getString(R.string.day) : getResources().getString(R.string.night));
        ((TextView) reservation_bill_include.findViewById(R.id.b_hall_name)).setText(hallReservationModel.getHall_name());
        ((TextView) reservation_bill_include.findViewById(R.id.b_city)).setText(hallReservationModel.getCity_name());
        ((TextView) reservation_bill_include.findViewById(R.id.b_date)).setText(hallReservationModel.getDate());
        ((TextView) reservation_bill_include.findViewById(R.id.b_package)).setText(hallReservationModel.getPackage_name());
        ((TextView) reservation_bill_include.findViewById(R.id.b_meal)).setText(hallReservationModel.getMeal_name());
        ((TextView) reservation_bill_include.findViewById(R.id.b_meal_details)).setText(Integer.parseInt(hallReservationModel.getMeal_id()) == 0 ? "" : getDetailsString(hallReservationModel.getMeal_details()));
        ((TextView) reservation_bill_include.findViewById(R.id.b_package_details)).setText(Integer.parseInt(hallReservationModel.getPackage_id()) == 0 ? "" : getDetailsString(hallReservationModel.getPackage_details()));
        ((TextView) reservation_bill_include.findViewById(R.id.b_num_of_people)).setText(hallReservationModel.getNum_of_ppl());
        ((TextView) reservation_bill_include.findViewById(R.id.b_include_meal)).setText(Integer.parseInt(hallReservationModel.getInclude_meal()) == 1 ? getResources().getString(R.string.yes) : getResources().getString(R.string.no));
        ((TextView) reservation_bill_include.findViewById(R.id.hall_price)).setText(hallReservationModel.getHall_price());
        ((TextView) reservation_bill_include.findViewById(R.id.package_price)).setText(hallReservationModel.getPackage_price());
        try {
            //change after insert meals
            // float T_meal_price = Float.parseFloat(hallReservationModel.getMeal_price().isEmpty() ? "0" : hallReservationModel.getMeal_price()) * Float.parseFloat(hallReservationModel.getNum_of_meals());
            float T_package_price = Float.parseFloat(hallReservationModel.getPackage_price().isEmpty() ? "0" : hallReservationModel.getPackage_price());

            // ((TextView) reservation_bill_include.findViewById(R.id.meal_price)).setText(hallReservationModel.getMeal_price() + " X " + hallReservationModel.getNum_of_meals() + " = " + T_meal_price);
            //change after insert meals
            //T_price = T_meal_price + Float.parseFloat(hallReservationModel.getHall_price()) + T_package_price;

            T_price = Float.parseFloat(hallReservationModel.getHall_price()) + T_package_price;
            if (Integer.parseInt(hallReservationModel.getPayment_type_id()) == 2) {
                T_price = (T_price / 100)*30;
                ((TextView) reservation_bill_include.findViewById(R.id.b_price)).setText(getResources().getString(R.string.first_payment) + " " + T_price);
            } else {
                ((TextView) reservation_bill_include.findViewById(R.id.b_price)).setText(String.valueOf(T_price));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private String getDetailsString(String[] details) {
        String detailsString = "";
        assert details != null;
        for (String detail : details) {
            detailsString = detailsString + " - " + detail;

        }
        return detailsString;
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    private boolean validate() {
        String name1, name2, phone1, phone2;
        name1 = ((EditText) reserver_info_include.findViewById(R.id.name1)).getText().toString();
        phone1 = ((EditText) reserver_info_include.findViewById(R.id.phone1)).getText().toString();
        name2 = ((EditText) reserver_info_include.findViewById(R.id.name2)).getText().toString();
        phone2 = ((EditText) reserver_info_include.findViewById(R.id.phone2)).getText().toString();
        if (name1.isEmpty() || name2.isEmpty() || phone1.isEmpty() || phone2.isEmpty()) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.fill_error), Toast.LENGTH_LONG).show();
            return false;
        } else {
            hallReservationModel.setName1(name1);
            hallReservationModel.setName2(name2);
            hallReservationModel.setPhone1(phone1);
            hallReservationModel.setPhone2(phone2);
            hallReservationModel.setPayment_type_id(((RadioButton) findViewById(R.id.full)).isChecked() ? "1" : "2");
            return true;
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
    private void openSyberPaymentActivity(String url) {
        Intent intent = new Intent(getApplicationContext(), payment_webview.class);
        intent.putExtra("url", url);
        startActivityForResult(intent, 202);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 202 && resultCode == 1) {
            System.out.println("succccccccccccccccccc");
            try {
                checkSyberPaymentStatus(uuid);
            } catch (Exception e) {
                e.printStackTrace();
            }
           /* after = findViewById((Integer) lifo.pop());
            animateLayout(before, after);*/
        } else if (requestCode == 200) {
            BushraResult result = (BushraResult) data.getSerializableExtra(Tags.API_ACTIVITY_RESULT_TAG);
            switch (resultCode) {
                case ResultCode.SUCCESS: {
//Payment Completed Successfully
                    try {
                        paid = true;
                        afterPayment();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //Toast.makeText(this, result.getID(), Toast.LENGTH_LONG).show();
                }
                break;
                case ResultCode.CANCELED:
                    Toast.makeText(this, "Canceled " + result.getMessage(), Toast.LENGTH_LONG).show();
                    break;
                case ResultCode.ERROR:
                    //network connection anything
                    Toast.makeText(this, "Error " + result.getMessage(), Toast.LENGTH_LONG).show();
                    break;
                case ResultCode.INACTIVE_MERCHANT:
                    Toast.makeText(this, "Inactive Merchant " + result.getMessage(), Toast.LENGTH_LONG).show();
                    break;
                case ResultCode.INVALID_API_KEY:
                    Toast.makeText(this, "Invalid API Key " + result.getMessage(), Toast.LENGTH_LONG).show();
                    break;
                case ResultCode.SESSION_EXPIRED:
                    Toast.makeText(this, "Session Expired " + result.getMessage(), Toast.LENGTH_LONG).show();
                    break;
                case ResultCode.INVALID_AMOUNT:
                    Toast.makeText(this, "Invalid Amount " + result.getMessage(), Toast.LENGTH_LONG).show();
                    break;
                case ResultCode.INVALID_CURRENCY:
                    Toast.makeText(this, "Invalid Currency " + result.getMessage(), Toast.LENGTH_LONG).show();
                    break;
                case ResultCode.DUPLICATED_REFERENCE:
                    Toast.makeText(this, "duplicated Reference " + result.getMessage(), Toast.LENGTH_LONG).show();
                    break;
            }

        } else {
            hideLoading();
        }
    }
    private void changePaymentStatus(String uuid) throws JSONException {
        JSONObject json = new JSONObject();
        JSONObject subJSON = new JSONObject();
        subJSON.put("uuid", uuid);
        subJSON.put("flag", "1");
        try {
            json.put("data", subJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (iokihttp.isNetworkConnected(getApplicationContext())) {
            showLoading();

            iokihttp.post(getString(R.string.url) + "changePaymentStatus", json.toString(), new Callback() {
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
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.succesful_res), Toast.LENGTH_LONG).show();
                                    }
                                });
                                setResult(1);
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        System.out.println("Response=" + responseStr);

                    } else {
                        hideLoading();
                        System.out.println("Response=" + "jjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj");
                    }
                }
            });
        }
    }

    private void doneRequest() throws JSONException {
        JSONObject json = new JSONObject();
        JSONObject subJSON = new JSONObject();
        subJSON.put("id", hallReservationModel.getReq_id());
        subJSON.put("flag", "1");
        try {
            json.put("data", subJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (iokihttp.isNetworkConnected(getApplicationContext())) {
            showLoading();
            findViewById(R.id.frame_done).setVisibility(View.GONE);

            iokihttp.post(getString(R.string.url) + "notifyAdmin", json.toString(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    hideLoading();
                    findViewById(R.id.frame_done).setVisibility(View.VISIBLE);

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
                            if (Integer.parseInt(resJSON.get("error").toString()) == 1 && Integer.parseInt(resJSON.get("code").toString()) == 1) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.succesful_res), Toast.LENGTH_LONG).show();
                                    }
                                });
                                setResult(1);
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        System.out.println("Response=" + responseStr);

                    } else {
                        hideLoading();
                        findViewById(R.id.frame_done).setVisibility(View.VISIBLE);

                        System.out.println("Response=" + "jjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj");
                    }
                }
            });
        }
    }

    private void afterPayment() throws JSONException {
        if (paymentMethod == 2) {
            checkSyberPaymentStatus(uuid);
        } else if (paymentMethod == 3 && paid) {
            changePaymentStatus(uuid);
        }
    }

    public void changePayment(View view) {

        CheckBox bankC = payment_method_include.findViewById(R.id.syberCheckbox);
        CheckBox cashC = payment_method_include.findViewById(R.id.cashCheckbox);
        CheckBox bushraC = payment_method_include.findViewById(R.id.bushraCheckbox);

        // TextView selected = (TextView) payment_method_include.findViewById(R.id.payment_selected);


        if (view.getId() == bankC.getId()) {
            //selected.setText(getResources().getString(R.string.online_bank));
            bankC.setChecked(true);
            cashC.setChecked(false);
            bushraC.setChecked(false);
            //   bankC.setBackgroundColor(getResources().getColor(R.color.success_green));
            // cashC.setBackgroundColor(getResources().getColor(R.color.white));
            paymentMethod = 2;

        } else if (view.getId() == cashC.getId()) {
            // selected.setText(getResources().getString(R.string.cash));
            cashC.setChecked(true);
            bankC.setChecked(false);
            bushraC.setChecked(false);
            paymentMethod = 1;
        } else if (view.getId() == bushraC.getId()) {
            bushraC.setChecked(true);
            cashC.setChecked(false);
            bankC.setChecked(false);

            paymentMethod = 3;
        }
    }
    private void setPaymentMethod() throws JSONException {
        if (test) {
            hideLoading();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    findViewById(R.id.layout).setVisibility(View.GONE);
                    findViewById(R.id.hall_req_pendding).setVisibility(View.VISIBLE);
                    findViewById(R.id.frame_done).setVisibility(View.VISIBLE);
                }
            });

            done = true;
        } else {
            JSONObject json = new JSONObject();
            JSONObject subJSON = new JSONObject();
            subJSON.put("id", hallReservationModel.getReq_id());
            subJSON.put("payment_method", paymentMethod);
            subJSON.put("flag", 1);
            try {
                json.put("data", subJSON);
                System.out.println("json" + json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (iokihttp.isNetworkConnected(getApplicationContext())) {
                showLoading();
                iokihttp.post(getString(R.string.url) + "payment_method", json.toString(), new Callback() {
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
                                    final JSONObject subresJSON = resJSON.getJSONObject("data");
                                    if (paymentMethod == 2) {
                                        openSyberPaymentActivity(subresJSON.getString("url"));
                                        uuid = subresJSON.getString("uuid");
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                payment_method_include.findViewById(R.id.check_payment).setVisibility(View.VISIBLE);
                                                hideLoading();
                                            }
                                        });
                                        hideLoading();
                                    } else if (paymentMethod == 3) {
                                        uuid = subresJSON.getString("uuid");
                                        openBushraPay();
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                payment_method_include.findViewById(R.id.check_payment).setVisibility(View.VISIBLE);
                                                hideLoading();
                                            }
                                        });
                                        hideLoading();
                                    } else {
                                        hideLoading();

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                findViewById(R.id.layout).setVisibility(View.GONE);
                                                findViewById(R.id.hall_req_pendding).setVisibility(View.VISIBLE);
                                                findViewById(R.id.frame_done).setVisibility(View.VISIBLE);
                                            }
                                        });

                                        done = true;

                                    }
                                } else {
                                    hideLoading();

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
                            hideLoading();
                            System.out.println("Response=" + "jjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj");
                        }
                    }
                });
            }
        }
    }
    private void checkSyberPaymentStatus(String uuid) throws JSONException {
        JSONObject json = new JSONObject();
        JSONObject subJSON = new JSONObject();
        subJSON.put("uuid", uuid);
        subJSON.put("flag", "1");
        System.out.println("UUID=" + subJSON);
        try {
            json.put("data", subJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (iokihttp.isNetworkConnected(getApplicationContext())) {
            showLoading();

            iokihttp.post(getString(R.string.url) + "Syber_status", json.toString(), new Callback() {
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
                                int responseCode = subresJSON.getJSONObject("payment").getInt("responseCode");
                                final String responseMessage = subresJSON.getString("responseMessage");
                                if (responseCode == 1) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.succesful_res), Toast.LENGTH_LONG).show();
                                        }
                                    });
                                    setResult(1);
                                    finish();

                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.try_later) + responseMessage, Toast.LENGTH_LONG).show();
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
    private void makeReservation() throws JSONException {
        if (test) {
            hallReservationModel.setReq_id("1");
            current++;
            stateProgressBar.setCurrentStateNumber(getStateNumber(current));
            fillBill();
            after = findViewById((Integer) lifo.pop());
            animateLayout(before, after);
        } else {
            JSONObject json = new JSONObject();
            JSONObject subJSON = new JSONObject();
            subJSON.put("branch_id", hallReservationModel.getHall_id());
            subJSON.put("name", hallReservationModel.getName2());
            subJSON.put("groom_name", hallReservationModel.getName1());
            subJSON.put("phone1", hallReservationModel.getPhone1());
            subJSON.put("phone2", hallReservationModel.getPhone2());
            subJSON.put("address", hallReservationModel.getAddress());
            subJSON.put("extra_hours", hallReservationModel.getExtra_hours());
            subJSON.put("reservation_type_id", hallReservationModel.getReservation_type_id());
            subJSON.put("payment_type_id", hallReservationModel.getPayment_type_id());
            subJSON.put("reservation_date", hallReservationModel.getDate());
            subJSON.put("offer_id", hallReservationModel.getPackage_id());
            subJSON.put("meal_id", hallReservationModel.getMeal_id());
            subJSON.put("num_of_meals", hallReservationModel.getNum_of_meals());
            subJSON.put("user_id", shared.getString("user_id", "0"));
            subJSON.put("app", "1");

            System.out.println("RESERVATIOON=" + subJSON);
            try {
                json.put("data", subJSON);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (iokihttp.isNetworkConnected(getApplicationContext())) {
                showLoading();

                iokihttp.post(getResources().getString(R.string.url) + "makeReservation", json.toString(), new Callback() {
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
                                    hallReservationModel.setReq_id(subresJSON.getString("id"));
                                    current++;
                                    stateProgressBar.setCurrentStateNumber(getStateNumber(current));
                                    fillBill();
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
                            System.out.println("Response=" + "jjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj" + response.toString());
                        }
                    }

                });
            }
        }
    }

    private void openBushraPay() {
        BushraRequest Request = new BushraRequest(T_price, Currency.SDG, uuid, null);
        Intent intent = new Intent(hall_reservation.this, DirectPayUI.class);
        intent.putExtra(Tags.API_ACTIVITY_REQUEST_TAG, Request);
//The following putExtra parameters are optional
//if you know your client's card informations you can help them to enter those informations
        //intent.putExtra(Tags.API_ACTIVITY_PAN_TAG , "1111111111111111");
        //intent.putExtra(Tags.API_ACTIVITY_EX_TAG , "2302");
        startActivityForResult(intent, 200);
    }

    private void fillPolicy() {
        ((TextView) requirements_include.findViewById(R.id.agreemenet)).setText(hallReservationModel.getPolicy());
    }

    private void setMeals(String meals) throws JSONException {

        JSONArray jsonArray = new JSONArray(meals);

        mealsNum = jsonArray.length();

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
            initGrid(dataModels);
        }
    }

    private void initGrid(ArrayList sections) {

        int length = sections.size();
        final int columnNo = 2;
        int rowNo = 0;
        if (length % 2 != 0) {
            length = length + 1;
        }
        rowNo = length / 2;
        final GridLayout section_grid = findViewById(R.id.section_grid);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                section_grid.removeAllViewsInLayout();
            }
        });
        meals = new ArrayList<>();
        final int finalRowNo = rowNo;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                section_grid.setColumnCount(columnNo);
                section_grid.setRowCount(finalRowNo);
            }
        });


        for (int x = 0, c = 0, r = 0; x < length; x++, c++) {
            if (c == columnNo) {
                c = 0;
                r++;
            }
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View section_card = inflater.inflate(R.layout.hall_meal, null);

            final CardView section_cardView = section_card.findViewById(R.id.card);
            section_cardView.setId(getID("1", x));
            TextView lable = section_cardView.findViewById(R.id.meal_name);
            lable.setId(getID("3", x));
            TextView price = section_cardView.findViewById(R.id.meal_price);
            price.setId(getID("4", x));
            hall_meal section = null;
            if (x < sections.size()) {
                section = (hall_meal) sections.get(x);
                lable.setText(section.getName());
                price.setText(section.getPrice());
                section_cardView.setTag(x);
                meals.add(section);
            } else section_cardView.setVisibility(View.INVISIBLE);

            //icon.setImageURI(Uri.parse(section.getString("img")));

            section_cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // openNewsProfile(Integer.parseInt(view.getTag().toString()));
                }
            });
            GridLayout.Spec rowSpan = GridLayout.spec(GridLayout.UNDEFINED, 1);
            GridLayout.Spec colspan = GridLayout.spec(GridLayout.UNDEFINED, 1);
            if (r == 0 && c == 0) {
                Log.e("", "spec");
                colspan = GridLayout.spec(GridLayout.UNDEFINED, 2);
                rowSpan = GridLayout.spec(GridLayout.UNDEFINED, 2);
            }
            final GridLayout.LayoutParams gridParam = new GridLayout.LayoutParams(
                    rowSpan, colspan);

            if (section_cardView.getParent() != null) {
                ((ViewGroup) section_cardView.getParent()).removeView(section_cardView); // <- fix
            }
            final int finalX = x;
            System.out.println("outside" + finalX);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // section_grid.setLayoutParams(gridParam);
                    section_grid.addView(section_cardView);
                }
            });
        }
    }

    private int getID(String id, int number) {
        String s = id + number;
        return Integer.parseInt(s);
    }

    private boolean checkMeals(int meal_include, String numOfMeals) {
        if (meal_include == 1) {
            if (meal_id == 0) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_meals), Toast.LENGTH_LONG).show();
                    }
                });
                return false;
            } else if (Double.parseDouble(numOfMeals) == 0) {
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

}
