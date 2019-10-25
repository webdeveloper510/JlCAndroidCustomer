package com.example.jlccustomer.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Login {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("result")
    @Expose
    private Result result;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }
    public class Result {

        @SerializedName("data")
        @Expose
        private Data data;
        @SerializedName("rate")
        @Expose
        private Double rate;

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }

        public Double getRate() {
            return rate;
        }

        public void setRate(Double rate) {
            this.rate = rate;
            }

}
    public class Data {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("fname")
        @Expose
        private String fname;
        @SerializedName("lname")
        @Expose
        private String lname;
        @SerializedName("password")
        @Expose
        private String password;
        @SerializedName("email")
        @Expose
        private String email;
        @SerializedName("contact")
        @Expose
        private String contact;
        @SerializedName("customer_image")
        @Expose
        private String customerImage;
        @SerializedName("token")
        @Expose
        private String token;
        @SerializedName("emailverification")
        @Expose
        private String emailverification;
        @SerializedName("pendingpayment")
        @Expose
        private String pendingpayment;
        @SerializedName("cash")
        @Expose
        private String cash;
        @SerializedName("rating")
        @Expose
        private String rating;
        @SerializedName("ridecount")
        @Expose
        private String ridecount;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getFname() {
            return fname;
        }

        public void setFname(String fname) {
            this.fname = fname;
        }

        public String getLname() {
            return lname;
        }

        public void setLname(String lname) {
            this.lname = lname;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getContact() {
            return contact;
        }

        public void setContact(String contact) {
            this.contact = contact;
        }

        public String getCustomerImage() {
            return customerImage;
        }

        public void setCustomerImage(String customerImage) {
            this.customerImage = customerImage;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getEmailverification() {
            return emailverification;
        }

        public void setEmailverification(String emailverification) {
            this.emailverification = emailverification;
        }

        public String getPendingpayment() {
            return pendingpayment;
        }

        public void setPendingpayment(String pendingpayment) {
            this.pendingpayment = pendingpayment;
        }

        public String getCash() {
            return cash;
        }

        public void setCash(String cash) {
            this.cash = cash;
        }

        public String getRating() {
            return rating;
        }

        public void setRating(String rating) {
            this.rating = rating;
        }

        public String getRidecount() {
            return ridecount;
        }

        public void setRidecount(String ridecount) {
            this.ridecount = ridecount;
        }

    }

}