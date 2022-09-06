package com.ejosy.cbeasplus;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SmsBroadcastRxActivity extends AppCompatActivity {
    private DatabaseHelper db;
    private MediaPlayer mediaPlayer;

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
            }
        }
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
        String TodayDate = dateFormat.format(date).toString();
        String TodayTime = timeFormat.format(date).toString();
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

        if ((monthx == rxmsgMonth) && (dayx == rxmsgDay) && (hourx == rxmsgHour) && (minutex < rxmsgMin + acceptable_duration) )
        {status = true;}
        else
        {status = false;}
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

}


