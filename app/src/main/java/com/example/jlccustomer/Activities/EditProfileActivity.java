package com.example.jlccustomer.Activities;

import android.Manifest;
import android.app.AlertDialog;
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
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jlccustomer.Model.ModelGetCustomerProfile;
import com.example.jlccustomer.Model.ModelUpdateCustomer;
import com.example.jlccustomer.Model.ResetPassword;
import com.example.jlccustomer.R;
import com.example.jlccustomer.Retrofit.APIClient;
import com.example.jlccustomer.Retrofit.APIInterface;
import com.example.jlccustomer.Utils.ConnectionDetector;
import com.example.jlccustomer.Utils.CsPrefrences;
import com.example.jlccustomer.Utils.Utilss;

import java.io.File;
import java.text.DecimalFormat;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

public class EditProfileActivity extends AppCompatActivity {
    private static final int EXTERNAL_STORAGE_PERMISSION_CONSTANT = 100;
    private static final int STORAGE_PERMISSION_CODE = 123;
    public static String path;
    public static String stInsurancePath;
    private static int RESULT_INSURANCE_IMAGE = 1;
    TextView tvusername, tvemail, tvUploadPhoto, tvresetPass;
    EditText edOldpass, edNewpass, edConfirmPass, edAddress, tvphone;
    Button btnUpdate, btnCancel, btnOk;
    Context context;
    APIInterface apiInterface;
    Dialog dialog;
    String customerId, customerName, customerPhone, customerEmail;
    String strUserId;
    AlertDialog alertDialogs;
    ImageView ivphoto, imgBack;
    LinearLayout background;
    Bitmap original, bp;
    String strName, strEmail, strPhone;
    CsPrefrences prefrences;
    private File imgFile;
    private File insuranceImageCompressed;
    private File insuranceImage;
    private int insuranceImageId = 1;
    private Uri selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_profile);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        strUserId = getSharedPreferences("sharedPrefName", 0).getString("Key_Login_Id", null);
        strName = getSharedPreferences("sharedPrefName", 0).getString("Key_Login_FNAME", null);
        strEmail = getSharedPreferences("sharedPrefName", 0).getString("Key_Login_EMAIL", null);
        strPhone = getSharedPreferences("sharedPrefName", 0).getString("Key_Login_CONTACT", null);

        context = this;
        prefrences = new CsPrefrences(context);
        customerId = prefrences.getSharedPref("customerId");
        customerName = prefrences.getSharedPref("customerName");
        customerEmail = prefrences.getSharedPref("customerEmail");
        customerPhone = prefrences.getSharedPref("customerPhone");
        imgBack = findViewById(R.id.customer_profile_back);
        tvusername = findViewById(R.id.tvusername);
        tvusername.setText(strName);
        tvphone = findViewById(R.id.tvphone);
        tvphone.setText(strPhone);
        tvemail = findViewById(R.id.tvemail);
        tvemail.setText(strEmail);

        tvUploadPhoto = findViewById(R.id.tvUploadPhoto);
        ivphoto = findViewById(R.id.ivphoto);
        background = findViewById(R.id.background);
        btnUpdate = findViewById(R.id.btnUpdate);
        tvresetPass = findViewById(R.id.tvresetPass);


        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        tvUploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(EditProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestStoragePermission();
                } else {

                    Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, RESULT_INSURANCE_IMAGE);
                }
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hitUpdateProfile();

            }
        });

        tvresetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForgotDialog(EditProfileActivity.this);
            }
        });

