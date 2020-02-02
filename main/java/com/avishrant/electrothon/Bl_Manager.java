package com.avishrant.electrothon;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class Bl_Manager extends AppCompatActivity {
    int intval = 1;
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Layout Initialization
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ListView devicelist = findViewById(R.id.listview);
        Button btn = findViewById(R.id.button);
        int[] colors = {0,0xFFFF5500,0};
        devicelist.setDivider(new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT,colors));
        devicelist.setDividerHeight(3);
        if(bluetoothAdapter == null)
        {
            Toast.makeText(getApplicationContext() , "The Device Doesn't support Bluetooth...finishing" , Toast.LENGTH_LONG);
            finish();
        }
        if((!bluetoothAdapter.isEnabled()))
        {
            Intent bt = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(bt,intval);
        }
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click(devicelist);
                Toast.makeText(getBaseContext() , "Refreshing List",Toast.LENGTH_SHORT).show();

            }
        });
        devicelist.setOnItemClickListener(myListClickListener);

    }
    protected void click(ListView devicelist)
    {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0)
        {
            ArrayList list = new ArrayList();
            for(BluetoothDevice device:pairedDevices)
            {
                String deviceName = device.getName();
                String deviceMAC = device.getAddress();
                list.add(deviceName + "\n" + deviceMAC);
            }
            final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, list);
            devicelist.setAdapter(adapter);
        }
    }
    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener()
    {
        public void onItemClick (AdapterView av, View v, int arg2, long arg3)
        {
            // Get the device MAC address, the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);
            // Make an intent to start next activity.
            Intent i = new Intent(Bl_Manager.this, control.class);
            //Change the activity.
            i.putExtra("EXTRA_ADDRESS", address); //this will be received at ledControl (class) Activity
            startActivity(i);

        }
    };
}
