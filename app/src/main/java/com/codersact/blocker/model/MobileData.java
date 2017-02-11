package com.codersact.blocker.model;

/**
 * Created by masum on 30/07/2015.
 */
public class MobileData {
    private String smsNo;
    private String smsString;
    private String smsAddress;
    private String smsId;

    public String getSmsThreadNo() {
        return smsNo;
    }

    public void setCallerName(String smsNo) {
        this.smsNo = smsNo;
    }

    public String getCreatedTime() {
        return smsString;
    }

    public void setCreatedTime(String smsString) {
        this.smsString = smsString;
    }

    public String getMobileNumber() {
        return smsAddress;
    }

    public void setMobileNumber(String smsAddress) {
        this.smsAddress = smsAddress;
    }

    public String getRowId() {
        return smsId;
    }

    public void setRowId(String smsId) {
        this.smsId = smsId;
    }
}
