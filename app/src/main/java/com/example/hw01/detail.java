package com.example.hw01;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link detail#newInstance} factory method to
 * create an instance of this fragment.
 */
public class detail extends Fragment {
    TextView de1,de2,de3,den,dea;
    Button RB;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public detail() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment detail.
     */
    // TODO: Rename and change types and number of parameters
    public static detail newInstance(String param1, String param2) {
        detail fragment = new detail();
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
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        String rs = getArguments().getString("detail");
        String[] word = rs.split(" ");
        de1 = view.findViewById(R.id.de1);
        de2 = view.findViewById(R.id.de2);
        de3 = view.findViewById(R.id.de3);
        dea = view.findViewById(R.id.deAlias);
        den = view.findViewById(R.id.dename);
        de1.setText("ADDRESS: " + word[0]);
        de2.setText("RSSI: " + word[1]);
        de3.setText("context: " + word[2]);
        dea.setText("Alias: " + word[3]);
        den.setText("name: " + word[4]);
        RB = view.findViewById(R.id.ReturnButton);
        RB.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.scan2);
        });
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getFocus();
    }

    private void getFocus() {
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    return true;
                }
                return false;
            }
        });
    }

}