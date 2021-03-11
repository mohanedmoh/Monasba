package com.savvy.monasbat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.savvy.monasbat.Network.Iokihttp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import androidx.fragment.app.Fragment;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Home.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Home extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    View view;
    Iokihttp iokihttp;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Home() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Home.
     */
    // TODO: Rename and change types and number of parameters
    public static Home newInstance(String param1, String param2) {
        Home fragment = new Home();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        view = inflater.inflate(R.layout.fragment_home, container, false);
        init();
        return view;
    }

    private void init() {
        initOnClick();
        iokihttp = new Iokihttp();
    }

    private void initOnClick() {
        view.findViewById(R.id.honeymoon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openView(view.getId());
            }
        });
        view.findViewById(R.id.halls).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openView(view.getId());
            }
        });
        view.findViewById(R.id.nemasave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openView(view.getId());
            }
        });
        view.findViewById(R.id.zafa).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openView(view.getId());
            }
        });
        view.findViewById(R.id.photographer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openView(view.getId());
            }
        });
        view.findViewById(R.id.barber).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openView(view.getId());
            }
        });
        view.findViewById(R.id.makeup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openView(view.getId());
            }
        });
        view.findViewById(R.id.hotel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openView(view.getId());
            }
        });
        view.findViewById(R.id.sara).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openView(view.getId());
            }
        });
        view.findViewById(R.id.meetinghalls).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openView(view.getId());
            }
        });
        view.findViewById(R.id.team).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openView(view.getId());
            }
        });
        view.findViewById(R.id.cooking).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openView(view.getId());
            }
        });
        view.findViewById(R.id.outdodor_halls).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openView(view.getId());
            }
        });
        view.findViewById(R.id.partydesigner).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openView(view.getId());
            }
        });
        view.findViewById(R.id.request_manager).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openView(view.getId());
            }
        });
    }

    private void getGrace() throws JSONException {
        JSONObject json = new JSONObject();
        final JSONObject subJSON = new JSONObject();
        subJSON.put("type", 1);
        try {

            json.put("data", subJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (iokihttp.isNetworkConnected(getContext())) {
            showLoading();
            iokihttp.post(getString(R.string.url) + "orgs", json.toString(), new Callback() {
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
                                openResult(subresJSON.getJSONArray("organizations"), R.id.nemasave);
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

    private void getLimo() throws JSONException {
        JSONObject json = new JSONObject();
        final JSONObject subJSON = new JSONObject();
        subJSON.put("type", 3);
        try {

            json.put("data", subJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (iokihttp.isNetworkConnected(getContext())) {
            showLoading();
            iokihttp.post(getString(R.string.url) + "limo", json.toString(), new Callback() {
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
                                openResult(subresJSON.getJSONArray("limo"), R.id.zafa);
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
                        System.out.println("Response=" + "jjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj" + response.toString());
                    }
                }

            });
        }
    }

    private void openResult(JSONArray grace, int id) {
        Intent intent = new Intent(getContext(), result.class);
        intent.putExtra("result", grace.toString());
        intent.putExtra("id", id);
        startActivity(intent);
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private void openView(int id) {
        Intent intent;
        switch (id) {
            case R.id.halls: {
                intent = new Intent(getActivity().getApplicationContext(), search_halls.class);
                startActivity(intent);
            }
            break;
            case R.id.nemasave: {
                try {
                    getGrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            break;
            case R.id.sara: {
                intent = new Intent(getActivity().getApplicationContext(), seera.class);
                startActivity(intent);
            }
            break;
            case R.id.zafa: {
                try {
                    getLimo();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            break;
            case R.id.request_manager: {
                intent = new Intent(getActivity().getApplicationContext(), RequestManager.class);
                startActivity(intent);
            }
            break;
            default: {
                unavailableService();
            }
        }
    }

    private void unavailableService() {

        Snackbar.make(view, getResources().getString(R.string.unavailable), Snackbar.LENGTH_SHORT).show();
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
}
