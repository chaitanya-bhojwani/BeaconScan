package com.example.affine.beaconscan;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ScanDataModel {
    @SerializedName("scenario_num")
    @Expose
    private String scenario_num;
    @SerializedName("point_num")
    @Expose
    private String point_num;
    @SerializedName("device_name")
    @Expose
    private String device_name;
    @SerializedName("rssi")
    @Expose
    private String rssi;
    @SerializedName("tx_power")
    @Expose
    private String tx_power;

    public String getScenario_num() {
        return scenario_num;
    }

    public void setScenario_num(String scenario_num) {
        this.scenario_num = scenario_num;
    }

    public String getPoint_num() {
        return point_num;
    }

    public void setPoint_num(String point_num) {
        this.point_num = point_num;
    }

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public String getRssi() {
        return rssi;
    }

    public void setRssi(String rssi) {
        this.rssi = rssi;
    }

    public String getTx_power() {
        return tx_power;
    }

    public void setTx_power(String tx_power) {
        this.tx_power = tx_power;
    }
}
