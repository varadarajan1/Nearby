package com.nearby.indoorpositioning;

public class BeaconDataWithSignalStrength extends BeaconData {

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    private int rssi;
}
