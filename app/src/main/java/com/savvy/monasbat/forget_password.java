package com.savvy.monasbat;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.goodiebag.pinview.Pinview;
import com.google.android.material.snackbar.Snackbar;
import com.savvy.monasbat.Network.Iokihttp;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class forget_password extends AppCompatActivity {
    boolean hide = true;
    Iokihttp iokihttp;
    SharedPreferences shared;
    String user_id;
    boolean main = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        init();
    }

    private void init() {
        iokihttp = new Iokihttp();
        shared = this.getSharedPreferences("com.savvy.monasbat", Context.MODE_PRIVATE);
        findViewById(R.id.show_pass_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHidePassword();
            }
        });
        findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateForm()) {
                    try {
                        verify_num();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        findViewById(R.id.verify_pin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verify_pin();
            }
        });

    }

    private void showHidePassword() {
        if (hide) {
            ((EditText) findViewById(R.id.password2)).setTransformationMethod(null);
            hide = false;
        } else {
            ((EditText) findViewById(R.id.password2)).setTransformationMethod(new PasswordTransformationMethod());
            hide = true;
        }
    }

    private boolean validateForm() {
        String phone, password;
        phone = ((EditText) findViewById(R.id.phone)).getText().toString();
        password = ((EditText) findViewById(R.id.password2)).getText().toString();
        if (phone.isEmpty() || password.isEmpty()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Snackbar.make(findViewById(R.id.layout), getResources().getString(R.string.fill_error), Snackbar.LENGTH_SHORT).show();
                }
            });
            return false;
        } else {
            return true;
        }
    }

    private String encrypt(String password) throws NoSuchAlgorithmException {
        try {

            // Static getInstance method is called with hashing MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // digest() method is called to calculate message digest
            //  of an input digest() return array of byte
            byte[] messageDigest = md.digest(password.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressLint("SetTextI18n")
    public void openOTPLayout() {
        final View phoneLayout = findViewById(R.id.phoneLayout);
        final View otcLayout = findViewById(R.id.otcLayout);
        TextView enter_otp = findViewById(R.id.enter_otp);
        enter_otp.setText(getString(R.string.enter_verfication) + ": " + "" + shared.getString("phone", ""));
        animateLayout(phoneLayout, otcLayout);
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
        before.animate().alpha(0f).setDuration(700).setListener(animatorListener);
    }

    @Override
    public void onBackPressed() {
        if (main) {
            super.onBackPressed();
            finish();
        } else {
            final View phoneLayout = findViewById(R.id.phoneLayout);
            final View otcLayout = findViewById(R.id.otcLayout);

            phoneLayout.setVisibility(View.VISIBLE);
            otcLayout.setVisibility(View.GONE);

            main = true;
            phoneLayout.setAlpha(1f);
        }
    }

    private void verify_num() throws JSONException {

        String phone;
        // String token = FirebaseInstanceId.getInstance().getToken();

        phone = ((EditText) findViewById(R.id.phone)).getText().toString();
        shared.edit().putString("phone", phone).apply();
        JSONObject json = new JSONObject();
        final JSONObject subJSON = new JSONObject();
        subJSON.put("phone", phone);

        try {
            json.put("data", subJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (iokihttp.isNetworkConnected(getApplicationContext())) {
            showLoading();

            iokihttp.post(getString(R.string.url) + "forgetPassword", json.toString(), new Callback() {
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
                                if (Integer.parseInt(resJSON.get("code").toString()) == 1) {
                                    final JSONObject subresJSON = resJSON.getJSONObject("data");
                                    user_id = subresJSON.getString("id");
                                    openOTPLayout();
                                    hideLoading();
                                    // openMain();
                                } else if (Integer.parseInt(resJSON.get("code").toString()) == 2) {
                                    hideLoading();

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), getString(R.string.wrong_password), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    hideLoading();

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), getString(R.string.fill_error), Toast.LENGTH_SHORT).show();
                                        }
                                    });
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

    private void verify_pin() {
        Pinview pin = findViewById(R.id.pinView);
        String password1 = ((EditText) findViewById(R.id.password2)).getText().toString();

        JSONObject json = new JSONObject();
        JSONObject subJSON = new JSONObject();
        if (pin.getValue().isEmpty() || pin.getValue().length() < 4) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.fill_error), Toast.LENGTH_LONG).show();
            return;
        }
        try {
            subJSON.put("id", user_id);
            subJSON.put("pin", pin.getValue());
            subJSON.put("password", encrypt(password1));
            json.put("data", subJSON);

        } catch (JSONException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        showLoading();
        if (iokihttp.isNetworkConnected(getApplicationContext())) {
            iokihttp.post(getString(R.string.url) + "verifyPin", json.toString(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    System.out.println("FAIL");
                    hideLoading();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseStr = response.body().string();
                        try {
                            JSONObject resJSON = new JSONObject(responseStr);
                            if (Integer.parseInt(resJSON.get("error").toString()) == 1 && Integer.parseInt(resJSON.getString("code")) == 1) {
                                finish();
                            } else {
                                hideLoading();

                                runOnUiThread(new Runnable() {
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
}