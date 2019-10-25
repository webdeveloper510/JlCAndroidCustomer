package com.example.jlccustomer.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ModelCustomerFare {

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

        @SerializedName("go")
        @Expose
        private Double go;
        @SerializedName("xl")
        @Expose
        private Double xl;
        @SerializedName("pre")
        @Expose
        private Double pre;

        public Double getGo() {
            return go;
        }

        public void setGo(Double go) {
            this.go = go;
        }

        public Double getXl() {
            return xl;
        }

        public void setXl(Double xl) {
            this.xl = xl;
        }

        public Double getPre() {
            return pre;
        }

        public void setPre(Double pre) {
            this.pre = pre;
        }

    }
}
