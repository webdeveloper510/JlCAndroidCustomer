package com.example.jlccustomer.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.jlccustomer.Adapters.MyPlacesAdapter;
import com.example.jlccustomer.Model.MyGooglePlaces;
import com.example.jlccustomer.R;
import com.example.jlccustomer.Utils.Utilss;

public class ChooseLocation extends AppCompatActivity implements MyPlacesAdapter.EnentHandler {
    MyPlacesAdapter adapter;
    ImageView imgBack;
    MyGooglePlaces googlePlaces;
    AutoCompleteTextView dropLocationPlaces;
    Context context;
    Button btnDone;
    Dialog progressDialog;
    Bundle bundle;
    EditText edtPickupLocation;
    String currentLocation, pickLocationName, dropLocationName;
    Location pickUpLocation, dropLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_location);
        context = ChooseLocation.this;
        initViews();
        getIntentData();
        showPlacesApi();
        clickListners();
        onFocusListner();
    }

    private void onFocusListner() {
        edtPickupLocation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                } else {
                    progressDialog = Utilss.progressDialogs(context);
                    progressDialog.show();
                    Address address = Utilss.getAddressAndLocation(edtPickupLocation.getText().toString(), context);
                    if (address != null) {
                        if (progressDialog != null) {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }
                        Double lat = address.getLatitude();
                        Double lng = address.getLongitude();
                        pickUpLocation.setLatitude(lat);
                        pickUpLocation.setLongitude(lng);
                        edtPickupLocation.setText(address.getAddressLine(0));

                    } else {
                        if (progressDialog != null) {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }
                        Toast.makeText(context, "Something went Wrong", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
    }

    private void initViews() {
        imgBack = findViewById(R.id.imgBack);
        dropLocationPlaces = (AutoCompleteTextView) findViewById(R.id.places);
        btnDone = (Button) findViewById(R.id.btnDone);
        edtPickupLocation = findViewById(R.id.edtPickupLocation);
        pickUpLocation = new Location("");
        dropLocation = new Location("");
    }

    private void clickListners() {
        /*edtPickupLocation.setEnabled(true);*/
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pickUpLocation != null) {
                    if (!dropLocationPlaces.getText().toString().equalsIgnoreCase("")) {
                        Address address = Utilss.getAddressAndLocation(dropLocationPlaces.getText().toString(), context);
                       // Address address = Utilss.getAddressAndLocation("ISBT Road, Sector 43, Chandigarh, India", context);
                        if (address != null) {
                            Double lat = address.getLatitude();
                            Double lng = address.getLongitude();
                            dropLocation.setLatitude(lat);
                            dropLocation.setLongitude(lng);
                        }
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.putExtra("pickupLocation", pickUpLocation);
                        intent.putExtra("dropLocation", dropLocation);
                        intent.putExtra("pickLocationName", edtPickupLocation.getText().toString());
                        intent.putExtra("dropLocationName", dropLocationPlaces.getText().toString());

                        startActivity(intent);
                    } else {
                        Toast.makeText(context, "Please choose drop Location", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();

                }


            }
        });
    }


    private void showPlacesApi() {
        adapter = new MyPlacesAdapter(context, this);
        dropLocationPlaces.setAdapter(adapter);
        // text changed listener to get results precisely according to our search
        dropLocationPlaces.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("listData", "TextChangeddddd" + "Failure");
            }
        });
        // handling click of autotextcompleteview items
        dropLocationPlaces.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InputMethodManager inputMethodManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                if (getCurrentFocus() != null) {
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }

                Log.d("checkPos", position + "Check position");
                googlePlaces = adapter.getItem(position);
                if (googlePlaces != null)
                    dropLocationPlaces.setText(googlePlaces.getName());
            }
        });
    }

    @Override
    public void handle(int i) {
        if (i == 1) {
            adapter.notifyDataSetChanged();
        } else if (i == 0) {
            adapter.notifyDataSetInvalidated();
        }
    }

    private void getIntentData() {
        bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey("currentLocation")) {
                currentLocation = bundle.getString("currentLocation");
                edtPickupLocation.setText(currentLocation);
                edtPickupLocation.requestFocus();
                edtPickupLocation.setSelection(0);
            }
        }
    }
}
