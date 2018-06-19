package com.example.affine.beaconscan;


import java.util.List;

public class ScanData {

    private List<DeviceInfo> devices = Lists.newArrayList();
    private String deviceId;
    private String seat;

    public List<DeviceInfo> getDevices() {
        return devices;
    }

    public void setDevices(List<DeviceInfo> devices) {
        this.devices = devices;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getSeat() {
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }

}
