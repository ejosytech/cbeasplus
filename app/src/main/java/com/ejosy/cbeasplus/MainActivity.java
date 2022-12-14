package com.ejosy.cbeasplus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ejosy.cbeasplus.Utilities.GPSTracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
    public static final int SMS_RECEIVED_PERMISSION_CODE = 101;

    //Spinner
    public String selected_item = "";
    public String selected_item_position = "";
    public String Extract_signal;
    public String src_tx_phone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //
        db = new DatabaseHelper(this);
        //Extract Stored Phone Number
        src_tx_phone = db.getContent_reg();
        //
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // REFRESH LOCAL DB
        LoadClients();

        //

        //Major Key to enable app usage
        //
        Switch switchEnableBtnAlarm= findViewById(R.id.btn_sw_enable);
        if (!db.PhoneExist_reg())
        {
            //if not registerd, Popup dialog to direct User to register
            //inorder to enable app usage
            //disable Switch
            String msgx = "Not Registered. \n Kindly register via the Option Menu \n Do you wish to Exit?";
            switchEnableBtnAlarm.setEnabled(false);
            exit_dialog(msgx);
        }
        else
        {
            switchEnableBtnAlarm.setEnabled(true);
        }
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
                //
                //checkPermission(Manifest.permission.READ_PHONE_STATE, READ_PHONE_STATE_PERMISSION_CODE);
                checkPermission(Manifest.permission.RECEIVE_SMS, SMS_RECEIVED_PERMISSION_CODE);
                checkPermission(Manifest.permission.SEND_SMS, SMS_SEND_PERMISSION_CODE);

                //
                GPSTracker gpsTracker = new GPSTracker(MainActivity.this);
                String GPSLocation = gpsTracker.getLocation();
                //
                String  msgTimeStamp =  TimeStamp();
                // Attach TimeStamp, Selected Message and GPSLocation
                String message = msgTimeStamp + selected_item_position + GPSLocation;

                //Toast.makeText(MainActivity.this, "Alert Btn Click", Toast.LENGTH_SHORT).show();
                //
                // Send Messages to All Suscribers
                Cursor Cptr = db.getContent();
                 while (Cptr.moveToNext())
                {

                 // @SuppressLint("Range") String phone = Cptr.getString(Cptr.getColumnIndex("phone"));
                    @SuppressLint("Range") String dst_rx_phone = Cptr.getString(0);
                    @SuppressLint("Range") String Name = Cptr.getString(1);
                    //Toast.makeText(MainActivity.this, "Phone:" + phone, Toast.LENGTH_SHORT).show();
                    //Toast.makeText(MainActivity.this, "Alert Sent to " + Name, Toast.LENGTH_SHORT).show();

                    //Toast.makeText(MainActivity.this, "Phone Signal " + Extract_signal, Toast.LENGTH_SHORT).show();
                    //Toast.makeText(MainActivity.this, "GPSLocation " + GPSLocation, Toast.LENGTH_SHORT).show();
                    //Toast.makeText(MainActivity.this, "Message TimeStamp " +  msgTimeStamp, Toast.LENGTH_SHORT).show();

                    //
                    //Messaging
                    //srcphone = GetNumber();
                 if (dst_rx_phone != "8033927733") {
                     String phoneNumber = "+234" + dst_rx_phone;

                     sendSMS(phoneNumber, message);
                     //String vsrc_tx_timestamp;
                     //String vsrc_tx_phone;
                     //String vdst_rx_phone;
                     //String vtx_msg;
                     //String vsrc_tx_signal;
                     String latv = GPSLocation.substring(GPSLocation.indexOf("T") + 1, GPSLocation.indexOf("G"));
                     String longv = GPSLocation.substring(GPSLocation.indexOf("G") + 1);
                     StringBuilder str_longv = new StringBuilder(longv);
                     //insert character value at offset 8
                     StringBuilder str_latv = new StringBuilder(latv);
                     str_latv.insert(1, '.');
                     str_longv.insert(1, '.');
                     //Extract_signal="do";

                     Toast.makeText(MainActivity.this, "msgTimeStamp " +  msgTimeStamp, Toast.LENGTH_SHORT).show();
                     Toast.makeText(MainActivity.this, "src_tx_phone " +  src_tx_phone, Toast.LENGTH_SHORT).show();
                     Toast.makeText(MainActivity.this, "dst_rx_phone " +  dst_rx_phone, Toast.LENGTH_SHORT).show();
                     Toast.makeText(MainActivity.this, "selected_item " +  selected_item, Toast.LENGTH_SHORT).show();
                     Toast.makeText(MainActivity.this, "Extract_signal " +  Extract_signal, Toast.LENGTH_SHORT).show();
                     Toast.makeText(MainActivity.this, "latv " +  latv, Toast.LENGTH_SHORT).show();
                     Toast.makeText(MainActivity.this, "longv " +  longv, Toast.LENGTH_SHORT).show();
                     // insert character value at offset 8
                    postTxPerformanceMetricsData(msgTimeStamp, src_tx_phone, dst_rx_phone, selected_item, Extract_signal, latv , longv );
                 }

                    //Toast.makeText(MainActivity.this, "Src Phone " +  srcphone, Toast.LENGTH_SHORT).show();
                    //Toast.makeText(MainActivity.this, "Message " +  message, Toast.LENGTH_SHORT).show();
                    //Logging of Performance Metrics
                    //logdat();
                }


            };
        });


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




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.register:
                //Toast.makeText(getApplicationContext(),"Item 1 Selected",Toast.LENGTH_LONG).show();
                Intent myIntent = new Intent(MainActivity.this, RegistryPrefrenceActivity.class);
                //myIntent.putExtra("key", value); //Optional parameters
                MainActivity.this.startActivity(myIntent);
                return true;

            case R.id.exit:
                Toast.makeText(getApplicationContext(),"Item 2 Selected",Toast.LENGTH_LONG).show();
                String msg_display = "Do you really want to Exit";
                exit_dialog(msg_display);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void exit_dialog(String msg)
    {
        // Create the object of AlertDialog Builder class
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        // Set the message show for the Alert time
        builder.setMessage(msg);

        // Set Alert Title
        builder.setTitle("Alert !");

        // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
        builder.setCancelable(false);

        // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
        builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
            // When the user click yes button then app will close
            finish();
        });

        // Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.
        builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
            // If user click no then dialog box is canceled.
            dialog.cancel();
        });

        // Create the Alert dialog
        AlertDialog alertDialog = builder.create();
        // Show the Alert Dialog box
        alertDialog.show();
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
        selected_item_position = String.valueOf(position);;
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
                                db.addClient(clients.getInt("id"),clients.getString("phone"), clients .getString("name"), clients.getString("designation"), clients.getString("avenue"),clients.getString("street") );

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Data may not be updated. \n No Internet Connectivity",Toast.LENGTH_LONG).show();
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

    public String TimeStamp()
    {
        // Collect Data for logging
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        DateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        // get current date time with Date()
        Date date = new Date();
        String TodayDate = dateFormat.format(date).toString();
        String TodayTime = timeFormat.format(date).toString();
        //
        String hourx_str ="00";
        String minutex_str = "00";
        String dayx_str = "00";
        String monthx_str = "00";


        // Extract Day, Month Hour and Minutes for Data Validity
        Calendar instance = Calendar.getInstance();

        int hourx = instance.get(Calendar.HOUR_OF_DAY);
        int minutex = instance.get(Calendar.MINUTE);
        int dayx = instance.get(Calendar.DAY_OF_MONTH);
        int monthx = instance.get(Calendar.MONTH);
        monthx = monthx + 1;
        //
        if (hourx<10)
        { hourx_str = "0" + String.valueOf(hourx);  }
        else
        { hourx_str = String.valueOf(hourx); }
        //
        if (minutex<10)
        { minutex_str = "0" + String.valueOf(minutex); }
        else
        { minutex_str = String.valueOf(minutex); }
        //
        if (dayx<10)
        { dayx_str = "0" + String.valueOf(dayx); }
        else
        { dayx_str = String.valueOf(dayx); }
        //
        if (monthx<10)
        { monthx_str = "0" + String.valueOf(monthx); }
        else
        { monthx_str = String.valueOf(monthx); }
        //

        String timestamp = monthx_str  + dayx_str  + hourx_str + minutex_str;

        return timestamp;
    }



    private void postTxPerformanceMetricsData(String vsrc_tx_timestamp, String vsrc_tx_phone, String vdst_rx_phone, String vtx_msg, String vsrc_tx_signal, String vsrc_tx_lat,String vsrc_tx_long)
    {
        // url to post our Transmit performance metric Source data
        String url = "https://cbeas.ramsme.com/api/metrics-tx.php";

        Log.i("debugvolley", "vsrc_tx_timestamp: " + vsrc_tx_timestamp);
        // creating a new variable for our request queue
        // Creating Volley newRequestQueue .
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

        // on below line we are calling a string
        // request method to post the data to our API
        // in this we are calling a post method.
        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                // on below line we are displaying a success toast message.
                //Toast.makeText(MainActivity.this, "Data added to API", Toast.LENGTH_SHORT).show();
                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(response);
                    //if no error in response
                    if(!obj.getBoolean("error")) {
                        Log.i("debugvolley", "No Error Response: " + obj.getString("message"));
                        Toast.makeText(getApplicationContext(), "No Error Response Message: " + obj.getString("message"), Toast.LENGTH_SHORT).show();

                    }
                    if(obj.getBoolean("error")) {
                        Log.i("debugvolley", "Error Response: " + obj.getString("message"));
                        Toast.makeText(getApplicationContext(), "Error Response Message: " + obj.getString("message"), Toast.LENGTH_SHORT).show();

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // method to handle errors.
                Toast.makeText(MainActivity.this, "Fail to get response = " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // below line we are creating a map for
                // storing our values in key and value pair.
                Map<String, String> params = new HashMap<String, String>();

                // on below line we are passing our key
                // and value pair to our parameters.
                Log.i("debugvolley-getParams", "vsrc_tx_timestamp: " + vsrc_tx_timestamp);
                params.put("src_tx_timestamp", vsrc_tx_timestamp);
                params.put("src_tx_phone", vsrc_tx_phone);
                params.put("dst_rx_phone", vdst_rx_phone);
                params.put("tx_msg", vtx_msg);
                params.put("src_tx_signal", vsrc_tx_signal);
                params.put("src_tx_lat", vsrc_tx_lat);
                params.put("src_tx_long", vsrc_tx_long);

                // at last we are
                // returning our params.
                return params;
            }
        };
        // below line is to make
        // Creating RequestQueue.

        // Adding the StringRequest object into requestQueue.
        requestQueue.add(request);

    }



    @Override
    public void onResume(){
        super.onResume();
        // put your code here...
        Switch switchEnableBtnAlarm= findViewById(R.id.btn_sw_enable);
        switchEnableBtnAlarm.setEnabled(true);

    }
}