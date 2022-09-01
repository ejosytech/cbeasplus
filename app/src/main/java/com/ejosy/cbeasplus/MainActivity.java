package com.ejosy.cbeasplus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ejosy.cbeasplus.Utilities.GPSTracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;




public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    //database helper object
    private DatabaseHelper db;
    //this is the JSON Data URL
    //make sure you are using the correct ip else it will not work
    private static final String URL_CLIENTS = "https://cbeas.ramsme.com/api/subscribers.php";
    // Defining Permission codes.
    // We can give any value
    // but unique for each permission.
    private static final int SMS_SEND_PERMISSION_CODE = 100;
    private static final int SMS_RECEIVED_PERMISSION_CODE = 101;
    //Spinner
    public String selected_item = "";
    public String Extract_signal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //
        db = new DatabaseHelper(this);
        //
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // REFRESH LOCAL DB
        LoadClients();
        //
        //SIGNAL STRENGHT EXTRACT
        final TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        telephonyManager.listen(new PhoneStateListener() {

            @Override
            public void onSignalStrengthsChanged(SignalStrength strength) {
                super.onSignalStrengthsChanged(strength);

                if (strength.isGsm()) {
                    String[] parts = strength.toString().split(" ");
                    String signalStrength = "";
                    int currentStrength = strength.getGsmSignalStrength();
                    if (currentStrength <= 0) {
                        if (currentStrength == 0) {
                            signalStrength = String.valueOf(Integer.parseInt(parts[3]));
                        } else {
                            signalStrength = String.valueOf(Integer.parseInt(parts[1]));
                        }
                        signalStrength += " dBm";
                    } else {
                        if (currentStrength != 99) {
                            signalStrength = String.valueOf(((2 * currentStrength) - 113));
                            signalStrength += " dBm";
                            Extract_signal = signalStrength;
                        }
                    }
                    //signal = (2 * signal) - 113;
                    System.out.println("Signal strength is : " + signalStrength);
                    System.out.println("Extract_signal : " + Extract_signal);
                    Extract_signal = signalStrength;
                } else {
                    Extract_signal= "Not GSM Signal";
                }
            }
        }, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        //
        //

        //
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

        Button activateAlertBtn = findViewById(R.id.btnSendAlert);
        activateAlertBtn.setEnabled(false);
        activateAlertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = selected_item + ": Test Hello";
                //
                GPSTracker gpsTracker = new GPSTracker(MainActivity.this);
                String GPSLocation = gpsTracker.getLocation();
                //

                checkPermission(Manifest.permission.SEND_SMS, SMS_SEND_PERMISSION_CODE);
                //Toast.makeText(MainActivity.this, "Alert Btn Click", Toast.LENGTH_SHORT).show();
                //
                // Send Messages to All Suscribers
                Cursor Cptr = db.getContent();
                 while (Cptr.moveToNext())
                {

                 // @SuppressLint("Range") String phone = Cptr.getString(Cptr.getColumnIndex("phone"));
                    @SuppressLint("Range") String phone = Cptr.getString(0);
                    @SuppressLint("Range") String Name = Cptr.getString(1);
                    //Toast.makeText(MainActivity.this, "Phone:" + phone, Toast.LENGTH_SHORT).show();
                    Toast.makeText(MainActivity.this, "Alert Sent to " + Name, Toast.LENGTH_SHORT).show();

                    Toast.makeText(MainActivity.this, "Phone Signal " + Extract_signal, Toast.LENGTH_SHORT).show();
                    Toast.makeText(MainActivity.this, "GPSLocation " + GPSLocation, Toast.LENGTH_SHORT).show();

                    String phoneNumber = "+234" + phone;
                 // sendSMS(phoneNumber, message);


                }


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
                //Toast.makeText(MainActivity.this, "SMS SEND Permission Granted", Toast.LENGTH_SHORT) .show();
            }
            else {
                Toast.makeText(MainActivity.this, "SMS SEND Permission Denied", Toast.LENGTH_SHORT) .show();
            }
        }
        else if (requestCode == SMS_RECEIVED_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(MainActivity.this, "SMS Received Permission Granted", Toast.LENGTH_SHORT).show();
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
        // Toast.makeText(parent.getContext(), "Selected item position " + position, Toast.LENGTH_LONG).show();
       // Toast.makeText(parent.getContext(), "Selected: " + selected_item, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    // REFRESH CLIENTS
    private void LoadClients() {

        /* https://google.github.io/volley/
         * Creating a String Request
         * The request type is GET defined by first parameter
         * The URL is defined in the second parameter
         * Then we have a Response Listener and a Error Listener
         * In response listener we will get the JSON response as a String
         * */
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_CLIENTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting the string to json array object
                            JSONArray array = new JSONArray(response);
                            //Clean up SQLite
                            db.delete();
                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {
                                //Toast.makeText(getApplicationContext(), "Volley Process",Toast.LENGTH_LONG).show();
                                //getting product object from json array
                                JSONObject clients = array.getJSONObject(i);

                                //Fill SQLite DB with extraxted version from portal
                                db.addClient(clients.getInt("id"),clients .getString("phone"), clients .getString("name"), clients.getString("designation"), clients.getString("avenue"),clients.getString("street") );

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Volley Error Occured",Toast.LENGTH_LONG).show();
                    }
                });

        //adding our stringrequest to queue
        Volley.newRequestQueue(this).add(stringRequest);
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
            //Toast.makeText(getApplicationContext(), "Message Sent",Toast.LENGTH_LONG).show();
            //
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(),ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }



}