package com.example.changednsapp;

public class UnitData {
    private String unitNo;
    private String mobileNo;
    private String changeDNSCommand;
    private String deviceQueryCmd;
    private String confirmedValue;

    public UnitData(String unitNo, String mobileNo, String changeDNSCommand, String deviceQueryCmd, String confirmedValue) {
        this.unitNo = unitNo;
        this.mobileNo = mobileNo;
        this.changeDNSCommand = changeDNSCommand;
        this.deviceQueryCmd = deviceQueryCmd;
        this.confirmedValue = confirmedValue;
    }

    public String isConfirmedValue() {
        return confirmedValue;
    }

    public String getUnitNo() {
        return unitNo;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public String getChangeDnsCmd() {
        return changeDNSCommand;
    }

    public String getDeviceQueryCmd() {
        return deviceQueryCmd;
    }

}
