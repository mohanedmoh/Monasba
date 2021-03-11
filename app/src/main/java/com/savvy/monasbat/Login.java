package com.savvy.monasbat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;
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

public class Login extends AppCompatActivity {

    boolean hide = true;
    Iokihttp iokihttp;
    SharedPreferences shared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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
        findViewById(R.id.forget_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openForget();
            }
        });
        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateForm()) {
                    try {
                        login();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        findViewById(R.id.signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSignup();
            }
        });
    }

    private void openForget() {
        Intent intent = new Intent(getApplicationContext(), forget_password.class);
        startActivity(intent);
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

    private void login() throws NoSuchAlgorithmException, JSONException {

        String password1, phone;
        String token = FirebaseInstanceId.getInstance().getToken();
        //System.out.println();
        password1 = ((EditText) findViewById(R.id.password2)).getText().toString();
        phone = ((EditText) findViewById(R.id.phone)).getText().toString();
        JSONObject json = new JSONObject();
        JSONObject subJSON = new JSONObject();
        subJSON.put("password", encrypt(password1));
        subJSON.put("phone", phone);
        subJSON.put("token", token);
        System.out.println("json=" + subJSON);
        try {
            json.put("data", subJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (iokihttp.isNetworkConnected(getApplicationContext())) {
            showLoading();

            iokihttp.post(getString(R.string.url) + "login", json.toString(), new Callback() {
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
                                    shared.edit().putString("name", subresJSON.getString("name")).apply();
                                    shared.edit().putString("phone", subresJSON.getString("phone")).apply();
                                    shared.edit().putString("email", subresJSON.getString("email")).apply();
                                    shared.edit().putString("user_id", subresJSON.getString("id")).apply();
                                    openMain();
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

    private void openSignup() {
        Intent intent = new Intent(getApplicationContext(), Signup.class);
        startActivity(intent);
    }

    private void openMain() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        shared.edit().putBoolean("login", true).apply();
        startActivity(intent);

        finish();


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
