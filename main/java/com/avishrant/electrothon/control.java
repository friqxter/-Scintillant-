package com.avishrant.electrothon;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.UUID;


import javax.net.ssl.HttpsURLConnection;

public class control extends AppCompatActivity {
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    TextView loc;
    TextView rec , rec2 , rec3 ;
    Button btn;
    LocationManager locationManager;
    LocationListener locationListener;
    private Handler handler;
    private boolean blue = false;
    String fstr = "";

    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Layout Initialization
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control2);
        handler = new Handler();
        //Get details from Bl_Manager
        Intent newint = getIntent();
        String address = newint.getStringExtra("EXTRA_ADDRESS");

        //Initialising the GUI elements
        loc = findViewById(R.id.locview);
        rec = findViewById(R.id.recview);
        rec2 = findViewById(R.id.recview2);
        rec3 = findViewById(R.id.recview3);
        btn = findViewById(R.id.button2);
        if (btSocket == null || !isBtConnected)
        {

            //Initializing bluetooth services
            myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
            BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
            try
            {
                //Creating bluetooth socket
                btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
            } catch (IOException e)
            {
                Toast.makeText(getBaseContext() , "Socket Creation Failed" , Toast.LENGTH_SHORT).show();
            }
            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
            try {
                btSocket.connect();//start connection
                isBtConnected = true;
                Toast.makeText(getBaseContext() , "Connected" , Toast.LENGTH_SHORT).show();
                blue = true;
            } catch (IOException e) {
                e.printStackTrace();
                isBtConnected = false;
                Toast.makeText(getBaseContext() , "Something Went Wrong. Remote device unresponsive." , Toast.LENGTH_SHORT).show();
            }
        }

        handler.postDelayed(updateTimerThread , 2000);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(blue == true)
                {
                    try
                    {
                        //Setting InputStream to listen for incoming data
                        InputStream inputStream = btSocket.getInputStream();
                        int byteCount = inputStream.available();
                        if(byteCount > 0)
                        {
                            byte[] rawBytes = new byte[byteCount];
                            inputStream.read(rawBytes);

                            //Processing received bytes
                            String string=new String(rawBytes,"UTF-8");
                            String str = string.substring(string.indexOf('{')+1);
                            str = str.substring(0,str.indexOf('}'));

                            rec.setText(str);
                            //new Async().doInBackground();

                        }

                    }
                    catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getBaseContext(), "God knows what is wrong", Toast.LENGTH_SHORT).show();
                    }


                }
                else
                    Toast.makeText(getBaseContext() , "Bluetooth not Operational." , Toast.LENGTH_SHORT).show();


            }
        });
        //Initializing location services
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //Getting the Listener for loc_services
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //Display Lat and Long
                loc.setText("Latitude :" + location.getLatitude() + "  Longitude :" + location.getLongitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

                //Notification builder if Location is disabled
                AlertDialog.Builder builder = new AlertDialog.Builder(control.this);
                builder.setTitle("Location Service Required")
                        .setMessage("For better experience, the app requests Location Services");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        Intent callGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(callGPS);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        loc.setText("Feature Disabled");
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        };


        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET} , 124);
            return;
        }

        //Get Location from Network Provider
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }
    @Override
    public void onDestroy() {
        try
        {
            btSocket.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        super.onDestroy();
    }
    private Runnable updateTimerThread = new Runnable()
    {
        public void run()
        {
            //write here whaterver you want to repeat
            if(blue == true)
            {
                try
                {
                    //Setting InputStream to listen for incoming data
                    InputStream inputStream = btSocket.getInputStream();
                    int byteCount = inputStream.available();
                    if(byteCount > 0)
                    {
                        byte[] rawBytes = new byte[byteCount];
                        inputStream.read(rawBytes);

                        //Processing received bytes
                        String string=new String(rawBytes,"UTF-8");
                        String str = string.substring(string.indexOf('{')+1);
                        if(str.length() > 1) {
                            str = str.substring(0, str.indexOf('}'));
                            String[] res = str.split(",");
                            rec.setText(res[0] + " Chh                   www");
                            rec2 . setText(res[1]);
                            rec3.setText(res[2]);
                            fstr = str;
                        }

                    }

                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }

            handler.postDelayed(this, 500);
        }
    };

    private class Async extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            try {
                URL url = new URL("http://192.168.43.28:8018/");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
            } catch (
                    Exception e) {
                e.printStackTrace();
            }

            return null;
        }

    }
}

//                    }