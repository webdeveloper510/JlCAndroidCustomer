package com.example.jlccustomer.Retrofit;

import com.example.jlccustomer.Model.Login;
import com.example.jlccustomer.Model.ModelBookRide;
import com.example.jlccustomer.Model.ModelCancelRide;
import com.example.jlccustomer.Model.ModelCustomerFare;
import com.example.jlccustomer.Model.ModelDriverRating;
import com.example.jlccustomer.Model.ModelGetCustomerProfile;
import com.example.jlccustomer.Model.ModelGetDriverLocation;
import com.example.jlccustomer.Model.ModelNotifiyDriver;
import com.example.jlccustomer.Model.ModelUpdateCustomer;
import com.example.jlccustomer.Model.RegisterStageOne;
import com.example.jlccustomer.Model.ResetPassword;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface APIInterface {



    @FormUrlEncoded
    @POST("Customerapi/getCustomerinfo")
    Call<ModelGetCustomerProfile> getCustomerProfile(@Field("customer_id") String customer_id);


    @FormUrlEncoded
    @POST("Customerapi/profilecustomerEdit")
    Call<ModelUpdateCustomer> updateCustomerProfile(@Field("customer_id") String customer_id, @Field("name") String name, @Field("contact") String contact, @Field("email") String email);


    @FormUrlEncoded
    @POST("Customerapi/customerLogin")
    Call<Login> login(@Field("email") String email, @Field("password") String password, @Field("token") String token);

    @FormUrlEncoded
    @POST("Customerapi/customerFare")
    Call<ModelCustomerFare> hitGetRideFare(@Field("customer_id") String customer_id, @Field("source_lat") String source_lat, @Field("source_long") String source_long, @Field("dest_lat") String dest_lat, @Field("dest_long") String dest_long);


    @FormUrlEncoded
    @POST("Api/driverRating")
    Call<ModelDriverRating> hitDriverRating(@Field("ride_id") String ride_id,@Field("driver_review") String driver_review,@Field("driver_rating") String driver_rating);



    @FormUrlEncoded
    @POST("Api/customerCancelride")
    Call<ModelCancelRide> hitCancelRide(@Field("ride_id") String ride_id);


    @FormUrlEncoded
    @POST("Customerapi/getDriverlatlong")
    Call<ModelGetDriverLocation> hitGetDriverLocation(@Field("driver_id") String driver_id);


    @FormUrlEncoded
    @POST("Customerapi/notifyDriver")
    Call<ModelNotifiyDriver> hitDriverNotifyApi(@Field("ride_id") String ride_id,@Field("customer_id") String customer_id,@Field("driver_id") String driver_id);



    @FormUrlEncoded
    @POST("Customerapi/bookingRide")
    Call<ModelBookRide> hitBookRideApi(@Field("customer_id") String customer_id, @Field("source_name") String source_name, @Field("source_long") String source_long, @Field("source_lat") String source_lat, @Field("dest_lat") String dest_lat, @Field("dest_long") String dest_long, @Field("dest_name") String dest_name, @Field("car_type") int car_type, @Field("payment_mode") String payment_mode);

    @FormUrlEncoded
    @POST("Customerapi/resetPassword")
    Call<Login> forgotPass(@Field("email") String email);


    @FormUrlEncoded
    @POST("Customerapi/editcustomerPassword")
    Call<ResetPassword> resetPass(@Field("customer_id") String customer_id, @Field("old_password") String old_password, @Field("new_password") String new_password);

    @Multipart
    @POST("Customerapi/editCustomerimage")
    Call<ResetPassword> editProfile(@Part("customer_id") RequestBody customer_id, @Part MultipartBody.Part customer_image);


    @Multipart
    @POST("Customerapi/customerSignup")
    Call<RegisterStageOne> register(@Part MultipartBody.Part customer_image, @Part("fname") RequestBody fname,
                                    @Part("lname") RequestBody lname, @Part("password") RequestBody password, @Part("email") RequestBody email,
                                    @Part("contact") RequestBody contact);

//    @Multipart
//    @POST("driverStages")
//    Call<RegisterStagesTwo> registerTwo(@Part("driver_id") RequestBody driver_id, @Part("stage_id") RequestBody stage_id, @Part MultipartBody.Part driver_license_front, @Part MultipartBody.Part driver_license_back,
//                                        @Part MultipartBody.Part work_eligibility_front, @Part MultipartBody.Part work_eligibility_back, @Part MultipartBody.Part police_record_front, @Part MultipartBody.Part police_record_back);
//
//
//    @Multipart
//    @POST("driverStages")
//    Call<RegisterStagesTwo> registerThree(@Part("driver_id") RequestBody driver_id, @Part("stage_id") RequestBody stage_id, @Part MultipartBody.Part vehicle_insurance_front, @Part MultipartBody.Part vehicle_insurance_back,
//                                          @Part MultipartBody.Part vehicle_registration_front, @Part MultipartBody.Part vehicle_registration_back, @Part MultipartBody.Part safety_certificate_front, @Part MultipartBody.Part safety_certificate_back);
//
//
//    @Multipart
//    @POST("driverStages")
//    Call<RegisterStagesTwo> registerFour(@Part("driver_id") RequestBody driver_id, @Part("stage_id") RequestBody stage_id, @Part MultipartBody.Part vehicle_picture, @Part("vehicle_plate_number") RequestBody vehicle_plate_number, @Part("vehicle_model") RequestBody vehicle_model,
//                                         @Part("vehicle_year") RequestBody vehicle_year, @Part("vehicle_color") RequestBody vehicle_color, @Part("vehicle_brand") RequestBody vehicle_brand, @Part("vehicle_seats") RequestBody vehicle_seats);


}