//        if (new ConnectionDetector(EditProfileActivity.this).isConnectingToInternet()) {
//
//            hitEditProfile(strUserId);
//
//        } else {
//
//            Toast.makeText(EditProfileActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
//        }
    }


    private void ForgotDialog(final Context context) {


        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.reset_pass_dialog, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        alertDialogs = builder.create();
        alertDialogs.show();


        edOldpass = (EditText) alertDialogs.findViewById(R.id.edOldpass);
        edNewpass = (EditText) alertDialogs.findViewById(R.id.edNewPass);
        edConfirmPass = (EditText) alertDialogs.findViewById(R.id.edConfirmPass);
        btnCancel = (Button) alertDialogs.findViewById(R.id.bt_cancel);
        btnOk = (Button) alertDialogs.findViewById(R.id.bt_ok);


        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//
                String strold = edOldpass.getText().toString();
                String strnew = edNewpass.getText().toString();
                String strconfirm = edConfirmPass.getText().toString();


                if (strnew.length() < 6) {
                    //  Utils.makeSnackBar(EditProfileActivity.this, background, "Please Enter Minimum 6 digit Password");


                    Toast.makeText(EditProfileActivity.this, "Please Enter Minimum 6 digit Password", Toast.LENGTH_SHORT).show();


                } else {


                    if (strnew.equals(strconfirm)) {

                        if (new ConnectionDetector(EditProfileActivity.this).isConnectingToInternet()) {

                            String flag = "update_pass";

                            hitResetPassword(strUserId, strold, strnew);

                        } else {

                            Toast.makeText(EditProfileActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                        }


                    } else {


                        //  Utils.makeSnackBar(EditProfileActivity.this, background, "Confirm Password Doesnot match");


                        Toast.makeText(EditProfileActivity.this, "Confirm Password Doesnot match", Toast.LENGTH_SHORT).show();


                    }

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


    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }


    //method to show file chooser
    private void showFileChooser() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_INSURANCE_IMAGE);
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
                    Toast.makeText(EditProfileActivity.this, "Request Denied", Toast.LENGTH_LONG).show();
                    Log.e("Permission", "Denied");
                }
                return;
            }
        }
    }


    private void hitResetPassword(final String strUserId, final String strold, final String strnew) {

        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
        dialog = Utilss.progressDialog(context);
        dialog.show();

        apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ResetPassword> call = apiInterface.resetPass(strUserId, strold, strnew);

        call.enqueue(new Callback<ResetPassword>() {
            @Override
            public void onResponse(Call<ResetPassword> call, retrofit2.Response<ResetPassword> response) {

                dialog.dismiss();

                if (response.isSuccessful()) {
                    if (response.body().getStatus().equalsIgnoreCase("success")) {

                        // Utilss.makeSnackBar(EditProfileActivity.this, background, "Password Updated Successfully");


                        Toast.makeText(context, "Password Updated Successfully", Toast.LENGTH_LONG).show();
                        alertDialogs.dismiss();


                    } else {

                        // Utils.makeSnackBar(EditProfileActivity.this, background, "Old Password is Incorrect");

                        //  alertDialogs.dismiss();

                        Toast.makeText(context, "Old Password is Incorrect", Toast.LENGTH_LONG).show();

                    }
                }


            }

            @Override
            public void onFailure(Call<ResetPassword> call, Throwable t) {

                dialog.dismiss();
                Toast.makeText(context, "Server Error", Toast.LENGTH_LONG).show();


            }
        });

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
                    insuranceImageCompressed = new Compressor.Builder(EditProfileActivity.this)
                            .setMaxWidth(800)
                            .setMaxHeight(600)
                            .setQuality(100)
                            .setCompressFormat(Bitmap.CompressFormat.WEBP)
                            .build()
                            .compressToFile(insuranceImage);

                    Log.d("sizeeeeeee", getReadableFileSize(insuranceImageCompressed.length()));
                    webServiceFunction();
                    //webServiceFunction();
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

            Log.d("image", String.valueOf(insuranceImage));
            Log.d("image", String.valueOf(insuranceImage.getPath()));

            //Log.d("imageccccfde", tvphone.getText().toString());


            final RequestBody requestBody4 = RequestBody.create(MediaType.parse("*/*"), insuranceImage);
            RequestBody carmodel = RequestBody.create(MediaType.parse("text/plain"), customerId);
            MultipartBody.Part InsurencePath1 = MultipartBody.Part.createFormData("customer_image", insuranceImage.getName(), requestBody4);
            RequestBody flag = RequestBody.create(MediaType.parse("text/plain"), "update_image");


            hitEditProfileUpdate1(carmodel, InsurencePath1);


        } catch (Exception e) {
            e.printStackTrace();

            Toast.makeText(EditProfileActivity.this, "Something went wrong.please try again later", Toast.LENGTH_LONG);
        }

    }

    private void hitEditProfileUpdate1(RequestBody carmodel, MultipartBody.Part InsurencePath1) {

        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
        dialog = Utilss.progressDialogs(context);
        dialog.show();

        apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ResetPassword> call = apiInterface.editProfile(carmodel, InsurencePath1);

        call.enqueue(new Callback<ResetPassword>() {
            @Override
            public void onResponse(Call<ResetPassword> call, retrofit2.Response<ResetPassword> response) {

                dialog.dismiss();

                if (response.isSuccessful()) {
                    if (response.body().getStatus().equalsIgnoreCase("success")) {

                        //Utils.makeSnackBar(EditProfileActivity.this, background, "Image Updated Successfully");


                        Toast.makeText(EditProfileActivity.this, "Image Updated Successfully", Toast.LENGTH_SHORT).show();


                    } else {


                    }
                }

            }

            @Override
            public void onFailure(Call<ResetPassword> call, Throwable t) {

                dialog.dismiss();
                Toast.makeText(context, "Server Error", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void hitUpdateProfile() {

        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
        dialog = Utilss.progressDialogs(context);
        dialog.show();

        apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ModelUpdateCustomer> call = apiInterface.updateCustomerProfile(customerId, customerName, customerPhone, customerEmail);

        call.enqueue(new Callback<ModelUpdateCustomer>() {
            @Override
            public void onResponse(Call<ModelUpdateCustomer> call, retrofit2.Response<ModelUpdateCustomer> response) {

                dialog.dismiss();

                if (response.isSuccessful()) {
                    if (response.body().getStatus().equalsIgnoreCase("success")) {

                        //Utils.makeSnackBar(EditProfileActivity.this, background, "Image Updated Successfully");
                        hitGetCustomerProfile();

                        Toast.makeText(EditProfileActivity.this, "Data Updated Successfully", Toast.LENGTH_SHORT).show();


                    } else {


                    }
                }

            }

            @Override
            public void onFailure(Call<ModelUpdateCustomer> call, Throwable t) {

                dialog.dismiss();
                Toast.makeText(context, "Server Error", Toast.LENGTH_LONG).show();


            }
        });

    }

    private void hitGetCustomerProfile() {

        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
        dialog = Utilss.progressDialogs(context);
        dialog.show();

        apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ModelGetCustomerProfile> call = apiInterface.getCustomerProfile(customerId);

        call.enqueue(new Callback<ModelGetCustomerProfile>() {
            @Override
            public void onResponse(Call<ModelGetCustomerProfile> call, retrofit2.Response<ModelGetCustomerProfile> response) {

                dialog.dismiss();

                if (response.isSuccessful()) {
                    if (!response.body().getContact().equalsIgnoreCase("")) {

                        //Utils.makeSnackBar(EditProfileActivity.this, background, "Image Updated Successfully");

                        tvphone.setText(response.body().getContact());

                    } else {


                    }
                }

            }

            @Override
            public void onFailure(Call<ModelGetCustomerProfile> call, Throwable t) {

                dialog.dismiss();
                Toast.makeText(context, "Server Error", Toast.LENGTH_LONG).show();


            }
        });

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


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}

