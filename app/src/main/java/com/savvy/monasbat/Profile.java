package com.savvy.monasbat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;
import com.savvy.monasbat.Network.Iokihttp;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Profile.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Profile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Profile extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Iokihttp iokihttp;
    SharedPreferences shared;
    boolean nameEdit, emailEdit, phoneEdit = false;
    int req_code;
    ArrayList<String> profile_image;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View view;
    private OnFragmentInteractionListener mListener;

    public Profile() {
        // Required empty public constructor
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Profile.
     */
    // TODO: Rename and change types and number of parameters
    public static Profile newInstance(String param1, String param2) {
        Profile fragment = new Profile();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private void getProfile() throws JSONException {
        JSONObject json = new JSONObject();
        final JSONObject subJSON = new JSONObject();
        subJSON.put("id", shared.getString("user_id", "0"));
        try {
            json.put("data", subJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (iokihttp.isNetworkConnected(getContext())) {
            showLoading();
            iokihttp.post(getString(R.string.url) + "getProfile", json.toString(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    hideLoading();
                    System.out.println("FAIL");
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), getString(R.string.try_later), Toast.LENGTH_SHORT).show();
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
                                shared.edit().putString("name", subresJSON.getString("name")).apply();
                                shared.edit().putString("phone", subresJSON.getString("phone")).apply();
                                shared.edit().putString("email", subresJSON.getString("email")).apply();
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            fillProfile(subresJSON.getString("name"), subresJSON.getString("phone"), subresJSON.getString("email"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            } else {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getContext(), getString(R.string.try_later), Toast.LENGTH_SHORT).show();
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

    private void fillProfile(String name, String phone, String email) {
        ((TextView) view.findViewById(R.id.name)).setText(name);
        ((TextView) view.findViewById(R.id.phone)).setText(phone);
        ((TextView) view.findViewById(R.id.name2)).setText(name);
        ((TextView) view.findViewById(R.id.phone2)).setText(phone);
        ((TextView) view.findViewById(R.id.email)).setText(email);


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        try {
            init();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }

    private void openCam(int code) {
        Pix.start(Profile.this, code, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("DDDDDDDDDDDDDD");

        if (requestCode == 101) {
            profile_image = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            try {
                System.out.println("FFFFFFFFFFFFFFFFFFFFFf");
                changeImage(profile_image.get(0), R.id.profile_image);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            hideLoading();
        }
    }

    private void changeImage(String imageString, int id) throws IOException {
        File f = new File(imageString);
        Bitmap d = new BitmapDrawable(getContext().getResources(), f.getAbsolutePath()).getBitmap();
        //Bitmap scaled = com.fxn.utility.Utility.getScaledBitmap(512, com.fxn.utility.Utility.getExifCorrectedBitmap(f));
        Bitmap scaled = com.fxn.utility.Utility.getScaledBitmap(512, d);
        ((ImageView) view.findViewById(id)).setImageBitmap(scaled);
        uploadImage(101, imageString, "profile_image");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Pix.start(Profile.this, req_code, 1);
                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "Approve permissions to open ImagePicker", Toast.LENGTH_LONG).show();

                        }
                    });
                }
                return;
            }
        }
    }

    private void uploadImage(int code, String image1, String name1) throws IOException {
        File f1 = new File(getContext().getCacheDir(), name1);
        f1.createNewFile();
        OutputStream os1 = new BufferedOutputStream(new FileOutputStream(f1));

        File imageString1 = new File(image1);
        Bitmap bitmap1 = new BitmapDrawable(getContext().getResources(), imageString1.getAbsolutePath()).getBitmap();
        bitmap1.compress(Bitmap.CompressFormat.JPEG, 70, os1);
        os1.close();
        System.out.println("LLLLL" + shared.getString("user_id", "0"));
        showLoading();
        iokihttp.uploadImage(shared.getString("user_id", "0"), f1, name1, getResources().getString(R.string.url) + "set_profile_picture", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (iokihttp.retry <= 3) {
                    call.clone();
                    iokihttp.retry++;
                } else {

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hideLoading();
                            Toast.makeText(getContext(), getResources().getString(R.string.try_later), Toast.LENGTH_LONG).show();
                        }
                    });
                }
                System.out.println("FAIL");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                hideLoading();
                iokihttp.retry = 0;
                if (response.isSuccessful()) {
                    String responseStr = response.body().string();
                    try {
                        JSONObject resJSON = new JSONObject(responseStr);
                        // JSONObject subresJSON = new JSONObject(resJSON.getString("data"));
                        if (Integer.parseInt(resJSON.get("error").toString()) == 1) {


                        } else {
                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {

                                    Toast.makeText(getContext(), getString(R.string.try_later), Toast.LENGTH_SHORT).show();
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

    private void init() throws JSONException {
        iokihttp = new Iokihttp();
        shared = getActivity().getSharedPreferences("com.savvy.monasbat", Context.MODE_PRIVATE);
        if (shared.getString("name", "").isEmpty() || shared.getString("name", "").equals("")) {
            getProfile();
        } else {
            fillProfile(shared.getString("name", ""), shared.getString("phone", ""), shared.getString("email", ""));
        }
        view.findViewById(R.id.nameB).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                if (nameEdit) {
                    try {
                        changeForSave(R.id.nameE, R.id.name, R.id.nameB);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    nameEdit = false;
                } else {
                    changeForEdit(R.id.nameE, R.id.name, R.id.nameB);
                    nameEdit = true;
                }
            }
        });
        view.findViewById(R.id.phoneB).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                if (phoneEdit) {
                    try {
                        changeForSave(R.id.phoneE, R.id.phone, R.id.phoneB);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    phoneEdit = false;
                } else {
                    changeForEdit(R.id.phoneE, R.id.phone, R.id.phoneB);
                    phoneEdit = true;
                }
            }
        });
        view.findViewById(R.id.emailB).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                if (emailEdit) {
                    try {
                        changeForSave(R.id.emailE, R.id.email, R.id.emailB);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    emailEdit = false;
                } else {
                    changeForEdit(R.id.emailE, R.id.email, R.id.emailB);
                    emailEdit = true;
                }
            }
        });
        view.findViewById(R.id.profile_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCam(101);
            }
        });

    }

    private void setProfile() throws JSONException {
        final String name, email, phone;
        name = ((TextView) view.findViewById(R.id.name)).getText().toString();
        email = ((TextView) view.findViewById(R.id.email)).getText().toString();
        phone = ((TextView) view.findViewById(R.id.phone)).getText().toString();
        JSONObject json = new JSONObject();
        JSONObject subJSON = new JSONObject();
        subJSON.put("name", name);
        subJSON.put("email", email);
        subJSON.put("phone", phone);
        try {
            json.put("data", subJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (iokihttp.isNetworkConnected(getContext())) {
            showLoading();
            iokihttp.post(getString(R.string.url) + "setProfile", json.toString(), new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    hideLoading();
                    System.out.println("FAIL");
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), getString(R.string.try_later), Toast.LENGTH_SHORT).show();
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
                                if (Integer.parseInt(resJSON.get("code").toString()) == 1) {
                                    shared.edit().putString("name", name).apply();
                                    shared.edit().putString("phone", phone).apply();
                                    shared.edit().putString("email", email).apply();
                                }
                            } else {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getContext(), getString(R.string.try_later), Toast.LENGTH_SHORT).show();
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void changeForEdit(int editID, int textID, int buttonID) {
        EditText editText = view.findViewById(editID);
        TextView textView = view.findViewById(textID);
        ImageView imageView = view.findViewById(buttonID);
        editText.setText(textView.getText().toString());
        textView.setVisibility(View.GONE);
        editText.setVisibility(View.VISIBLE);
        imageView.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.save));
        editText.requestFocus();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void changeForSave(int editID, int textID, int buttonID) throws JSONException {
        EditText editText = view.findViewById(editID);
        TextView textView = view.findViewById(textID);
        ImageView imageView = view.findViewById(buttonID);
        textView.setText(editText.getText().toString());
        textView.setVisibility(View.VISIBLE);
        editText.setVisibility(View.GONE);
        imageView.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.edit_icon));
        setProfile();
    }

    public void showLoading() {
        final View main = view.findViewById(R.id.layout);
        final ProgressBar loading = view.findViewById(R.id.loading);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                main.setClickable(false);

                main.setVisibility(View.GONE);

                loading.setVisibility(View.VISIBLE);
            }
        });
    }

    public void hideLoading() {
        final View main = view.findViewById(R.id.layout);
        final ProgressBar loading = view.findViewById(R.id.loading);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                main.setClickable(true);

                main.setVisibility(View.VISIBLE);

                loading.setVisibility(View.GONE);
            }
        });
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
