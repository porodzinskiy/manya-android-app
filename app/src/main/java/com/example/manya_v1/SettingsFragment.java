package com.example.manya_v1;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.session.MediaController;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.renderscript.ScriptGroup;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import android.os.Handler;


import com.skydoves.colorpickerview.ColorPickerDialog;

import java.util.concurrent.Future;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Handler handler = new Handler();
    Runnable runnable;
    Switch btAutoSwitch;
    private Button btConnectionBtn;
    Spinner btDevicesSpinner;
    private Button playerPrevBtn, playerPlayBtn, playerNextBtn;
    private TextView playerArtistName, playerTitleName;
    private  String old_title_name = "";
    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
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
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        playerPrevBtn = getView().findViewById(R.id.player_prev_btn);
        playerPlayBtn = getView().findViewById(R.id.player_play_btn);
        playerNextBtn = getView().findViewById(R.id.player_next_btn);

        playerPrevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).playerPrev();
            }
        });
        playerPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).playerPlay();
            }
        });

        playerNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).playerNext();
            }
        });

        btDevicesSpinner = (Spinner) getView().findViewById(R.id.spinner);

        playerArtistName = getView().findViewById(R.id.player_artist_name);
        playerTitleName = getView().findViewById(R.id.player_title_name);

        changePlayerInSettings();

        btDevicesSpinner.setAdapter(((MainActivity)getActivity()).arrayAdapter);
        btDevicesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String info = btDevicesSpinner.getItemAtPosition(i).toString();
                String address = info.substring(info.length()-17);
                ((MainActivity)getActivity()).sharedPreferences.edit().putString("APP_SETTINGS_BTINFO", info).apply();
                ((MainActivity)getActivity()).sharedPreferences.edit().putString("APP_SETTINGS_BTADDRESS", address).apply();
                ((MainActivity)getActivity()).btChangeAddress(address);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

        });
        btAutoSwitch = getView().findViewById(R.id.bt_auto_switch);
        btAutoSwitch.setChecked(((MainActivity)getActivity()).bt_auto_connect_flag);

        btAutoSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).bt_auto_connect_flag = !((MainActivity)getActivity()).bt_auto_connect_flag;
                ((MainActivity)getActivity()).sharedPreferences.edit().putBoolean("APP_SETTINGS_BTCONNECTSTART", ((MainActivity)getActivity()).bt_auto_connect_flag).apply();
            }
        });

        btConnectionBtn = getView().findViewById(R.id.bt_connect_btn);
        btConnectionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).btConnectMethod();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        runnable = new Runnable() {
            @Override
            public void run() {
                String new_title_name = ((MainActivity)getActivity()).playerTitle();
                if (new_title_name != null) {
                    if (old_title_name.equals(new_title_name) == false) {
                        old_title_name = new_title_name;
                        changePlayerInSettings();
                    }
                }
                handler.postDelayed(this, 200);
            }
        };
        handler.postDelayed(runnable, 200);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    void changePlayerInSettings(){
        if (((MainActivity) getActivity()).isNotificationPermission() == false){
            playerArtistName.setText(R.string.player_not_permission_artist);
            playerTitleName.setText(R.string.player_not_permission_title);
        } else {
            if (((MainActivity)getActivity()).playerArtist() == null){
                playerArtistName.setText(R.string.player_not_active_artist);
                playerTitleName.setText(R.string.player_not_active_title);
            } else {
                playerArtistName.setText(((MainActivity)getActivity()).playerArtist());
                playerTitleName.setText(((MainActivity) getActivity()).playerTitle());
            }
        }
    }
}