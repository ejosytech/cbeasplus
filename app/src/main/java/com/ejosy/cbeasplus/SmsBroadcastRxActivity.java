package com.ejosy.cbeasplus;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SmsBroadcastRxActivity extends AppCompatActivity {
    private DatabaseHelper db;
    private MediaPlayer mediaPlayer;
    private String latitude_str;
    private String longitude_str;
    String Extract_signal = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new DatabaseHelper(this);
        String srcphone = db.getContent_reg();

        if (!db.PhoneExist_subscription(srcphone)) {
            // If Client has not subscribed access is denied
            exit_dialog("You have not subscribed \n Kindly Subscribe, Please ");
        } else {
            // Client Allowed access if subscribed

            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                //String router_mobile_no = extras.getString("MessageNumber");
                String message = extras.getString("Message");
                //
                Toast.makeText(SmsBroadcastRxActivity.this, "Message Received: " + message, Toast.LENGTH_SHORT).show();
                //
                // Message         :  090611350T89374218G75401254D##########
                // Message format  :  MMDDHHmmZT########G########D##########
                // MM - month, DD- Day, HH-Hour, mm-Minute, Z- Message Type, T-lat, G-Long
                String msg_date = message.substring(0,8);
                int msgValidityperiod = 10;
                if (msg_validity(msg_date,msgValidityperiod ))
                {validityMsg("Alert May No Longer be Valid \n Incidence Occured Over " + msgValidityperiod + "minutes ago");}
                //
                setContentView(R.layout.activity_sms_broadcast_rx);
                //
                //
                // Used Alarmist Call Number(address) to retrieve Client's Details
                // origin address sample = "D+2348033927733"
                String OrignAddrr = message.substring(message.indexOf("D")+5);
                Log.d("OrignAddrr", "OrignAddrr: " + OrignAddrr);

                ObjectClient objectClient;
                objectClient = db.readSingleRecord(OrignAddrr);

                Log.d("objectClient.name", "objectClient.name: " + objectClient.name);
               // Trigger Alarm


                latitude_str = message.substring(message.indexOf("T") + 1, message.indexOf("G"));
                longitude_str = message.substring(message.indexOf("G") + 1, message.indexOf("D"));
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


                final RippleBackground rippleBackground = (RippleBackground) findViewById(R.id.content);
                //MediaPlayer

                mediaPlayer = MediaPlayer.create(SmsBroadcastRxActivity.this, Settings.System.DEFAULT_RINGTONE_URI);

                mediaPlayer.start(); // no need to call prepare(); create() does that for you


                //try {
                //    mediaPlayer.prepare();
                //} catch (IOException e) {
               //     e.printStackTrace();
               // }
                //mediaPlayer.setVolume(1f, 1f);
                //mediaPlayer.setLooping(false);
                rippleBackground.startRippleAnimation();
                //mediaPlayer.start();
                //
                TextView txPhoneNo = findViewById(R.id.txPhoneNo);
                TextView txaddress = findViewById(R.id.txaddress);
                TextView txSentMsg = findViewById(R.id.txSentMsg);

                txPhoneNo.setText("Message From : " + objectClient.phone + "\n" + objectClient.name);
                txaddress.setText("Address: " + objectClient.avenue + " Avenue," + " " + objectClient.street);
                txSentMsg.setText(coreMsg(message.charAt(8)));
             //
                String rxTimeStamp = TimeStamp();
                String rxphone = objectClient.phone;
                String rxmsg = coreMsg(message.charAt(8));
                //
                Toast.makeText(SmsBroadcastRxActivity.this, "msg_date " +  msg_date, Toast.LENGTH_SHORT).show();
                Toast.makeText(SmsBroadcastRxActivity.this, "OrignAddrr " +  OrignAddrr, Toast.LENGTH_SHORT).show();
                Toast.makeText(SmsBroadcastRxActivity.this, "rxTimeStamp " +  rxTimeStamp, Toast.LENGTH_SHORT).show();
                Toast.makeText(SmsBroadcastRxActivity.this, "rxphone" +  rxphone, Toast.LENGTH_SHORT).show();
                Toast.makeText(SmsBroadcastRxActivity.this, "rx_msg " +  rxmsg, Toast.LENGTH_SHORT).show();
                Toast.makeText(SmsBroadcastRxActivity.this, "Extract_signal " +  Extract_signal, Toast.LENGTH_SHORT).show();
                Toast.makeText(SmsBroadcastRxActivity.this, "latitude_str " +  latitude_str, Toast.LENGTH_SHORT).show();
                Toast.makeText(SmsBroadcastRxActivity.this, "longitude_str " +  longitude_str, Toast.LENGTH_SHORT).show();

                //
                        String src_tx_timestamp = msg_date;
                        String src_tx_phone = OrignAddrr;
                        String dst_rx_timestamp = rxTimeStamp;
                        String dst_rx_phone = rxphone ;
                        String rx_msg = rxmsg;
                        String dst_rx_signal = Extract_signal;
                        String dst_rx_lat = latitude_str ;
                        String dst_rx_long = longitude_str;

                postRxPerformanceMetricsData(src_tx_timestamp, src_tx_phone,dst_rx_timestamp, dst_rx_phone, rx_msg,  dst_rx_signal, dst_rx_lat,dst_rx_long);


            }
        }

        Button btn_map = (Button) findViewById(R.id.btn_map);
        btn_map.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mediaPlayer.stop();

                Intent webintent = new Intent(getApplicationContext(), mapView.class);
                webintent.putExtra("lat_read", latitude_str);
                webintent.putExtra("long_read", longitude_str);
                startActivity(webintent);

            }

        });
    }
  private boolean msg_validity(String date_extract, int acceptable_duration)
    {
        boolean status = false;
        // Extract Receivers Current Time Stamp - Begin
        // Collect Data for logging
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        DateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        // get current date time with Date()
        Date date = new Date();
        String TodayDate = dateFormat.format(date);
        String TodayTime = timeFormat.format(date);
        //
        String hourx_str ="00", minutex_str = "00", dayx_str = "00", monthx_str = "00";
    // Extract Day, Month Hour and Minutes for Data Validity
        Calendar instance = Calendar.getInstance();

        int hourx = instance.get(Calendar.HOUR_OF_DAY);
        int minutex = instance.get(Calendar.MINUTE);
        int dayx = instance.get(Calendar.DAY_OF_MONTH);
        int monthx = instance.get(Calendar.MONTH);
        monthx = monthx + 1;
        //
        // Extract Receivers Current Time Stamp - End

        //
        int rxmsgMonth = Integer.parseInt(date_extract.substring(0, 2));
        int rxmsgDay = Integer.parseInt(date_extract.substring(1, 3));
        int rxmsgHour = Integer.parseInt(date_extract.substring(2, 4));
        int rxmsgMin = Integer.parseInt(date_extract.substring(3, 5));

        //

        status = (monthx == rxmsgMonth) && (dayx == rxmsgDay) && (hourx == rxmsgHour) && (minutex < rxmsgMin + acceptable_duration);
        return status;
    }

    private void exit_dialog(String msg) {
        // Create the object of AlertDialog Builder class
        AlertDialog.Builder builder = new AlertDialog.Builder(SmsBroadcastRxActivity.this);

        // Set the message show for the Alert time
        builder.setMessage(msg);

        // Set Alert Title
        builder.setTitle("Alert !");

        // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
        builder.setCancelable(false);

        // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
        builder.setPositiveButton("Ok", (DialogInterface.OnClickListener) (dialog, which) -> {
            // When the user click yes button then app will close
            finish();
        });

        // Create the Alert dialog
        AlertDialog alertDialog = builder.create();
        // Show the Alert Dialog box
        alertDialog.show();
    }

    private void validityMsg (String msg)
    {
            // Create the object of AlertDialog Builder class
            AlertDialog.Builder builder = new AlertDialog.Builder(SmsBroadcastRxActivity.this);

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

    private String coreMsg(char msg_no)
    {
        String selected_msg;
        switch(msg_no)
        {
            case '0':
                selected_msg = "Theft/Robbery";
                break;
            case '1' :
                selected_msg = "Health Issues";
                break;
            case '2' :
                selected_msg = "Fire Incidence";
                break;
            default :
                selected_msg = "";
        }
        return selected_msg;
    }

    private void postRxPerformanceMetricsData(String vsrc_tx_timestamp, String vsrc_tx_phone, String vdst_rx_timestamp, String vdst_rx_phone, String vrx_msg, String vdst_rx_signal, String vdst_rx_lat,String vdst_rx_long)
    {
        // url to post our Transmit performance metric Source data
        String url = "https://cbeas.ramsme.com/api/metrics-rx.php";

        Log.i("debugvolley-rx", "vsrc_tx_timestamp: " + vsrc_tx_timestamp);
        // creating a new variable for our request queue
        // Creating Volley newRequestQueue .
        RequestQueue requestQueue = Volley.newRequestQueue(SmsBroadcastRxActivity.this);

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
                        Log.i("debugvolley_rx", "No Error Response: " + obj.getString("message"));
                        Toast.makeText(getApplicationContext(), "No Error Response Message: " + obj.getString("message"), Toast.LENGTH_SHORT).show();

                    }
                    if(obj.getBoolean("error")) {
                        Log.i("debugvolley_rx", "Error Response: " + obj.getString("message"));
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
                Toast.makeText(SmsBroadcastRxActivity.this, "Fail to get response = " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // below line we are creating a map for
                // storing our values in key and value pair.
                Map<String, String> params = new HashMap<String, String>();

                // on below line we are passing our key
                // and value pair to our parameters.
                Log.i("debugvolley-getP_rx", "vdst_rx_timestamp: " + vdst_rx_timestamp);
                params.put("dst_rx_timestamp", vdst_rx_timestamp);
                params.put("dst_rx_phone", vdst_rx_phone);
                params.put("src_tx_timestamp", vsrc_tx_timestamp);
                params.put("src_tx_phone", vsrc_tx_phone);
                params.put("rx_msg", vrx_msg);
                params.put("dst_rx_signal", vdst_rx_signal);
                params.put("dst_rx_lat", vdst_rx_lat);
                params.put("dst_rx_long", vdst_rx_long);

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

    public String TimeStamp()
    {
        // Collect Data for logging
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        DateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        // get current date time with Date()
        Date date = new Date();
        String TodayDate = dateFormat.format(date);
        String TodayTime = timeFormat.format(date);
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
        { hourx_str = "0" + hourx;  }
        else
        { hourx_str = String.valueOf(hourx); }
        //
        if (minutex<10)
        { minutex_str = "0" + minutex; }
        else
        { minutex_str = String.valueOf(minutex); }
        //
        if (dayx<10)
        { dayx_str = "0" + dayx; }
        else
        { dayx_str = String.valueOf(dayx); }
        //
        if (monthx<10)
        { monthx_str = "0" + monthx; }
        else
        { monthx_str = String.valueOf(monthx); }
        //

        String timestamp = monthx_str  + dayx_str  + hourx_str + minutex_str;

        return timestamp;
    }

}


