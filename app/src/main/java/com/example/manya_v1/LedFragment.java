package com.example.manya_v1;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LedFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Button NeonBtn, TrunkBtn, AngelBtn, DemonBtn;

    private Button NeonSaveM1Btn, NeonLoadM1Btn, NeonSaveM2Btn, NeonLoadM2Btn;
    public LedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LedFragment newInstance(String param1, String param2) {
        LedFragment fragment = new LedFragment();
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
        return inflater.inflate(R.layout.fragment_led, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NeonBtn = getView().findViewById(R.id.neon_btn);
        TrunkBtn = getView().findViewById(R.id.trunk_btn);
        AngelBtn = getView().findViewById(R.id.angel_btn);
        DemonBtn = getView().findViewById(R.id.demon_btn);

        NeonLoadM1Btn = getView().findViewById(R.id.neon_load_m1_btn);
        NeonSaveM1Btn = getView().findViewById(R.id.neon_save_m1_btn);
        NeonLoadM2Btn = getView().findViewById(R.id.neon_load_m2_btn);
        NeonSaveM2Btn = getView().findViewById(R.id.neon_save_m2_btn);

        NeonBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).colorPicker("Неон", 20);
            }
        });
        TrunkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).colorPicker("Багажник", 30);
            }
        });
        AngelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).colorPicker("Ангельские глазки", 40);
            }
        });
        DemonBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).colorPicker("Дьявольские глазки", 50);
            }
        });

        NeonLoadM1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).btSendInfo(25, 10);

            }
        });
        NeonSaveM1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).btSendInfo(26, 10);
            }
        });
        NeonLoadM2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).btSendInfo(27, 10);
            }
        });
        NeonSaveM2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).btSendInfo(28, 10);
            }
        });
    }
}