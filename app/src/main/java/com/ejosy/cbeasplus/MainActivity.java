package com.ejosy.cbeasplus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;



public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    // Defining Permission codes.
    // We can give any value
    // but unique for each permission.
    private static final int SMS_SEND_PERMISSION_CODE = 100;
    private static final int SMS_RECEIVED_PERMISSION_CODE = 101;
    //Spinner
    public String selected_item = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Spinner: emergency_type element
        Spinner spinner_emergency_type = (Spinner) findViewById(R.id.spinner_emergency_type);
        // Spinner:emergency_type click listener
        spinner_emergency_type.setOnItemSelectedListener(this);
        // Spinner:emergency_type  Drop down elements
        List<String> emergency_type = new ArrayList<String>();
        emergency_type.add("Theft/Robbery");
        emergency_type.add("Health Issues");
        emergency_type.add("Fire Incidence");

        spinner_emergency_type.setPrompt("Select an item");
        // Creating adapter for spinner:emergency_type
        ArrayAdapter<String> dataAdapter_emergency_type = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, emergency_type);
        // Drop down layout style - list view with radio button
        dataAdapter_emergency_type.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner:emergency_type
        spinner_emergency_type.setAdapter(dataAdapter_emergency_type);
        //

        //
        Button activateAlertBtn = findViewById(R.id.btnSendAlert);
        activateAlertBtn.setEnabled(false);
        activateAlertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = "+2347042708025" ;
                String message = selected_item + ": Test Hello";
                //

                checkPermission(Manifest.permission.SEND_SMS, SMS_SEND_PERMISSION_CODE);
                Toast.makeText(MainActivity.this, "Alert Btn Click", Toast.LENGTH_SHORT).show();
                //
                sendSMS(phoneNumber, message);
            };
        });

        Switch switchEnableBtnAlarm= findViewById(R.id.btn_sw_enable);
        switchEnableBtnAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    activateAlertBtn.setEnabled(true);
                } else {
                    activateAlertBtn.setEnabled(false);
                }
            }
        });
    }

    // Function to check and request permission.
    public void checkPermission(String permission, int requestCode)
    {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(MainActivity.this, new String[] { permission }, requestCode);
        }
        else {
            Toast.makeText(MainActivity.this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }

    // This function is called when the user accepts or decline the permission.
    // Request Code is used to check which permission called this function.
    // This request code is provided when the user is prompt for permission.

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);

        if (requestCode == SMS_SEND_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "SMS SEND Permission Granted", Toast.LENGTH_SHORT) .show();
            }
            else {
                Toast.makeText(MainActivity.this, "SMS SEND Permission Denied", Toast.LENGTH_SHORT) .show();
            }
        }
        else if (requestCode == SMS_RECEIVED_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "SMS Received Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "SMS Received Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }

    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long l)
    {
      // On selecting a spinner item
        int rpt = parent.getCount();
        int selected_item_position = position;
        selected_item  =  parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(), "Selected item position " + position, Toast.LENGTH_LONG).show();
        Toast.makeText(parent.getContext(), "Selected: " + selected_item, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    //--sends an SMS message to another device---
    public void sendSMS(String phoneNumber, String message)
    {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";
        //PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,  new Intent(SENT), 0);
        // PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);
        //When the SMS has been sent---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS sent",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic failure",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));
        //---when the SMS has been delivered---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));
        //SmsManager sms = SmsManager.getDefault();
        //sms.sendTextMessage(phoneNumber, null, message, null, null);
        try {
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(getApplicationContext(), "Message Sent",Toast.LENGTH_LONG).show();
            //
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(),ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

}