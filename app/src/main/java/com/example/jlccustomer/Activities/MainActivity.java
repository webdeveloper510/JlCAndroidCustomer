package com.example.jlccustomer.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jlccustomer.Adapters.MyPlacesAdapter;
import com.example.jlccustomer.Model.CustomObject;
import com.example.jlccustomer.Model.ModelBookRide;
import com.example.jlccustomer.Model.ModelCancelRide;
import com.example.jlccustomer.Model.ModelCustomerFare;
import com.example.jlccustomer.Model.ModelDriverRating;
import com.example.jlccustomer.Model.ModelGetDriverLocation;
import com.example.jlccustomer.Model.ModelNotifiyDriver;
import com.example.jlccustomer.R;
import com.example.jlccustomer.Retrofit.APIClient;
import com.example.jlccustomer.Retrofit.APIInterface;
import com.example.jlccustomer.Utils.Constants;
import com.example.jlccustomer.Utils.CsPrefrences;
import com.example.jlccustomer.Utils.Event;
import com.example.jlccustomer.Utils.GoogleApis;
import com.example.jlccustomer.Utils.MainMenu;
import com.example.jlccustomer.Utils.Utilss;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, MyPlacesAdapter.EnentHandler {
    private static final String TAG = Utilss.class.getSimpleName();
    public static boolean isFindTripOpen = false;
    public static boolean isDriverAccept = false;
    CustomObject myData;
    DrawerLayout drawer;
    CircleImageView imgDriverProfilePic, customerProfilePic;
    ImageView imgCar1, imgCar2, imgCar3, imgHumburger;
    boolean drawBookRide = false;
    Location pickupLocation, dropLocation;
    Context context;
    Bundle bundle;
    Dialog progressDialog;
    APIInterface apiInterface;
    MyPlacesAdapter adapter;
    AutoCompleteTextView dropLocationPlaces;
    Double lattitude, longitude;
    boolean getCurrentLocation = false;
    ImageView imgBack;
    GoogleMap map;
    CsPrefrences prefrences;
    TextView txWhereTo, txPriceGo, txPriceXl, txPricePremium, txVehicleName, txVehicleNumber, txDriverName, txRideCancel, txOngoingPickup, txOngoingDestination, txRideDestinationPopup, txDayWishes;
    RelativeLayout llMainMapView;
    LinearLayout llAddLocation, llBookRideLayout, llFindTrip, llDriverInfoLayout, llOngoingLayout, llRideDestinationPop;
    Marker stopMarker, startMarker;
    MarkerOptions stopOptions = new MarkerOptions();
    MarkerOptions carMarkerOptions = new MarkerOptions();
    Button btnBookRide;
    int carType = 2;
    String customerId, pickLocationName, dropLocationName, rideId, driverID, customerName, customerImage, customerRate;
    Polyline greyPolyLine;
    PolylineOptions polylineOptions;
    ArrayList<LatLng> polyLineList;
    ArrayList<String> driverIdArray;
    Handler handler = new Handler();
    LatLng startPosition, endPosition;
    Handler updateLocationHandler = new Handler();
    int delay = 10000; //milliseconds
    int updateLocationDelay = 6000; //milliseconds
    Double driverLattitude, driverLongitude;
    int iVar = 0;
    int state = 0;
    Dialog writeReviewsDialog;
    TextView txWriteReviews, txCustomerName, txCustomerRating;
    LinearLayout llActivityRating;
    private boolean isFirstPosition = true;

    public static Bitmap createCustomMarker(Context context, @DrawableRes int resource, String _name) {
        View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);
        CircleImageView markerImage = (CircleImageView) marker.findViewById(R.id.user_dp);
        markerImage.setImageResource(resource);
        TextView txt_name = (TextView) marker.findViewById(R.id.name);
        txt_name.setText(_name);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        marker.setLayoutParams(new ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT));
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        marker.draw(canvas);
        return bitmap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        prefrences = new CsPrefrences(context);
        inflateMainView();
        initViews();
        setToolbarDrawer();
        getSupportMapFragment();
        getIntentData();
        initNavigationDrawer();
        clickListner();
        checkDayStatus();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Menu menu = navigationView.getMenu();

        Log.d("CustomerId", customerId);


    }

    private void checkDayStatus() {
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if (timeOfDay >= 0 && timeOfDay < 12) {
            txDayWishes.setText(" Good Morning, " + customerName);
        } else if (timeOfDay >= 12 && timeOfDay < 16) {
            txDayWishes.setText(" Good Afternoon, " + customerName);
        } else if (timeOfDay >= 16 && timeOfDay < 21) {
            txDayWishes.setText(" Good Evening, " + customerName);
        } else if (timeOfDay >= 21 && timeOfDay < 24) {
            txDayWishes.setText(" Good Night, " + customerName);
        }

    }

    private void clickListner() {
        txWhereTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Geocoder geocoder;
                String address = null;
                List<Address> addresses = null;
                geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                try {
                    if (lattitude != null && longitude != null)
                        addresses = geocoder.getFromLocation(lattitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (addresses != null) {
                    address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

                }
                if (address != null) {
                    Intent intent = new Intent(context, ChooseLocation.class);
                    intent.putExtra("currentLocation", address);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(context, ChooseLocation.class);
                    intent.putExtra("currentLocation", "");
                    startActivity(intent);
                }
                //  startActivity(new Intent(context, ChooseLocation.class));

            }
        });
        imgCar1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                carType = 1;
                imgCar1.setImageResource(R.mipmap.car_selected_jlc);
                imgCar2.setImageResource(R.mipmap.car_go);
                imgCar3.setImageResource(R.mipmap.car_xl_jlc);
            }
        });
        imgCar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                carType = 2;
                imgCar1.setImageResource(R.mipmap.car_go);
                imgCar3.setImageResource(R.mipmap.car_xl_jlc);
                imgCar2.setImageResource(R.mipmap.car_selected_jlc);
            }
        });
        imgCar3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                carType = 3;
                imgCar1.setImageResource(R.mipmap.car_go);
                imgCar2.setImageResource(R.mipmap.car_xl_jlc);
                imgCar3.setImageResource(R.mipmap.car_selected_jlc);
            }
        });
        imgHumburger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!drawBookRide) {
                    drawer.openDrawer(Gravity.LEFT);

                } else {
                    llBookRideLayout.setVisibility(View.GONE);
                    llAddLocation.setVisibility(View.VISIBLE);
                    imgHumburger.setImageResource(R.mipmap.jlc_hamburger);
                }

            }
        });
        btnBookRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hitBookRideApi();
            }
        });
        txRideCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hitGetCustomerCancelRide();
            }
        });
    }

    private void drawPath() {
        llBookRideLayout.setVisibility(View.VISIBLE);
        llAddLocation.setVisibility(View.GONE);
        GoogleApis.hitDirectionApi(Utilss.directionApi(context, pickupLocation.getLatitude(), pickupLocation.getLongitude(), dropLocation.getLatitude(), dropLocation.getLongitude()));
        if (pickupLocation != null && dropLocation != null) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(new LatLng(pickupLocation.getLatitude(), pickupLocation.getLongitude()));
            builder.include(new LatLng(dropLocation.getLatitude(), dropLocation.getLongitude()));
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
        }

    }

    void staticPolyLine() {
        if (greyPolyLine != null) {
            greyPolyLine.remove();
        }
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng latLng : polyLineList) {
            builder.include(latLng);
        }
        polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.BLACK);
        polylineOptions.width(10);
        polylineOptions.startCap(new SquareCap());
        polylineOptions.endCap(new SquareCap());
        polylineOptions.jointType(JointType.ROUND);
        polylineOptions.addAll(polyLineList);
        greyPolyLine = map.addPolyline(polylineOptions);
        if (state == 0) {
            addStartMarker(new LatLng(pickupLocation.getLatitude(), pickupLocation.getLongitude()));
            addStopMarker(new LatLng(dropLocation.getLatitude(), dropLocation.getLongitude()));
        }
    }

    private void initViews() {
        driverIdArray = new ArrayList<>();
        imgBack = findViewById(R.id.imgBack);
        polyLineList = new ArrayList<>();
        txRideCancel = findViewById(R.id.txRideCancel);
        imgDriverProfilePic = findViewById(R.id.imgDriverProfile);
        txVehicleName = findViewById(R.id.txVehicleName);
        txVehicleNumber = findViewById(R.id.txVehicleNumber);
        txDriverName = findViewById(R.id.txDriverName);
        llMainMapView = findViewById(R.id.llMainMapView);
        dropLocationPlaces = (AutoCompleteTextView) findViewById(R.id.places);
        txPriceGo = findViewById(R.id.txPriceGo);
        txDayWishes = findViewById(R.id.txDayWishes);
        txPricePremium = findViewById(R.id.txPricePre);
        txPriceXl = findViewById(R.id.txPriceXL);
        txWhereTo = findViewById(R.id.txWhereTo);
        llBookRideLayout = findViewById(R.id.llInflatedChooseLoc);
        txOngoingPickup = findViewById(R.id.txOngoingPickup);
        txOngoingDestination = findViewById(R.id.txOngoinDestination);
        txRideDestinationPopup = findViewById(R.id.txOngoingDestinationPop);

        llFindTrip = findViewById(R.id.llInflatedFindTrip);
        llOngoingLayout = findViewById(R.id.llInflatedOngoing);
        llRideDestinationPop = findViewById(R.id.llOngoingRidePopUp);

        llDriverInfoLayout = findViewById(R.id.llInflatedDriverInfo);
        llAddLocation = findViewById(R.id.llPick);
        imgCar1 = findViewById(R.id.img_car1);
        imgCar2 = findViewById(R.id.img_car2);
        imgCar3 = findViewById(R.id.img_car3);
        imgHumburger = findViewById(R.id.imgHumburger);
        btnBookRide = findViewById(R.id.btnBookRide);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        MainMenu mai = new MainMenu(this, item);
        return true;
    }

    private void getSupportMapFragment() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void setToolbarDrawer() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    }

    //***Inflate contentHome View in MainActivity
    private void inflateMainView() {
        LinearLayout dynamicContent = (LinearLayout) findViewById(R.id.drawer_layout_main);
        View wizardView = getLayoutInflater().inflate(R.layout.content_home, dynamicContent, false);
        dynamicContent.addView(wizardView);
    }

    //End Inflate
    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(context);
    }

    @Override
    protected void onStart() {
        EventBus.getDefault().register(context);
        super.onStart();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(Event event) {
        switch (event.getKey()) {
            case Constants.LOCATION_GET:
                lattitude = event.getLattitude();
                longitude = event.getLongitude();
                if (lattitude != null && longitude != null) {
                    if (!getCurrentLocation && !drawBookRide) {
                        if (lattitude != null && longitude != null) {
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lattitude, longitude), 14));
                            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            map.setMyLocationEnabled(true);
                        }
                    }
                    getCurrentLocation = true;
                }
                Log.d("AppLattitudeLongitude", lattitude + "application" + " " + longitude + "application");
                break;

            case Constants.DRAW_POLYLINE:
                polyLineList = (ArrayList<LatLng>) GoogleApis.polyLineList;

                if (polyLineList != null) {
                    staticPolyLine();
                } else {
                    Toast.makeText(context, "Google Direction Api Response is null", Toast.LENGTH_LONG).show();
                }
                break;

            case Constants.DRIVER_ACCEPT:
                if (progressDialog != null) {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                }
                state = 1;
                llFindTrip.setVisibility(View.GONE);
                llDriverInfoLayout.setVisibility(View.VISIBLE);
                txVehicleNumber.setText(event.getVehi_number());
                txDriverName.setText(event.getDriverName());
                if (!event.getDriverImage().equalsIgnoreCase("")) {
                    Picasso.with(context).load(event.getDriverImage().replaceAll("\\s+", "")).placeholder(R.mipmap.user).into(imgDriverProfilePic);
                }
                rideId = event.getRide_id();
                driverID = event.getDriverID();
                updateLocationHandler.postDelayed(new Runnable() {
                    public void run() {
                        handler.postDelayed(this, updateLocationDelay);
                        hitGetDriverLocation(driverID);
                    }
                }, updateLocationDelay);

                break;
            case Constants.DRIVER_ARRIVED:
                state = 2;
                txRideCancel.setVisibility(View.GONE);
                break;
            case Constants.TRIP_START:
                state = 3;
                llDriverInfoLayout.setVisibility(View.GONE);
                llOngoingLayout.setVisibility(View.VISIBLE);
                txOngoingPickup.setText(pickLocationName);
                Log.d("tripStartDroptest", dropLocationName);
                txOngoingDestination.setText(dropLocationName);
                llRideDestinationPop.setVisibility(View.VISIBLE);
                txRideDestinationPopup.setText(dropLocationName);
                break;
            case Constants.PAYMENT_COLLECT:
                Toast.makeText(context, "Ride is Complete", Toast.LENGTH_LONG).show();
                llOngoingLayout.setVisibility(View.GONE);
                llRideDestinationPop.setVisibility(View.GONE);
                llBookRideLayout.setVisibility(View.GONE);
                llAddLocation.setVisibility(View.VISIBLE);
                imgHumburger.setImageResource(R.mipmap.jlc_hamburger);
                writeReviewsDialog = Utilss.writeReviewsDialog(context);
                writeReviewsDialog.setCancelable(false);
                writeReviewsDialog.show();
                Window window = writeReviewsDialog.getWindow();
                window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                final RatingBar ratingBar = writeReviewsDialog.findViewById(R.id.rtbProductRating);
                final EditText edtWriteReviews = writeReviewsDialog.findViewById(R.id.edtWriteReview);
                TextView txCancel = writeReviewsDialog.findViewById(R.id.txCancel);
                TextView txPost = writeReviewsDialog.findViewById(R.id.txPost);
                txCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        writeReviewsDialog.dismiss();
                    }

                });
                txPost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ratingBar.getRating() > 0) {
                            if (!edtWriteReviews.getText().toString().equalsIgnoreCase("")) {
                                Log.d("ratingadd", ratingBar.getRating() + " ");
                                hitDriverRatingApi(String.valueOf(ratingBar.getRating()), edtWriteReviews.getText().toString());
                            } else {
                                Toast.makeText(context, "Please add reviews", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(context, "Please give rating ", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                /* final TextView titleBar =  submitDialog.findViewById(R.id.titlebar);*/
                LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
                // Filled stars
                setRatingStarColor(stars.getDrawable(2), ContextCompat.getColor(context, R.color.startColorActivated));
                // Half filled stars
                setRatingStarColor(stars.getDrawable(1), ContextCompat.getColor(context, R.color.startColorNormal));
                // Empty stars
                setRatingStarColor(stars.getDrawable(0), ContextCompat.getColor(context, R.color.startColorNormal));


                break;
            case Constants.TRIP_COMPLETE:
                state = 4;

                break;
            case Constants.DRIVER_CANCEL_RIDE:
                llDriverInfoLayout.setVisibility(View.GONE);
                llAddLocation.setVisibility(View.VISIBLE);
                imgHumburger.setImageResource(R.mipmap.jlc_hamburger);
                Toast.makeText(context, "Ride Cancel", Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (drawBookRide) {
            map.setInfoWindowAdapter(new MyInfoWindowAdapter());
            drawPath();
            hitGetRideFare();
        } else {
            googleMap.setMyLocationEnabled(true);
            lattitude = SplashActivity.lattitude;
            longitude = SplashActivity.longitude;
            if (lattitude != null && longitude != null) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lattitude, longitude), 14));
                googleMap.setMyLocationEnabled(true);
            }

        }

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
        customerRate = prefrences.getSharedPref("customerRate");
        customerId = prefrences.getSharedPref("customerId");
        customerName = prefrences.getSharedPref("customerName");
        customerImage = prefrences.getSharedPref("customerImage");
        bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey("pickupLocation")) {
                drawBookRide = true;
                pickupLocation = (Location) bundle.get("pickupLocation");
                imgHumburger.setImageResource(R.mipmap.back_jlc);
            }
            if (bundle.containsKey("dropLocation")) {
                dropLocation = (Location) bundle.get("dropLocation");
            }
            if (bundle.containsKey("pickLocationName")) {
                pickLocationName = bundle.getString("pickLocationName");
            }
            if (bundle.containsKey("dropLocationName")) {
                dropLocationName = bundle.getString("dropLocationName");
                Log.d("dropLocationTest", dropLocationName);

            }
        }
    }

    private void addStopMarker(LatLng latLng) {
        stopOptions.position(latLng);
        if (stopMarker != null)
            stopMarker.remove();
        stopMarker = createMarker(dropLocationName, latLng, false);
    }

    private void addStartMarker(LatLng latLng) {
        if (startMarker != null)
            startMarker.remove();
        if (state == 1 || state == 2) {
            carMarkerOptions.position(latLng);
            startMarker = createMarker(pickLocationName, latLng, true);
            Log.d("sourceLattitude", latLng.latitude + "start Lattitude" + latLng.longitude + " start Longitude");

        } else if (state == 3 || state == 4) {
            carMarkerOptions.position(latLng);
            startMarker = createMarker(pickLocationName, latLng, true);
            Log.d("sourceLattitude", lattitude + "start Lattitude" + longitude + " start Longitude");

        }
    }

    private void hitBookRideApi() {
        if (progressDialog != null) {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
        }
        progressDialog = Utilss.progressDialogs(context);
        progressDialog.show();
        apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ModelBookRide> call = apiInterface.hitBookRideApi(customerId, pickLocationName, pickupLocation.getLongitude() + "", pickupLocation.getLatitude() + "", dropLocation.getLatitude() + "", dropLocation.getLongitude() + "", dropLocationName, carType, "cash");
        call.enqueue(new Callback<ModelBookRide>() {
            @Override
            public void onResponse(Call<ModelBookRide> call, Response<ModelBookRide> response) {
                if (progressDialog != null) {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                }
                if (response.body().getStatus() != null) {
                    if (response.body().getStatus().equalsIgnoreCase("success")) {
                        final int rideID = response.body().getRideId();
                        llBookRideLayout.setVisibility(View.GONE);
                        llFindTrip.setVisibility(View.VISIBLE);
                        isFindTripOpen = true;
                        map.clear();
                        driverIdArray = (ArrayList<String>) response.body().getData();
                        iVar = 0;
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                if (iVar < driverIdArray.size() && !isDriverAccept) {
                                    handler.postDelayed(this, delay);
                                    String driverId = driverIdArray.get(iVar);
                                    Log.d("driverId", driverId);
                                    hitNotifyDrivers(String.valueOf(rideID), driverId);
                                }
                                if (iVar >= driverIdArray.size() && !isDriverAccept) {
                                    llDriverInfoLayout.setVisibility(View.GONE);
                                    llAddLocation.setVisibility(View.VISIBLE);
                                    llFindTrip.setVisibility(View.GONE);
                                    imgHumburger.setImageResource(R.mipmap.jlc_hamburger);
                                    Toast.makeText(context, "No Driver Found", Toast.LENGTH_LONG).show();
                                }
                                iVar++;
                            }
                        }, delay);
                        progressDialog = Utilss.progressDialogs(context);
                        progressDialog.show();

                    }
                }
            }

            @Override
            public void onFailure(Call<ModelBookRide> call, Throwable t) {
                if (progressDialog != null) {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                }
            }
        });
    }

    private void hitGetRideFare() {
        if (progressDialog != null) {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
        }
        progressDialog = Utilss.progressDialogs(context);
        progressDialog.show();
        apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ModelCustomerFare> call = apiInterface.hitGetRideFare(customerId, pickupLocation.getLatitude() + "", pickupLocation.getLongitude() + "", dropLocation.getLatitude() + "", dropLocation.getLongitude() + "");
        call.enqueue(new Callback<ModelCustomerFare>() {
            @Override
            public void onResponse(Call<ModelCustomerFare> call, Response<ModelCustomerFare> response) {
                if (progressDialog != null) {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                }
                if (response.body().getStatus().equalsIgnoreCase("success")) {
                    txPriceGo.setText(String.valueOf(response.body().getData().getGo()));
                    txPricePremium.setText(String.valueOf(response.body().getData().getPre()));
                    txPriceXl.setText(String.valueOf(response.body().getData().getXl()));
                    /*Toast.makeText(context, "Fare Accepted", Toast.LENGTH_LONG).show();*/
                }
            }

            @Override
            public void onFailure(Call<ModelCustomerFare> call, Throwable t) {
                if (progressDialog != null) {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                }
            }
        });
    }

    private void hitDriverRatingApi(String rating, String review) {
        if (progressDialog != null) {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
        }
        progressDialog = Utilss.progressDialogs(context);
        progressDialog.show();
        apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ModelDriverRating> call = apiInterface.hitDriverRating(rideId, review, rating);
        call.enqueue(new Callback<ModelDriverRating>() {
            @Override
            public void onResponse(Call<ModelDriverRating> call, Response<ModelDriverRating> response) {
                if (progressDialog != null) {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                }
                if (response.body().getStatus().equalsIgnoreCase("success")) {
                    Toast.makeText(context, "Your Rating Submit", Toast.LENGTH_LONG).show();
                    map.clear();
                    writeReviewsDialog.dismiss();

                }
            }

            @Override
            public void onFailure(Call<ModelDriverRating> call, Throwable t) {
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG).show();

                writeReviewsDialog.dismiss();
                if (progressDialog != null) {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                }
            }
        });
    }

    private void hitGetDriverLocation(String driverID) {

        apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ModelGetDriverLocation> call = apiInterface.hitGetDriverLocation(driverID);
        call.enqueue(new Callback<ModelGetDriverLocation>() {
            @Override
            public void onResponse(Call<ModelGetDriverLocation> call, Response<ModelGetDriverLocation> response) {
                if (progressDialog != null) {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                }
                Log.d("getLocation", "location get");
                if (response != null) {
                    if (response.body().getStatus() != null) {

                        if (response.body().getStatus().equalsIgnoreCase("success")) {
                            driverLattitude = Double.parseDouble(response.body().getData().getDriverLat());
                            driverLongitude = Double.parseDouble(response.body().getData().getDriverLong());
                            if (driverLattitude != null && driverLongitude != null) {

                                if (state == 1) {
                                    GoogleApis.hitDirectionApi(Utilss.directionApi(context, driverLattitude, driverLongitude, lattitude, longitude));
                                    addStartMarker(new LatLng(driverLattitude, driverLongitude));
                                    addStopMarker(new LatLng(lattitude, longitude));

                                } else if (state == 2) {
                                    GoogleApis.hitDirectionApi(Utilss.directionApi(context, driverLattitude, driverLongitude, pickupLocation.getLatitude(), pickupLocation.getLongitude()));
                                    addStartMarker(new LatLng(driverLattitude, driverLongitude));
                                    addStopMarker(new LatLng(pickupLocation.getLatitude(), pickupLocation.getLongitude()));

                                } else if (state == 3) {
                                    GoogleApis.hitDirectionApi(Utilss.directionApi(context, driverLattitude, driverLongitude, dropLocation.getLatitude(), dropLocation.getLongitude()));
                                    addStartMarker(new LatLng(driverLattitude, driverLongitude));
                                    addStopMarker(new LatLng(dropLocation.getLatitude(), dropLocation.getLongitude()));

                                }
                                moveCarAnimation(driverLattitude, driverLongitude);
                            }
                        }
                    }

                }

            }

            @Override
            public void onFailure(Call<ModelGetDriverLocation> call, Throwable t) {
                if (progressDialog != null) {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                }
            }
        });
    }

    protected Marker createMarker(String locationName, LatLng latLng, boolean pickup) {
        int img;
        if (pickup) {
            img = R.mipmap.start_new_image;
        } else {
            img = R.drawable.test_stop;
        }
        Marker marker = map.addMarker(new MarkerOptions().position(latLng).anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory.fromBitmap(createCustomMarker(MainActivity.this, img, ""))).title(pickLocationName));
        myData = new CustomObject(locationName);
        marker.setTag(myData);
        return marker;
    }

    private void hitGetCustomerCancelRide() {
        if (progressDialog != null) {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
        }
        progressDialog = Utilss.progressDialogs(context);
        progressDialog.show();
        apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ModelCancelRide> call = apiInterface.hitCancelRide(rideId);
        call.enqueue(new Callback<ModelCancelRide>() {
            @Override
            public void onResponse(Call<ModelCancelRide> call, Response<ModelCancelRide> response) {
                if (progressDialog != null) {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                }
                if (response != null) {
                    if (response.body().getStatus().equalsIgnoreCase("success")) {
                        llDriverInfoLayout.setVisibility(View.GONE);
                        llAddLocation.setVisibility(View.VISIBLE);
                        imgHumburger.setImageResource(R.mipmap.jlc_hamburger);
                        Toast.makeText(context, "Ride Cancel", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ModelCancelRide> call, Throwable t) {
                if (progressDialog != null) {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                }
            }
        });
    }

    private void hitNotifyDrivers(String rideId, String driverNotifyID) {
        if (progressDialog != null) {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
        }
        progressDialog = Utilss.progressDialogs(context);
        progressDialog.show();
        apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ModelNotifiyDriver> call = apiInterface.hitDriverNotifyApi(rideId, customerId, driverNotifyID);
        call.enqueue(new Callback<ModelNotifiyDriver>() {
            @Override
            public void onResponse(Call<ModelNotifiyDriver> call, Response<ModelNotifiyDriver> response) {
                if (progressDialog != null) {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                }
                if (response != null) {
                    if (response.body().getStatus().equalsIgnoreCase("success")) {
                        Log.d("notified", "notified");
                    }
                }
            }

            @Override
            public void onFailure(Call<ModelNotifiyDriver> call, Throwable t) {
                if (progressDialog != null) {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                }
            }
        });
    }

    private void moveCarAnimation(double lati, double lngi) {
        final LatLng latLng = new LatLng(lati, lngi);
        if (isFirstPosition) {
            startPosition = latLng;
            isFirstPosition = false;
        } else {
            endPosition = new LatLng(lati, lngi);
            Log.d(TAG, startPosition.latitude + "--" + endPosition.latitude + "--Check --" + startPosition.longitude + "--" + endPosition.longitude);
            if ((startPosition.latitude != endPosition.latitude) || (startPosition.longitude != endPosition.longitude)) {
                Log.e(TAG, "NOT SAME");
                Utilss.getInstance().startBikeAnimation(map, startMarker, startMarker.getPosition(), endPosition);
            } else {
                Log.e(TAG, "SAMME");
            }
        }
    }

    private void setRatingStarColor(Drawable drawable, @ColorInt int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            DrawableCompat.setTint(drawable, color);
        } else {
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }
    }

    public void initNavigationDrawer() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        customerProfilePic = (CircleImageView) navigationView.findViewById(R.id.nav_image);
        View header = navigationView.getHeaderView(0);
        txCustomerName = (TextView) header.findViewById(R.id.customerName);
        LinearLayout linearLayout = (LinearLayout) header.findViewById(R.id.ll_layout1);
        customerProfilePic = (CircleImageView) header.findViewById(R.id.nav_image);
        txCustomerRating = (TextView) header.findViewById(R.id.customerRating);
        txCustomerRating.setText(customerRate);
        txCustomerName.setText(customerName);
        if (!customerImage.equalsIgnoreCase("")) {
            Picasso.with(context).load(customerImage).placeholder(R.mipmap.user).into(customerProfilePic);
        }
        customerProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, EditProfileActivity.class));

            }
        });

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, EditProfileActivity.class));

            }
        });

    }

    //****Info Windo Clciks Manage And (Open Info Window according to some conditions)
    class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        public View myContentsView;

        MyInfoWindowAdapter() {
            myContentsView = getLayoutInflater().inflate(R.layout.custom_info_contents, null);
        }

        @Override
        public View getInfoContents(Marker marker) {
            if (marker != null) {
                myData = (CustomObject) marker.getTag();
                if (myData != null) {
                    if (myContentsView != null) {
                        TextView tvActivityName = ((TextView) myContentsView.findViewById(R.id.txRideType));
                        tvActivityName.setText(myData.getRideType());
                    }
                } else {
                    TextView tvActivityName = ((TextView) myContentsView.findViewById(R.id.txRideType));
                    tvActivityName.setText(marker.getTitle());
                }
            }
            return myContentsView;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            // TODO Auto-generated method stub
            return null;
        }
    }

}



