package com.example.jlccustomer.Activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jlccustomer.Model.Login;
import com.example.jlccustomer.R;
import com.example.jlccustomer.Retrofit.APIClient;
import com.example.jlccustomer.Retrofit.APIInterface;
import com.example.jlccustomer.Utils.ConnectionDetector;
import com.example.jlccustomer.Utils.CsPrefrences;
import com.example.jlccustomer.Utils.SessionManager;
import com.example.jlccustomer.Utils.Utilss;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


import retrofit2.Call;
import retrofit2.Callback;

public class LoginActivity extends AppCompatActivity {

    AlertDialog alertDialogs;
    Button btnCancel, btnOk, btnLogin, btnRegister;
    TextView tvforgotPassword;
    Context context;
    String strForgotEmail;
    Dialog dialog;
    String token;
    APIInterface apiInterface;
    SessionManager session;
    String stremail, strPass;
    EditText edEmail, edPassord;
    CheckBox chRemember;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private Boolean saveLogin;
    LinearLayout background;
    EditText forgotEmail;
    CsPrefrences csPrefrences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;
        csPrefrences = new CsPrefrences(context);
        tvforgotPassword = findViewById(R.id.forgetPassword);
        btnLogin = findViewById(R.id.bt_login);
        btnRegister = findViewById(R.id.btn_Register);
        chRemember = (CheckBox) findViewById(R.id.saveLoginCheckBox);
        edEmail = (EditText) findViewById(R.id.et_Email);
        edPassord = (EditText) findViewById(R.id.et_Password);
        session = new SessionManager(getApplicationContext());
        background = findViewById(R.id.background);


        try {

            FirebaseInstanceId iid = FirebaseInstanceId.getInstance();
            token = iid.getToken();

            while (token == null) {
                iid = FirebaseInstanceId.getInstance();
                token = iid.getToken();
            }

            Log.d("tokenhh", token);


        } catch (Exception ex) {

            ex.printStackTrace();
        }


        //  hitText();
        if (session.isLoggedIn()) {
            // User is already logged in.Take him to main activity
            try {

                Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
                LoginActivity.this.startActivity(loginIntent);
                LoginActivity.this.finish();


            } catch (Exception ex) {

                ex.printStackTrace();
            }
        } else {


        }

        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();
        saveLogin = loginPreferences.getBoolean("saveLogin", false);

