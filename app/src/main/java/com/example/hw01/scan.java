package com.example.hw01;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link scan#newInstance} factory method to
 * create an instance of this fragment.
 */
public class scan extends Fragment {
    boolean sc = false;
    BluetoothLeScanner mBluetoothLeScanner;
    BluetoothManager mBluetoothManager;
    BluetoothAdapter mBluetoothAdapter;
    RecyclerView mRecyclerView;
    MyListAdapter myListAdapter;
    HashMap<String,BLEDevice> hashMap = new HashMap<>();
    ArrayList<BLEDevice> arrayList = new ArrayList<>();
    ArrayList<String> rs = new ArrayList<>();
    BluetoothDevice device = null;
    private static final int PERMISSION_REQUEST_CODE = 666;

    private final static String[] permissionWeNeed = new String[]{
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public scan() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment scan.
     */
    // TODO: Rename and change types and number of parameters
    public static scan newInstance(String param1, String param2) {
        scan fragment = new scan();
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
        setupPermission();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan, container, false);
        sc = false;

        mRecyclerView = view.findViewById(R.id.rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        myListAdapter = new MyListAdapter();
        mRecyclerView.setAdapter(myListAdapter);
        Button SB = view.findViewById(R.id.ScanButton);
        SB.setOnClickListener(v -> {
            if (!sc){
                sc = true;
                SB.setText("關閉掃描");
                mBluetoothLeScanner.startScan(startScanCallback);
            }
            else if(sc){
                sc = false;
                SB.setText("開啟掃描");
                mBluetoothLeScanner.stopScan(startScanCallback);
            }
        });


        // Inflate the layout for this fragment
        return view;

    }

    private void setupPermission(){
        boolean isGranted = true;
        for(String permission : permissionWeNeed){
            isGranted &= ActivityCompat.checkSelfPermission(getActivity(),permission) == PackageManager.PERMISSION_GRANTED;
        }
        if(!isGranted){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(permissionWeNeed,PERMISSION_REQUEST_CODE);
            }
            else{
                Toast.makeText(getActivity(),"no permission",Toast.LENGTH_SHORT).show();
                getActivity().finishAndRemoveTask();
            }
        }
        else {
            initBluetooth();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,@NonNull String permissions[],@NonNull int[] grantResults){
        switch (requestCode){
            case PERMISSION_REQUEST_CODE:{
                boolean isGranted = grantResults.length > 0;
                for(int grantResult : grantResults){
                    isGranted &= grantResult == PackageManager.PERMISSION_GRANTED;
                }
                if(isGranted){
                    initBluetooth();
                }
                else {
                    Toast.makeText(getActivity(),"no permission",Toast.LENGTH_SHORT).show();
                    getActivity().finishAndRemoveTask();
                }
            }
        }
    }


    private void initBluetooth(){
        boolean success = false;
        mBluetoothManager = (BluetoothManager)getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        if(mBluetoothManager != null){
            mBluetoothAdapter = mBluetoothManager.getAdapter();
            if(mBluetoothAdapter != null){
                mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
                success = true;
            }
        }
        if(!success){
            Toast.makeText(getActivity(),"cannot start",Toast.LENGTH_SHORT).show();
            getActivity().finishAndRemoveTask();
        }
    }


    private final ScanCallback startScanCallback = new ScanCallback() {
        @RequiresApi(api = Build.VERSION_CODES.R)
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            device = result.getDevice();
            ScanRecord mScanRecord = result.getScanRecord();
            String address = device.getAddress();
            byte[] content = mScanRecord.getBytes();
            int mRssi = result.getRssi();
            String dataS = byteArrayToHexString(content);
            if(address == null || address.trim().length() == 0){
                return;
            }
            myListAdapter.addDevice(address,"" + mRssi,dataS);
            myListAdapter.notifyDataSetChanged();
        }
    };

    public static String byteArrayToHexString(byte[] b){
        int len = b.length;
        String data = "";

        for(int i = 0;i < len;i++){
            data += Integer.toHexString((b[i]>>4) & 0xf);
            data += Integer.toHexString(b[i] & 0xf);
        }
        return data;
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

    public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder>{

        class ViewHolder extends RecyclerView.ViewHolder{
            private TextView add;
            private TextView rss;
            private Button DB;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                add = itemView.findViewById(R.id.mac);
                rss = itemView.findViewById(R.id.ss);
                DB = itemView.findViewById(R.id.det);
                DB.setOnClickListener(v -> {
                    Bundle bd = new Bundle();
                    bd.putString("detail",rs.get(getAdapterPosition()));
                    Navigation.findNavController(v).navigate(R.id.detail,bd);
                });
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.R)
        public void addDevice(String mac, String rssi, String cont){
            if(hashMap.containsKey(mac)){
                return;
            }
            BLEDevice ble = new BLEDevice();
            ble.DeviceName = mac;
            ble.Rssi = rssi;
            ble.content = cont;
            arrayList.add(0,ble);
            rs.add(0,mac + " " + rssi + " " + cont + " " + device.getAlias() + " " + device.getName());
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.rvlayout, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.add.setText(arrayList.get(position).DeviceName);
            holder.rss.setText(arrayList.get(position).Rssi);
        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }
    }











}