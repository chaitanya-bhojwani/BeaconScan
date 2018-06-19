package com.example.affine.beaconscan;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class LeDeviceListAdapter extends RecyclerView.Adapter<LeDeviceListAdapter.ViewHolder> {
    private ArrayList<BluetoothDevice> mLeDevices;
    private ArrayList<DeviceInfo> m_deviceList;
    private LayoutInflater mInflator;

    public LeDeviceListAdapter(Context context) {
        super();
        mLeDevices = new ArrayList<BluetoothDevice>();
        m_deviceList = new ArrayList<DeviceInfo>();
        mInflator = LayoutInflater.from(context);
    }


    public ArrayList<DeviceInfo> getDeviceList() {
        return m_deviceList;
    }

    public void addDevice(BluetoothDevice device, int strength, int distance) {
            Log.e("List","Not Contains");
            mLeDevices.add(device);
            DeviceInfo deviceInfo = new DeviceInfo();
            if (Strings.isEmpty(device.getName())) {
                deviceInfo.setDeviceName("Unknown");
            } else {
                deviceInfo.setDeviceName(device.getName());
            }
            deviceInfo.setMacAddr(device.getAddress());
            deviceInfo.setStrength(strength);
            deviceInfo.setDistance(distance);
            m_deviceList.add(deviceInfo);
            notifyDataSetChanged();
    }

    public BluetoothDevice getDevice(int position) {
        return mLeDevices.get(position);
    }

    public void clear() {
        mLeDevices.clear();
        m_deviceList.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflator.inflate(R.layout.device_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DeviceInfo device = m_deviceList.get(position);
        if (Strings.isEmpty(device.getDeviceName())) {
            holder.deviceName.setText("Unknown");
        } else {
            holder.deviceName.setText(device.getDeviceName());
        }
        holder.deviceID.setText(device.getMacAddr());
        holder.deviceStrength.setText(device.getStrength().toString());
        holder.deviceDistance.setText(String.valueOf(device.getDistance()));
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return mLeDevices.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView deviceName;
        TextView deviceID;
        TextView deviceStrength;
        TextView deviceDistance;

        ViewHolder(View itemView) {
            super(itemView);
            deviceName = itemView.findViewById(R.id.deviceName);
            deviceID = itemView.findViewById(R.id.deviceId);
            deviceStrength = itemView.findViewById(R.id.strength);
            deviceDistance = itemView.findViewById(R.id.distance);
        }
    }
}
