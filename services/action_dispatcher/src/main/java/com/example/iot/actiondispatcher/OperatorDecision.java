package com.example.iot.actiondispatcher;

public class OperatorDecision {

    private String alert_id;
    private String sensor_id;
    private String action;

    public OperatorDecision() {
    }

    public String getAlert_id() {
        return alert_id;
    }

    public void setAlert_id(String alert_id) {
        this.alert_id = alert_id;
    }

    public String getSensor_id() {
        return sensor_id;
    }

    public void setSensor_id(String sensor_id) {
        this.sensor_id = sensor_id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
