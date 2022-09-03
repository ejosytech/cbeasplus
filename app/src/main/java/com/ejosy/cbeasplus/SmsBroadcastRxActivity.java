package com.ejosy.cbeasplus;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class SmsBroadcastRxActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_broadcast_rx);

        //
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            //String router_mobile_no = extras.getString("MessageNumber");
            String message = extras.getString("Message");
            //
            Toast.makeText(SmsBroadcastRxActivity.this, "Message Received: " + message , Toast.LENGTH_SHORT).show();

            TextView txPhoneNo = findViewById(R.id.txPhoneNo);
            TextView txSentMsg = findViewById(R.id.txSentMsg);

            txPhoneNo.setText(message);

        }
    }
}