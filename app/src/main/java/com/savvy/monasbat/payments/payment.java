package com.savvy.monasbat.payments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bushrapay.api.BushraResult;
import com.bushrapay.api.DirectPayUI;
import com.bushrapay.api.bone.BushraRequest;
import com.bushrapay.api.bone.Currency;
import com.bushrapay.api.bone.ResultCode;
import com.bushrapay.api.bone.Tags;
import com.savvy.monasbat.Network.Iokihttp;
import com.savvy.monasbat.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class payment extends AppCompatActivity {
    String uuid;
    int paymentMethod;
    SharedPreferences shared;
    Iokihttp iokihttp;
    String req_type, id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        init();
    }

    private void init() {
        shared = getSharedPreferences("com.savvy.monasbat", Context.MODE_PRIVATE);
        // method call to initialize the views
        iokihttp = new Iokihttp();
        req_type = getIntent().getExtras().getString("req_type");
        id = getIntent().getExtras().getString("id");


        findViewById(R.id.syberCheckbox).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePayment(view);
            }
        });
        findViewById(R.id.syberImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePayment(view);
            }
        });
        findViewById(R.id.bushraCheckbox).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePayment(view);
            }
        });
        findViewById(R.id.bushraImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePayment(view);
            }
        });
        findViewById(R.id.cashCheckbox).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePayment(view);
            }
        });
        findViewById(R.id.cashImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePayment(view);
            }
        });
        findViewById(R.id.pay).setVisibility(View.VISIBLE);
        findViewById(R.id.pay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    setPaymentMethod();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
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
                checkSyberPaymentStatus(uuid, req_type);
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
                        afterPayment(req_type);
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
        subJSON.put("flag", "9");

        try {
            json.put("data", subJSON);
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

    private void afterPayment(String req_type) throws JSONException {
        if (paymentMethod == 2) {
            checkSyberPaymentStatus(uuid, req_type);
        } else if (paymentMethod == 3) {
            changePaymentStatus(uuid);

        }
    }

    public void changePayment(View view) {

        CheckBox bankC = findViewById(R.id.syberCheckbox);
        CheckBox cashC = findViewById(R.id.cashCheckbox);
        CheckBox bushraC = findViewById(R.id.bushraCheckbox);

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
        JSONObject json = new JSONObject();
        JSONObject subJSON = new JSONObject();
        subJSON.put("id", id);
        subJSON.put("flag", req_type);
        subJSON.put("payment_method", paymentMethod);
        try {
            json.put("data", subJSON);
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
                                            findViewById(R.id.check_payment).setVisibility(View.VISIBLE);
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
                                            findViewById(R.id.check_payment).setVisibility(View.VISIBLE);
                                            hideLoading();
                                        }
                                    });
                                    hideLoading();
                                } else {
                                    setResult(1);
                                    finish();
                                    hideLoading();
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

    private void openBushraPay() {
        BushraRequest Request = new BushraRequest(Double.parseDouble(""), Currency.SDG, uuid, null);
        Intent intent = new Intent(payment.this, DirectPayUI.class);
        intent.putExtra(Tags.API_ACTIVITY_REQUEST_TAG, Request);
//The following putExtra parameters are optional
//if you know your client's card informations you can help them to enter those informations
        //intent.putExtra(Tags.API_ACTIVITY_PAN_TAG , "1111111111111111");
        //intent.putExtra(Tags.API_ACTIVITY_EX_TAG , "2302");
        startActivityForResult(intent, 200);
    }

    private void checkSyberPaymentStatus(String uuid, String req_type) throws JSONException {
        JSONObject json = new JSONObject();
        JSONObject subJSON = new JSONObject();
        subJSON.put("uuid", uuid);
        subJSON.put("flag", req_type);
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
                                int responseCode = subresJSON.getInt("responseCode");
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
}