package com.example.affine.beaconscan;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;

/**
 * Created by VikashPatel on 28/11/17.
 */

public class BleManager extends BluetoothGattCallback {
    private final String TAG = BleManager.class.getSimpleName();
    private Context m_context;
    private BluetoothAdapter m_bluetoothAdapter;

    public BleManager(Context context) {
        m_context = context;
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        m_bluetoothAdapter = bluetoothManager.getAdapter();
    }


    public void scan(long scanPeriod, final LeScanCallback callback) {
        if (m_bluetoothAdapter == null) {
            PopUp.pop(m_context, "Bluetooth not supported", "");
            return;
        }
        if (!m_bluetoothAdapter.isEnabled()) {
            m_bluetoothAdapter.enable();
            return;
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                callback.onScanFinish();
                m_bluetoothAdapter.stopLeScan(callback);
            }
        }, scanPeriod);
        m_bluetoothAdapter.startLeScan(callback);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void scan(long scanPeriod, final ScanCallback callback) {
        if (m_bluetoothAdapter == null) {
            PopUp.pop(m_context, "Bluetooth not supported", "");
            return;
        }
        if (!m_bluetoothAdapter.isEnabled()) {
            m_bluetoothAdapter.enable();
            return;
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                m_bluetoothAdapter.getBluetoothLeScanner().stopScan(callback);
                if (m_bluetoothAdapter.isEnabled()) {
                    m_bluetoothAdapter.disable();
                }
            }
        }, scanPeriod);

        m_bluetoothAdapter.getBluetoothLeScanner().startScan(callback);

    }

    public void stopScan(BluetoothAdapter.LeScanCallback callback) {
        if (m_bluetoothAdapter == null) {
            return;
        }
        m_bluetoothAdapter.stopLeScan(callback);
    }

    public interface LeScanCallback extends BluetoothAdapter.LeScanCallback {
        void onScanFinish();
    }

}

