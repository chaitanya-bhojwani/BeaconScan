package com.example.affine.beaconscan;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.UUID;

import io.reactivex.observers.DefaultObserver;

public class MainActivity extends AppCompatActivity implements BleManager.LeScanCallback, SwipeRefreshLayout.OnRefreshListener {
    final String TAG = MainActivity.class.getSimpleName();

    private Button m_refreshButton;
    private TextView m_zeroDeviceInfo;
    private CoordinatorLayout m_coordinatorLayout;
    private SwipeRefreshLayout m_swipeRefreshLayout;
    private RecyclerView m_scanList;
    private BleManager m_bleManager;
    private PermissionManager m_permissionManager;
    private LeDeviceListAdapter m_leDeviceListAdapter;
    private ProgressDialog m_progressBar;
    private final long m_scanPeriod = 5000;
    private FilterTextSession m_filterSession;
    private String m_filterText;
    int TxPower = 0;
    int Rssi = 0;
    String point;
    String scenario;
    Gson gson = new Gson();
    DataManager dataManager = new DataManager(new ApiService.Factory().createService());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blescan);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Bundle extras = getIntent().getExtras();
        point = extras.getString("Point");
        scenario = extras.getString("Scenario");
        Log.e("Point","Point is: "+point);
        Log.e("Scenario","Scenario is: "+scenario);

        requestBluetooth();

        m_coordinatorLayout = findViewById(R.id.mainLayout);
        m_scanList = findViewById(R.id.deviceList);
        m_swipeRefreshLayout = findViewById(R.id.swiperefresh);
        m_swipeRefreshLayout.setOnRefreshListener(this);
        m_refreshButton = findViewById(R.id.refreshButton);
        m_zeroDeviceInfo = findViewById(R.id.zeroDevice);
        m_zeroDeviceInfo.setVisibility(View.GONE);

        m_bleManager = new BleManager(this);
        m_permissionManager = new PermissionManager(this, m_coordinatorLayout);
        m_progressBar = PopUp.progressBar(MainActivity.this, "Pushing Data to server");
        m_filterSession = new FilterTextSession(this);
        if (Strings.isEmpty(m_filterSession.getFilterText())) {
            m_filterText = "";
        } else {
            m_filterText = m_filterSession.getFilterText();
        }
        m_leDeviceListAdapter = new LeDeviceListAdapter(this);
        m_scanList.setAdapter(m_leDeviceListAdapter);
        m_scanList.setLayoutManager(new LinearLayoutManager(this));


        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(m_bluetoothBroadcastReceiver, filter);

        m_refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanWithPermission();
            }
        });
    }

    private void scanWithPermission() {
        if (m_refreshButton != null) {
            m_refreshButton.setVisibility(View.GONE);
        }
        if (m_zeroDeviceInfo != null) {
            m_zeroDeviceInfo.setVisibility(View.GONE);
        }
        if (m_permissionManager.checkPermission(Constants.PERMISSION_LOCATION_COURSE)) {
            m_swipeRefreshLayout.setRefreshing(true);
            m_leDeviceListAdapter.clear();
            if (m_refreshButton != null) {
                m_refreshButton.setVisibility(View.VISIBLE);
                m_refreshButton.setText("Scanning..");
            }
            m_bleManager.scan(m_scanPeriod, MainActivity.this);
        } else {
            m_swipeRefreshLayout.setRefreshing(false);
            m_permissionManager.requestPermission(1, Constants.PERMISSION_LOCATION_COURSE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        scanWithPermission();
    }

    @Override
    protected void onPause() {
        super.onPause();
        m_bleManager.stopScan(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(m_bluetoothBroadcastReceiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissions.length > 0 && grantResults.length > 0) {
            if (requestCode == 1) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    scanWithPermission();
                } else {
                    m_permissionManager.notifyUser(Formatter.formatPermission(Constants.PERMISSION_LOCATION_COURSE));
                }
            }

        }
    }


    @Override
    public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
        Log.i(TAG, "scan " + bluetoothDevice.getAddress());
        if(Strings.areEqual("PQ",bluetoothDevice.getName())) {
            // iBeacon indicator
                UUID uuid = getGuidFromByteArray(Arrays.copyOfRange(bytes, 9, 25));
                int major = (bytes[25] & 0xff) * 0x100 + (bytes[26] & 0xff);
                int minor = (bytes[27] & 0xff) * 0x100 + (bytes[28] & 0xff);
                byte txpw = bytes[29];
                System.out.println("PQ Major = " + major + " | Minor = " + minor + " TxPw " + (int)txpw + " | UUID = " + uuid.toString());
                TxPower = (int) txpw;
            Rssi = i;
            double d = Math.pow(10d, (((double) (TxPower - Rssi)) / (10 * 2)));
            int dshow = (int) Math.round(d);
            m_leDeviceListAdapter.addDevice(bluetoothDevice, Rssi,dshow);
            ScanDataModel scanDataModel = new ScanDataModel();
            scanDataModel.setScenario_num(scenario);
            scanDataModel.setPoint_num(point);
            scanDataModel.setDevice_name(bluetoothDevice.getName());
            scanDataModel.setRssi(String.valueOf(i));
            scanDataModel.setTx_power(String.valueOf(TxPower));
            Log.e("Details Json: ",gson.toJson(scanDataModel,ScanDataModel.class));
            dataManager.sendPost(scanDataModel);
        }
        else if(Strings.areEqual("PQ1",bluetoothDevice.getName())) {
            // iBeacon indicator
                UUID uuid = getGuidFromByteArray(Arrays.copyOfRange(bytes, 9, 25));
                int major = (bytes[25] & 0xff) * 0x100 + (bytes[26] & 0xff);
                int minor = (bytes[27] & 0xff) * 0x100 + (bytes[28] & 0xff);
                byte txpw = bytes[29];
                System.out.println("PQ1 Major = " + major + " | Minor = " + minor + " TxPw " + (int)txpw + " | UUID = " + uuid.toString());
                TxPower = (int)txpw;

            double d = Math.pow(10d, (((double) (TxPower - Rssi)) / (10 * 2)));
            int dshow = (int) Math.round(d);
            //Log.e("raw data","advertisement data" + new String(bytes, Charset.forName("ISO-8859-1")));
            Log.e("distance","Distance of PQ1 is: " + String.valueOf(d));
            m_leDeviceListAdapter.addDevice(bluetoothDevice, Rssi,dshow);
        }
        else if(Strings.areEqual("1",bluetoothDevice.getName())) {
            // iBeacon indicator
            UUID uuid = getGuidFromByteArray(Arrays.copyOfRange(bytes, 9, 25));
            int major = (bytes[25] & 0xff) * 0x100 + (bytes[26] & 0xff);
            int minor = (bytes[27] & 0xff) * 0x100 + (bytes[28] & 0xff);
            byte txpw = bytes[29];
            System.out.println("PQ1 Major = " + major + " | Minor = " + minor + " TxPw " + (int)txpw + " | UUID = " + uuid.toString());
            TxPower = (int)txpw;

            double d = Math.pow(10d, (((double) (TxPower - Rssi)) / (10 * 2)));
            int dshow = (int) Math.round(d);
            //Log.e("raw data","advertisement data" + new String(bytes, Charset.forName("ISO-8859-1")));
            Log.e("distance","Distance of PQ1 is: " + String.valueOf(d));
            m_leDeviceListAdapter.addDevice(bluetoothDevice, Rssi,dshow);
        }
        else if(Strings.areEqual("2",bluetoothDevice.getName())) {
            // iBeacon indicator
            UUID uuid = getGuidFromByteArray(Arrays.copyOfRange(bytes, 9, 25));
            int major = (bytes[25] & 0xff) * 0x100 + (bytes[26] & 0xff);
            int minor = (bytes[27] & 0xff) * 0x100 + (bytes[28] & 0xff);
            byte txpw = bytes[29];
            System.out.println("PQ1 Major = " + major + " | Minor = " + minor + " TxPw " + (int)txpw + " | UUID = " + uuid.toString());
            TxPower = (int)txpw;

            double d = Math.pow(10d, (((double) (TxPower - Rssi)) / (10 * 2)));
            int dshow = (int) Math.round(d);
            //Log.e("raw data","advertisement data" + new String(bytes, Charset.forName("ISO-8859-1")));
            Log.e("distance","Distance of PQ1 is: " + String.valueOf(d));
            m_leDeviceListAdapter.addDevice(bluetoothDevice, Rssi,dshow);
        }
        else if(Strings.areEqual("3",bluetoothDevice.getName())) {
            // iBeacon indicator
            UUID uuid = getGuidFromByteArray(Arrays.copyOfRange(bytes, 9, 25));
            int major = (bytes[25] & 0xff) * 0x100 + (bytes[26] & 0xff);
            int minor = (bytes[27] & 0xff) * 0x100 + (bytes[28] & 0xff);
            byte txpw = bytes[29];
            System.out.println("PQ1 Major = " + major + " | Minor = " + minor + " TxPw " + (int)txpw + " | UUID = " + uuid.toString());
            TxPower = (int)txpw;

            double d = Math.pow(10d, (((double) (TxPower - Rssi)) / (10 * 2)));
            int dshow = (int) Math.round(d);
            //Log.e("raw data","advertisement data" + new String(bytes, Charset.forName("ISO-8859-1")));
            Log.e("distance","Distance of PQ1 is: " + String.valueOf(d));
            m_leDeviceListAdapter.addDevice(bluetoothDevice, Rssi,dshow);
        }
        else if(Strings.areEqual("4",bluetoothDevice.getName())) {
            // iBeacon indicator
            UUID uuid = getGuidFromByteArray(Arrays.copyOfRange(bytes, 9, 25));
            int major = (bytes[25] & 0xff) * 0x100 + (bytes[26] & 0xff);
            int minor = (bytes[27] & 0xff) * 0x100 + (bytes[28] & 0xff);
            byte txpw = bytes[29];
            System.out.println("PQ1 Major = " + major + " | Minor = " + minor + " TxPw " + (int)txpw + " | UUID = " + uuid.toString());
            TxPower = (int)txpw;

            double d = Math.pow(10d, (((double) (TxPower - Rssi)) / (10 * 2)));
            int dshow = (int) Math.round(d);
            //Log.e("raw data","advertisement data" + new String(bytes, Charset.forName("ISO-8859-1")));
            Log.e("distance","Distance of PQ1 is: " + String.valueOf(d));
            m_leDeviceListAdapter.addDevice(bluetoothDevice, Rssi,dshow);
        }
        else if(Strings.areEqual("5",bluetoothDevice.getName())) {
            // iBeacon indicator
            UUID uuid = getGuidFromByteArray(Arrays.copyOfRange(bytes, 9, 25));
            int major = (bytes[25] & 0xff) * 0x100 + (bytes[26] & 0xff);
            int minor = (bytes[27] & 0xff) * 0x100 + (bytes[28] & 0xff);
            byte txpw = bytes[29];
            System.out.println("PQ1 Major = " + major + " | Minor = " + minor + " TxPw " + (int)txpw + " | UUID = " + uuid.toString());
            TxPower = (int)txpw;

            double d = Math.pow(10d, (((double) (TxPower - Rssi)) / (10 * 2)));
            int dshow = (int) Math.round(d);
            //Log.e("raw data","advertisement data" + new String(bytes, Charset.forName("ISO-8859-1")));
            Log.e("distance","Distance of PQ1 is: " + String.valueOf(d));
            m_leDeviceListAdapter.addDevice(bluetoothDevice, Rssi,dshow);
        }
        else if(Strings.areEqual("6",bluetoothDevice.getName())) {
            // iBeacon indicator
            UUID uuid = getGuidFromByteArray(Arrays.copyOfRange(bytes, 9, 25));
            int major = (bytes[25] & 0xff) * 0x100 + (bytes[26] & 0xff);
            int minor = (bytes[27] & 0xff) * 0x100 + (bytes[28] & 0xff);
            byte txpw = bytes[29];
            System.out.println("PQ1 Major = " + major + " | Minor = " + minor + " TxPw " + (int)txpw + " | UUID = " + uuid.toString());
            TxPower = (int)txpw;

            double d = Math.pow(10d, (((double) (TxPower - Rssi)) / (10 * 2)));
            int dshow = (int) Math.round(d);
            //Log.e("raw data","advertisement data" + new String(bytes, Charset.forName("ISO-8859-1")));
            Log.e("distance","Distance of PQ1 is: " + String.valueOf(d));
            m_leDeviceListAdapter.addDevice(bluetoothDevice, Rssi,dshow);
        }
        else if(Strings.areEqual("7",bluetoothDevice.getName())) {
            // iBeacon indicator
            UUID uuid = getGuidFromByteArray(Arrays.copyOfRange(bytes, 9, 25));
            int major = (bytes[25] & 0xff) * 0x100 + (bytes[26] & 0xff);
            int minor = (bytes[27] & 0xff) * 0x100 + (bytes[28] & 0xff);
            byte txpw = bytes[29];
            System.out.println("PQ1 Major = " + major + " | Minor = " + minor + " TxPw " + (int)txpw + " | UUID = " + uuid.toString());
            TxPower = (int)txpw;

            double d = Math.pow(10d, (((double) (TxPower - Rssi)) / (10 * 2)));
            int dshow = (int) Math.round(d);
            //Log.e("raw data","advertisement data" + new String(bytes, Charset.forName("ISO-8859-1")));
            Log.e("distance","Distance of PQ1 is: " + String.valueOf(d));
            m_leDeviceListAdapter.addDevice(bluetoothDevice, Rssi,dshow);
        }
        else if(Strings.areEqual("8",bluetoothDevice.getName())) {
            // iBeacon indicator
            UUID uuid = getGuidFromByteArray(Arrays.copyOfRange(bytes, 9, 25));
            int major = (bytes[25] & 0xff) * 0x100 + (bytes[26] & 0xff);
            int minor = (bytes[27] & 0xff) * 0x100 + (bytes[28] & 0xff);
            byte txpw = bytes[29];
            System.out.println("PQ1 Major = " + major + " | Minor = " + minor + " TxPw " + (int)txpw + " | UUID = " + uuid.toString());
            TxPower = (int)txpw;

            double d = Math.pow(10d, (((double) (TxPower - Rssi)) / (10 * 2)));
            int dshow = (int) Math.round(d);
            //Log.e("raw data","advertisement data" + new String(bytes, Charset.forName("ISO-8859-1")));
            Log.e("distance","Distance of PQ1 is: " + String.valueOf(d));
            m_leDeviceListAdapter.addDevice(bluetoothDevice, Rssi,dshow);
        }
        else if(Strings.areEqual("9",bluetoothDevice.getName())) {
            // iBeacon indicator
            UUID uuid = getGuidFromByteArray(Arrays.copyOfRange(bytes, 9, 25));
            int major = (bytes[25] & 0xff) * 0x100 + (bytes[26] & 0xff);
            int minor = (bytes[27] & 0xff) * 0x100 + (bytes[28] & 0xff);
            byte txpw = bytes[29];
            System.out.println("PQ1 Major = " + major + " | Minor = " + minor + " TxPw " + (int)txpw + " | UUID = " + uuid.toString());
            TxPower = (int)txpw;

            double d = Math.pow(10d, (((double) (TxPower - Rssi)) / (10 * 2)));
            int dshow = (int) Math.round(d);
            //Log.e("raw data","advertisement data" + new String(bytes, Charset.forName("ISO-8859-1")));
            Log.e("distance","Distance of PQ1 is: " + String.valueOf(d));
            m_leDeviceListAdapter.addDevice(bluetoothDevice, Rssi,dshow);
        }
        else if(Strings.areEqual("10",bluetoothDevice.getName())) {
            // iBeacon indicator
            UUID uuid = getGuidFromByteArray(Arrays.copyOfRange(bytes, 9, 25));
            int major = (bytes[25] & 0xff) * 0x100 + (bytes[26] & 0xff);
            int minor = (bytes[27] & 0xff) * 0x100 + (bytes[28] & 0xff);
            byte txpw = bytes[29];
            System.out.println("PQ1 Major = " + major + " | Minor = " + minor + " TxPw " + (int)txpw + " | UUID = " + uuid.toString());
            TxPower = (int)txpw;

            double d = Math.pow(10d, (((double) (TxPower - Rssi)) / (10 * 2)));
            int dshow = (int) Math.round(d);
            //Log.e("raw data","advertisement data" + new String(bytes, Charset.forName("ISO-8859-1")));
            Log.e("distance","Distance of PQ1 is: " + String.valueOf(d));
            m_leDeviceListAdapter.addDevice(bluetoothDevice, Rssi,dshow);
        }
        else if(Strings.areEqual("11",bluetoothDevice.getName())) {
            // iBeacon indicator
            UUID uuid = getGuidFromByteArray(Arrays.copyOfRange(bytes, 9, 25));
            int major = (bytes[25] & 0xff) * 0x100 + (bytes[26] & 0xff);
            int minor = (bytes[27] & 0xff) * 0x100 + (bytes[28] & 0xff);
            byte txpw = bytes[29];
            System.out.println("PQ1 Major = " + major + " | Minor = " + minor + " TxPw " + (int)txpw + " | UUID = " + uuid.toString());
            TxPower = (int)txpw;

            double d = Math.pow(10d, (((double) (TxPower - Rssi)) / (10 * 2)));
            int dshow = (int) Math.round(d);
            //Log.e("raw data","advertisement data" + new String(bytes, Charset.forName("ISO-8859-1")));
            Log.e("distance","Distance of PQ1 is: " + String.valueOf(d));
            m_leDeviceListAdapter.addDevice(bluetoothDevice, Rssi,dshow);
        }
        else if(Strings.areEqual("12",bluetoothDevice.getName())) {
            // iBeacon indicator
            UUID uuid = getGuidFromByteArray(Arrays.copyOfRange(bytes, 9, 25));
            int major = (bytes[25] & 0xff) * 0x100 + (bytes[26] & 0xff);
            int minor = (bytes[27] & 0xff) * 0x100 + (bytes[28] & 0xff);
            byte txpw = bytes[29];
            System.out.println("PQ1 Major = " + major + " | Minor = " + minor + " TxPw " + (int)txpw + " | UUID = " + uuid.toString());
            TxPower = (int)txpw;

            double d = Math.pow(10d, (((double) (TxPower - Rssi)) / (10 * 2)));
            int dshow = (int) Math.round(d);
            //Log.e("raw data","advertisement data" + new String(bytes, Charset.forName("ISO-8859-1")));
            Log.e("distance","Distance of PQ1 is: " + String.valueOf(d));
            m_leDeviceListAdapter.addDevice(bluetoothDevice, Rssi,dshow);
        }
        else if(Strings.areEqual("13",bluetoothDevice.getName())) {
            // iBeacon indicator
            UUID uuid = getGuidFromByteArray(Arrays.copyOfRange(bytes, 9, 25));
            int major = (bytes[25] & 0xff) * 0x100 + (bytes[26] & 0xff);
            int minor = (bytes[27] & 0xff) * 0x100 + (bytes[28] & 0xff);
            byte txpw = bytes[29];
            System.out.println("PQ1 Major = " + major + " | Minor = " + minor + " TxPw " + (int)txpw + " | UUID = " + uuid.toString());
            TxPower = (int)txpw;

            double d = Math.pow(10d, (((double) (TxPower - Rssi)) / (10 * 2)));
            int dshow = (int) Math.round(d);
            //Log.e("raw data","advertisement data" + new String(bytes, Charset.forName("ISO-8859-1")));
            Log.e("distance","Distance of PQ1 is: " + String.valueOf(d));
            m_leDeviceListAdapter.addDevice(bluetoothDevice, Rssi,dshow);
        }
        else if(Strings.areEqual("14",bluetoothDevice.getName())) {
            // iBeacon indicator
            UUID uuid = getGuidFromByteArray(Arrays.copyOfRange(bytes, 9, 25));
            int major = (bytes[25] & 0xff) * 0x100 + (bytes[26] & 0xff);
            int minor = (bytes[27] & 0xff) * 0x100 + (bytes[28] & 0xff);
            byte txpw = bytes[29];
            System.out.println("PQ1 Major = " + major + " | Minor = " + minor + " TxPw " + (int)txpw + " | UUID = " + uuid.toString());
            TxPower = (int)txpw;

            double d = Math.pow(10d, (((double) (TxPower - Rssi)) / (10 * 2)));
            int dshow = (int) Math.round(d);
            //Log.e("raw data","advertisement data" + new String(bytes, Charset.forName("ISO-8859-1")));
            Log.e("distance","Distance of PQ1 is: " + String.valueOf(d));
            m_leDeviceListAdapter.addDevice(bluetoothDevice, Rssi,dshow);
        }
        else if(Strings.areEqual("15",bluetoothDevice.getName())) {
            // iBeacon indicator
            UUID uuid = getGuidFromByteArray(Arrays.copyOfRange(bytes, 9, 25));
            int major = (bytes[25] & 0xff) * 0x100 + (bytes[26] & 0xff);
            int minor = (bytes[27] & 0xff) * 0x100 + (bytes[28] & 0xff);
            byte txpw = bytes[29];
            System.out.println("PQ1 Major = " + major + " | Minor = " + minor + " TxPw " + (int)txpw + " | UUID = " + uuid.toString());
            TxPower = (int)txpw;

            double d = Math.pow(10d, (((double) (TxPower - Rssi)) / (10 * 2)));
            int dshow = (int) Math.round(d);
            //Log.e("raw data","advertisement data" + new String(bytes, Charset.forName("ISO-8859-1")));
            Log.e("distance","Distance of PQ1 is: " + String.valueOf(d));
            m_leDeviceListAdapter.addDevice(bluetoothDevice, Rssi,dshow);
        }
        else if(Strings.areEqual("16",bluetoothDevice.getName())) {
            // iBeacon indicator
            UUID uuid = getGuidFromByteArray(Arrays.copyOfRange(bytes, 9, 25));
            int major = (bytes[25] & 0xff) * 0x100 + (bytes[26] & 0xff);
            int minor = (bytes[27] & 0xff) * 0x100 + (bytes[28] & 0xff);
            byte txpw = bytes[29];
            System.out.println("PQ1 Major = " + major + " | Minor = " + minor + " TxPw " + (int)txpw + " | UUID = " + uuid.toString());
            TxPower = (int)txpw;

            double d = Math.pow(10d, (((double) (TxPower - Rssi)) / (10 * 2)));
            int dshow = (int) Math.round(d);
            //Log.e("raw data","advertisement data" + new String(bytes, Charset.forName("ISO-8859-1")));
            Log.e("distance","Distance of PQ1 is: " + String.valueOf(d));
            m_leDeviceListAdapter.addDevice(bluetoothDevice, Rssi,dshow);
        }

    }

    @Override
    public void onRefresh() {
        scanWithPermission();
    }

    @Override
    public void onScanFinish() {
        m_swipeRefreshLayout.setRefreshing(false);

        if (m_refreshButton != null) {
            m_refreshButton.setText("Scan");
        }
        int itemCount = m_leDeviceListAdapter.getItemCount();
        if (itemCount == 0 && m_zeroDeviceInfo != null) {
            String notfound = "";
            if (Strings.isEmpty(m_filterText)) {
                notfound = "Devices Not Found. Try Again!!";
            } else {
                notfound = "Devices Not Found. Try Again!! Current Filter : " + m_filterText;
            }
            m_zeroDeviceInfo.setVisibility(View.VISIBLE);
            m_zeroDeviceInfo.setText(Html.fromHtml(notfound));
        }
    }

    private void requestBluetooth() {
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager == null) {
            PopUp.pop(this, "Bluetooth not supported", "");
        } else {
            BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            } else {
                //Do Nothing
            }
        }

    }

    private final BroadcastReceiver m_bluetoothBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.i(TAG, "Bluetooth off");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.i(TAG, "Turning Bluetooth off...");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.i(TAG, "Bluetooth on");
                        scanWithPermission();
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.i(TAG, "Turning Bluetooth on...");
                        break;
                }
            }
        }
    };

    private class DataLogger extends AsyncTask<String, Integer, Integer> {

        @Override
        protected Integer doInBackground(String... strings) {
            String data = strings[0];
            Socket socket = null;
            Log.i(TAG, "starting data write");
            try {
                socket = new Socket("35.163.146.207", 5044);
                if (socket.isConnected()) {
                    PrintWriter out = new PrintWriter(new BufferedWriter(
                            new OutputStreamWriter(socket.getOutputStream())), true);
                    out.println(data);
                    out.close();
                    socket.close();
                    return 1;
                } else {
                    Log.i(TAG, "Socket is not connected");
                    return 0;
                }

            } catch (Throwable throwable) {
                Log.i(TAG, "error " + throwable.getLocalizedMessage());
                return 0;
            }
        }

        @Override
        protected void onPostExecute(Integer data) {
            m_progressBar.dismiss();
            switch (data) {
                case 0:
                    PopUp.pop(MainActivity.this, "Failed", "Please check your data connection.");
                    break;
                case 1:
                    PopUp.pop(MainActivity.this, "Success", "Data Pushed to server.", "OK", "", "", new PopUp.Callback() {
                        @Override
                        protected void onPosiveClick() {
                            finish();
                        }
                    });
            }
        }
    }

    public static String bytesToHexString(byte[] bytes) {
        StringBuilder buffer = new StringBuilder();
        for(int i=0; i<bytes.length; i++) {
            buffer.append(String.format("%02x", bytes[i]));
        }
        return buffer.toString();
    }
    public static UUID getGuidFromByteArray(byte[] bytes)
    {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        UUID uuid = new UUID(bb.getLong(), bb.getLong());
        return uuid;
    }
}

