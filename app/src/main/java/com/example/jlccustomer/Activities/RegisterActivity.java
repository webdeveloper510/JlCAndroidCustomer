package com.example.jlccustomer.Activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.jlccustomer.Model.Country;
import com.example.jlccustomer.Model.RegisterStageOne;
import com.example.jlccustomer.Model.State;
import com.example.jlccustomer.R;
import com.example.jlccustomer.Retrofit.APIClient;
import com.example.jlccustomer.Retrofit.APIInterface;
import com.example.jlccustomer.Utils.ConnectionDetector;
import com.example.jlccustomer.Utils.CustomFunction;
import com.example.jlccustomer.Utils.Utilss;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

public class RegisterActivity extends AppCompatActivity {

    Context context;
    Dialog dialog;
    TextView tvUploadPhoto;
    AutoCompleteTextView country,state,city;
    String country_id,state_id,city_id,strCode;
    public static String stInsurancePath;
    private File insuranceImageCompressed;
    private File insuranceImage;
    private int insuranceImageId = 1;
    Bitmap original, bp;
    private static int RESULT_INSURANCE_IMAGE = 1;
    public static String path,pass,strCountry,strState,strCity;
    public static String stRadioValue;
    CircleImageView ivphoto;
    Button btnRegister;
    APIInterface apiInterface;
    public EditText SgFirstName, SgLastName, SgEmail, SgMobile, SgAddress, SgPassword, SgConPassword,SgDob;
    private RadioGroup SgRadioGroup;
    private RadioButton SgRadioButton;
    public static String stFirstName, stLastName, stEmailId, stDob, stMobileNumber, stAddress, stPassword;
    private static final int EXTERNAL_STORAGE_PERMISSION_CONSTANT = 100;
    LinearLayout background;
    private static final int STORAGE_PERMISSION_CODE = 123;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        context = this;

     /*   try {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(true);
            }
            getSupportActionBar().setTitle("Register");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        }catch (Exception ex){

            ex.printStackTrace();
        }*/

        tvUploadPhoto = findViewById(R.id.tvUploadPhoto);
        SgFirstName = findViewById(R.id.edfirstname);
        SgLastName = findViewById(R.id.edlastname);
        SgEmail = findViewById(R.id.edemail);
        SgMobile = findViewById(R.id.edPhone);
        SgPassword = findViewById(R.id.edPassword);
        SgConPassword = findViewById(R.id.edConfimPassword);
        background = findViewById(R.id.background);
        ivphoto = findViewById(R.id.ivphoto);
        btnRegister = findViewById(R.id.btn_Register);
        btnRegister = findViewById(R.id.btnRegister);


        tvUploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestStoragePermission();
                } else {

                    Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, RESULT_INSURANCE_IMAGE);


                }
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                stFirstName = SgFirstName.getText().toString();
                Log.d("FirstName", stFirstName);
                stLastName = SgLastName.getText().toString();
                Log.d("LastName", stLastName);
                stEmailId = SgEmail.getText().toString();
                Log.d("EmailID", stEmailId);
                stMobileNumber = SgMobile.getText().toString();
                pass = SgPassword.getText().toString();
                String conpass = SgConPassword.getText().toString();

                if (TextUtils.isEmpty(stInsurancePath)) {

                    Toast.makeText(RegisterActivity.this, "Upload Profile Pic", Toast.LENGTH_SHORT).show();

                }
                else if (SgFirstName.getText().toString().trim().length() == 0) {
                    SgFirstName.setError("Enter First Name");
                    CustomFunction.setFocus(SgFirstName);

                }
                else if (SgFirstName.getText().toString().matches(".*\\d+.*")) {
                    SgFirstName.setError("Name must contain alphabets");
                    CustomFunction.setFocus(SgFirstName);


                } else if (SgLastName.getText().toString().matches(".*\\d+.*")) {
                    SgLastName.setError("Name must contain alphabets");
                    CustomFunction.setFocus(SgFirstName);


                } else if (!Patterns.EMAIL_ADDRESS.matcher(SgEmail.getText().toString().trim()).matches()) {

                    SgEmail.setError("Invalid Email");
                    CustomFunction.setFocus(SgEmail);

                }

                else if (SgMobile.getText().toString().trim().length() == 0) {
                    SgMobile.setError("Enter Number");
                    CustomFunction.setFocus(SgMobile);

                } else if (SgMobile.getText().toString().length() < 10) {

                    SgMobile.setError("Invalid Number");
                    CustomFunction.setFocus(SgMobile);

                } else if (!TextUtils.isDigitsOnly(SgMobile.getText().toString().trim())) {

                    SgMobile.setError("Invalid Number");
                    CustomFunction.setFocus(SgMobile);

                } else if (pass.length() == 0) {

                    SgPassword.setError("Enter Password");
                    CustomFunction.setFocus(SgPassword);

                } else if (pass.length() < 6) {

                    SgPassword.setError("Password must be of 6 characters");
                    CustomFunction.setFocus(SgPassword);

                } else if (conpass.length() == 0) {

                    SgConPassword.setError("Please Confirm Password");
                    CustomFunction.setFocus(SgConPassword);

                } else if (!pass.equals(conpass)) {
                    SgConPassword.setError("Password doesn't Match");

                } else {

                    if (PasswordValidator(pass)) {

                    } else {
                        Toast.makeText(RegisterActivity.this, "Weak Password", Toast.LENGTH_SHORT).show();
                        SgPassword.setError("Password must Contain A-Z,a-z,0-9");

                    }
                    webServiceFunction();
                }
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }



    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
//If the user has denied the permission previously your code will come to this block
//Here you can explain why you need this permission
//Explain here why you need this permission
        }