        if (saveLogin == true) {

            edEmail.setText(loginPreferences.getString("username", ""));
            edPassord.setText(loginPreferences.getString("password", ""));
            chRemember.setChecked(true);
        }

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);

            }
        });


        tvforgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ForgotDialog(context);
            }
        });


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                stremail = edEmail.getText().toString();
                strPass = edPassord.getText().toString();


                if (TextUtils.isEmpty(stremail)) {

                    edEmail.requestFocus();
                    edEmail.setError("Enter Email");
                    return;
                }

                if (!isValidEmail(stremail)) {

                    edEmail.requestFocus();
                    edEmail.setError("Enter Valid Email");
                    return;
                }
                if (TextUtils.isEmpty(strPass)) {

                    edPassord.requestFocus();
                    edPassord.setError("Enter Password");
                    return;
                }

                if (chRemember.isChecked()) {

                    loginPrefsEditor.putBoolean("saveLogin", true);
                    loginPrefsEditor.putString("username", stremail);
                    loginPrefsEditor.putString("password", strPass);
                    loginPrefsEditor.commit();
                } else {

                    loginPrefsEditor.clear();
                    loginPrefsEditor.commit();
                }

                if (new ConnectionDetector(LoginActivity.this).isConnectingToInternet()) {

                    hitLogin(stremail, strPass, token);
                } else {

                    Toast.makeText(LoginActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    private void ForgotDialog(final Context context) {
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.forget_dialog, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setView(dialogView);
        alertDialogs = builder.create();
        alertDialogs.show();

        forgotEmail = (EditText) alertDialogs.findViewById(R.id.forgotEmail);
        btnCancel = (Button) alertDialogs.findViewById(R.id.bt_cancel);
        btnOk = (Button) alertDialogs.findViewById(R.id.bt_ok);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                strForgotEmail = forgotEmail.getText().toString();


                if (TextUtils.isEmpty(strForgotEmail)) {
                    forgotEmail.requestFocus();
                    forgotEmail.setError("Enter Email Address");
                    return;
                }

                if (!isValidEmail(strForgotEmail)) {
                    forgotEmail.requestFocus();
                    forgotEmail.setError("Enter Valid Email");
                    return;
                }


                if (new ConnectionDetector(LoginActivity.this).isConnectingToInternet()) {

                    hitForgot(strForgotEmail);

                } else {

                    Toast.makeText(LoginActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }


            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialogs.dismiss();
            }
        });


    }


    private void hitForgot(String strForgotEmail) {

        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
        dialog = Utilss.progressDialog(context);
        dialog.show();

        apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<Login> call = apiInterface.forgotPass(strForgotEmail);

        call.enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, retrofit2.Response<Login> response) {

                dialog.hide();

                if (response.isSuccessful()) {
                    if (response.body().getCode().equals(201)) {

//                        Log.d("response",response.body().getId());
//                        String Id= response.body().getResult().getId();
//
//                        //String Id= response.body().getId();
//                        Log.d("Id", Id);
//
////                        String result=  response.body().getResult();
////                        result.replaceAll("\\/", "");
////                        Log.d("url", result);
//
//                        SharedPreferences sharedpreferences = getSharedPreferences("sharedPrefName", 0);
//                        SharedPreferences.Editor editor = sharedpreferences.edit();
//                        editor.putString("Key_Login_Id",Id);
//                        editor.commit();
//
////                        if(stage == 0){
////
//                        session.setLogin(true);

                        alertDialogs.dismiss();

                        Toast.makeText(LoginActivity.this, "Please check your email", Toast.LENGTH_SHORT).show();
                        //Intent i = new Intent(LoginActivity.this,MainActivity.class);
                        //  startActivity(i);
//                        }
//                        else if(stage == 2){
//
//                            Intent i = new Intent(LoginActivity.this,RegisterStageTwo.class);
//                            startActivity(i);
//
//                        }
//                        else if(stage == 3){
//
//                            Intent i = new Intent(LoginActivity.this,RegisterStageThree.class);
//                            startActivity(i);
//
//                        }
//                        else if(stage == 4){
//
//                            Intent i = new Intent(LoginActivity.this,RegisterStageFour.class);
//                            startActivity(i);
//                        }
//                        else{
//
//
//                        }

                    } else {

                        String text = response.body().getText();

                        Toast.makeText(LoginActivity.this, text, Toast.LENGTH_SHORT).show();

                        //  Utilss.makeSnackBar(LoginActivity.this, background, text);

                    }
                }


            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {

                dialog.hide();
                Toast.makeText(context, "Server Error", Toast.LENGTH_SHORT).show();
                // Toast.makeText(context,"SERVER ERROR",Toast.LENGTH_LONG).show();
                // Log.d("listData", t.getMessage() + "Failure");

            }
        });

    }

    private void hitLogin(String name, String pwd, String token) {

        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
        dialog = Utilss.progressDialog(context);
        dialog.show();

        apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<Login> call = apiInterface.login(name, pwd, token);

        call.enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, retrofit2.Response<Login> response) {

                dialog.hide();

                if (response.isSuccessful()) {
                    if (response.body().getCode().equals(201)) {
                        String Id = response.body().getResult().getData().getId();
                        String fname = response.body().getResult().getData().getFname();
                        String email = response.body().getResult().getData().getEmail();
                        String contact = response.body().getResult().getData().getContact();
                        csPrefrences.setSharedPref("customerId", Id);
                        csPrefrences.setSharedPref("customerName", fname);
                        csPrefrences.setSharedPref("customerPhone", contact);
                        csPrefrences.setSharedPref("customerEmail", email);
                        csPrefrences.setSharedPref("customerRate", String.valueOf(response.body().getResult().getRate()));
                        csPrefrences.setSharedPref("customerImage", response.body().getResult().getData().getCustomerImage());
                        Log.d("customerNameLogin", fname);

                        SharedPreferences sharedpreferences = getSharedPreferences("sharedPrefName", 0);
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("Key_Login_Id", Id);
                        editor.putString("Key_Login_FNAME", fname);
                        editor.putString("Key_Login_EMAIL", email);
                        editor.putString("Key_Login_CONTACT", contact);

                        editor.commit();

//                        if(stage == 0){
//
                        session.setLogin(true);
                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(i);
//                        }
//                        else if(stage == 2){
//
//                            Intent i = new Intent(LoginActivity.this,RegisterStageTwo.class);
//                            startActivity(i);
//
//                        }
//                        else if(stage == 3){
//
//                            Intent i = new Intent(LoginActivity.this,RegisterStageThree.class);
//                            startActivity(i);
//
//                        }
//                        else if(stage == 4){
//
//                            Intent i = new Intent(LoginActivity.this,RegisterStageFour.class);
//                            startActivity(i);
//                        }
//                        else{
//
//
//                        }

                    } else {

                        String text = response.body().getText();
                        Toast.makeText(LoginActivity.this, text, Toast.LENGTH_SHORT).show();

                        //  Utilss.makeSnackBar(LoginActivity.this, background, text);

                    }
                }


            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {

                dialog.hide();
                Toast.makeText(context, "Server Error", Toast.LENGTH_SHORT).show();
                // Toast.makeText(context,"SERVER ERROR",Toast.LENGTH_LONG).show();
                // Log.d("listData", t.getMessage() + "Failure");

            }
        });

    }

    private boolean isValidEmail(String str_email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(str_email);
        return matcher.matches();
    }

}
