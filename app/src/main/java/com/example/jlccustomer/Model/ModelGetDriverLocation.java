package com.example.jlccustomer.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ModelGetDriverLocation {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("data")
    @Expose
    private Data data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {

        @SerializedName("driver_lat")
        @Expose
        private String driverLat;
        @SerializedName("driver_long")
        @Expose
        private String driverLong;

        public String getDriverLat() {
            return driverLat;
        }

        public void setDriverLat(String driverLat) {
            this.driverLat = driverLat;
        }

        public String getDriverLong() {
            return driverLong;
        }

        public void setDriverLong(String driverLong) {
            this.driverLong = driverLong;
        }

    }

}