//And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case STORAGE_PERMISSION_CODE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("Permission", "Granted");

                    Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, RESULT_INSURANCE_IMAGE);


                } else {

                    Toast.makeText(RegisterActivity.this,"Request Denied", Toast.LENGTH_LONG).show();
                    Log.e("Permission", "Denied");
                }
                return;
            }
        }
    }





    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("image", "------");

        Bitmap bmp;
        if (requestCode == RESULT_INSURANCE_IMAGE && resultCode == RESULT_OK && null != data) {

            try {

                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                assert cursor != null;
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                stInsurancePath = cursor.getString(columnIndex);
                original = BitmapFactory.decodeFile(stInsurancePath);
                cursor.close();

                bp = decodeUri(selectedImage, 400);

                insuranceImage = new File(stInsurancePath);


                try {
                    ExifInterface exif = new ExifInterface(insuranceImage.getPath());
                    int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    int rotationInDegrees = exifToDegrees(rotation);
                    Log.d("rotation=", String.valueOf(rotationInDegrees));
                    Matrix matrix = new Matrix();
                    if (rotation != 0f) {
                        matrix.preRotate(rotationInDegrees);
                    }

                    ivphoto.setImageBitmap(Bitmap.createBitmap(original, 0, 0, original.getWidth(), original.getHeight(), matrix, true));

                } catch (Exception e) {
                    e.printStackTrace();
                }


                if (insuranceImage != null) {
                    // Compress image in main thread using custom Compressor

                    Log.d("name", String.valueOf(insuranceImage));
                    insuranceImageCompressed = new Compressor.Builder(RegisterActivity.this)
                            .setMaxWidth(800)
                            .setMaxHeight(600)
                            .setQuality(100)
                            .setCompressFormat(Bitmap.CompressFormat.PNG)
                            .build()
                            .compressToFile(insuranceImage);

                    Log.d("sizeeeeeee", getReadableFileSize(insuranceImageCompressed.length()));


                }
                //compressImage(800, 600, insuranceImageId);


            } catch (Exception e) {
                e.printStackTrace();

            }
        }


    }


    protected Bitmap decodeUri(Uri selectedImage, int REQUIRED_SIZE) {

        try {

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);

            // The new size we want to scale to
            // final int REQUIRED_SIZE =  size;

            // Find the correct scale value. It should be the power of 2.
            int width_tmp = o.outWidth, height_tmp = o.outHeight;

            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE
                        || height_tmp / 2 < REQUIRED_SIZE) {
                    break;
                }
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }






    public void webServiceFunction() {

        try {

            Log.d("image", String.valueOf(insuranceImageCompressed));

            //Log.d("imageccccfde", tvphone.getText().toString());


            RequestBody requestImage = RequestBody.create(MediaType.parse("*/*"), insuranceImageCompressed);
            //final RequestBody requestBody4 = RequestBody.create(MediaType.parse("*/*"), insuranceImageCompressed);
           // RequestBody carmodel = RequestBody.create(MediaType.parse("text/plain"), strUserId);
            MultipartBody.Part InsurencePath1 = MultipartBody.Part.createFormData("customer_image", insuranceImageCompressed.getName(), requestImage);
            RequestBody fname = RequestBody.create(MediaType.parse("text/plain"), stFirstName);
            RequestBody lname = RequestBody.create(MediaType.parse("text/plain"), stLastName);
            RequestBody password = RequestBody.create(MediaType.parse("text/plain"), pass);
            RequestBody email = RequestBody.create(MediaType.parse("text/plain"), stEmailId);
            RequestBody contact = RequestBody.create(MediaType.parse("text/plain"), stMobileNumber);



            hitRegister(InsurencePath1,fname,lname,password,email,contact);


        } catch (Exception e) {
            e.printStackTrace();

            Toast.makeText(RegisterActivity.this, "Something went wrong.please try again later", Toast.LENGTH_LONG);
        }

    }


    public String getReadableFileSize(long size) {
        if (size <= 0) {
            return "0";
        }
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    private int exifToDegrees(int exifOrientation) {
        Log.d("orientation", String.valueOf(exifOrientation));
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    private boolean PasswordValidator(String password) {
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN =
                "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,20})";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();
    }


    private void hitRegister( MultipartBody.Part InsurencePath1,RequestBody fname,RequestBody lname,RequestBody password,
                             RequestBody email,RequestBody contact) {

        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
        dialog = Utilss.progressDialog(context);
        dialog.show();

        apiInterface = APIClient.getClient().create(APIInterface.class);

        Call<RegisterStageOne> call = apiInterface.register(InsurencePath1,fname,lname,password,email,contact);

        call.enqueue(new Callback<RegisterStageOne>() {
            @Override
            public void onResponse(Call<RegisterStageOne> call, retrofit2.Response<RegisterStageOne> response) {

                dialog.hide();

                if (response.isSuccessful()) {
                    if(response.body().getCode().equals(100))
                    {


                        Toast.makeText(RegisterActivity.this,"Please Verify your Email Address", Toast.LENGTH_SHORT).show();


                       // Utilss.makeSnackBar(RegisterActivity.this, background, "Register Successfully");
                        Intent i = new Intent(RegisterActivity.this,LoginActivity.class);
                        startActivity(i);


                    }
                    else {

//                        String text = response.body().getText();

                        Toast.makeText(RegisterActivity.this,"Email already Exists", Toast.LENGTH_SHORT).show();
//
                       // Utilss.makeSnackBar(RegisterActivity.this, background, "Email already Exists");


                    }
                }


            }

            @Override
            public void onFailure(Call<RegisterStageOne> call, Throwable t) {


                dialog.hide();

                Toast.makeText(context, "Server Error", Toast.LENGTH_SHORT).show();
                // Toast.makeText(context,"SERVER ERROR",Toast.LENGTH_LONG).show();
                // Log.d("listData", t.getMessage() + "Failure");

            }
        });

    }



}
