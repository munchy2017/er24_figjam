package com.example.prosp.er24;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.prosp.er24.users.Profile;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.example.prosp.er24.users.Register.KEY_EMAIL;

public class Dashboard extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    //shared
    private static final String SHARED_PREF_NAME = "mysharedpref";

    private TextView txtName;
    private TextView txtEmail;
    // private Button btnLogout;


    //text location
    Geocoder geocoder;
    List<Address> addresses;
    Double latitude;
    Double longitude;
    static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;
    String fullAddress;


    private ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        geocoder = new Geocoder(this, Locale.getDefault());

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);




        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        Button SOSCALL = (Button) findViewById(R.id.button2);
        SOSCALL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:0775688386"));

                if (ActivityCompat.checkSelfPermission(Dashboard.this,
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(callIntent);
            }
        });



        final Button SOSTEXT = (Button) findViewById(R.id.button);
        SOSTEXT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendLocationSMS("+263775688386", getLocation());

                }





            });





        //get user email to dashboard
        txtEmail = (TextView) findViewById(R.id.textView8);

        SharedPreferences sp = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        // String name = sp.getString(KEY_EMAIL, null);
        txtEmail.setText(sp.getString(KEY_EMAIL, String.valueOf(txtEmail)));



    }

    private String getLocation() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {
            android.location.Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (location != null){
                latitude = location.getLatitude();
                longitude = location.getLongitude();

                try {
                    addresses = geocoder.getFromLocation(latitude, longitude, 1);

                    String address = addresses.get(0).getAddressLine(0);
                    String area = addresses.get(0).getAdminArea();
                    String city = addresses.get(0).getAdminArea();
                    fullAddress = address+" "+area+" "+city;
                     Toast.makeText(Dashboard.this, "Message sent", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "Unable to get your location", Toast.LENGTH_SHORT).show();
            }
        }

        return fullAddress;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_LOCATION:
                getLocation();
                break;
        }
    }
    //THIS IS THE METHOD THAT SEND YOUR LOCATION TO A PHONE NIMBER
    public void sendLocationSMS(String phoneNumber, String currentLocation) {
        SmsManager smsManager = SmsManager.getDefault();
        StringBuffer smsBody = new StringBuffer();
        smsBody.append("Requesting Emergency Ambulance Service!! I am located at "+ currentLocation );
        //smsBody.append(currentLocation.getLatitude());
        smsBody.append(",");
        //smsBody.append(currentLocation.getLongitude());
        smsManager.sendTextMessage(phoneNumber, null, smsBody.toString(), null, null);
    }












    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            Uri uri= Uri.parse("http://www.fusiondc.co.zw");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_gallery) {

            Intent a= new Intent(Dashboard.this, Book.class);
            startActivity(a);

        }
        else if (id == R.id.nav_manage) {
            Intent a= new Intent(Dashboard.this, ER.class);
            startActivity(a);

        }  //logout
        else if (id == R.id.log) {


            Intent a= new Intent(Dashboard.this, Profile.class);
            startActivity(a);


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
