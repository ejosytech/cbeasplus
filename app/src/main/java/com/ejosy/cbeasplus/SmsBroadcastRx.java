package com.ejosy.cbeasplus;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class SmsBroadcastRx extends BroadcastReceiver {

    /**
     * Called when the BroadcastReceiver is receiving an Intent broadcast.
     *
     * @param context  The Context in which the receiver is running.
     * @param intent   The Intent received.
     */

    private static final String TAG = SmsBroadcastRx.class.getSimpleName();
    public static final String pdu_type = "pdus";

    @TargetApi(Build.VERSION_CODES.M)
// Get the object of SmsManager

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
       // throw new UnsupportedOperationException("Not yet implemented");
        // Get the SMS message.
        // Get the SMS message.
        Bundle bundle = intent.getExtras();
        String strMessage = "";
        String  straddr ="";
        String  strmsg = "";

        String format = bundle.getString("format");
        // Retrieve the SMS message received.
        Object[] pdus = (Object[]) bundle.get(pdu_type);
        if (pdus != null) {
            // Check the Android version.
            boolean isVersionM = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
            // Fill the msgs array.
            SmsMessage[] msgs = new SmsMessage[pdus.length];
            for (int i = 0; i < msgs.length; i++) {
                // Check Android version and use appropriate createFromPdu.
                if (isVersionM) {
                    // If Android version M or newer:
                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                } else {
                    // If Android version L or older:
                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }
                //
                // Start Application's  MainActivty activity

                //
                // Build the message to show.
                //T indicate begining of Timestamp
                //P indicate begining of Phone number
                //B indicate body of message

                
                straddr +=    msgs[i].getOriginatingAddress();
                strMessage += msgs[i].getMessageBody();

                // Log and display the SMS message.
                Log.d(TAG, "onReceive: " + strMessage);
                Toast.makeText(context, strMessage, Toast.LENGTH_LONG).show();
                // Start Application's  MainActivty activity
//
                Intent smsIntent=new Intent(context,SmsBroadcastRxActivity.class);
                smsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //


                    smsIntent.putExtra("Message",strMessage);
                    context.startActivity(smsIntent);



            }


        }
    }
}