package com.ejosy.cbeasplus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegistryPrefrenceActivity extends AppCompatActivity {
    private EditText mobile_line;
    private TextView txt_display_reg_status;
    private Button btn_verify;
    private DatabaseHelper db;
    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prefrence_registry);
        //
        db = new DatabaseHelper(this);
        //
        mobile_line = findViewById(R.id.editTextTextMobile);
        txt_display_reg_status = findViewById(R.id.txt_pref_display_reg_status);

        //
        btn_verify = findViewById(R.id.btn_verify);
        //
        btn_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String var_phone_no = mobile_line.getText().toString();
                //Toast.makeText(RegistryPreferenceActivity.this, "Phone Number to Verify " + var_phone_no, Toast.LENGTH_SHORT).show();

                if (!db.PhoneExist_subscription(var_phone_no.trim()) || var_phone_no.isEmpty())
                {
                    txt_display_reg_status.setText("You have not Subscribed, hence you cant enjoy this service");

                }
                else
                { //Subscription Confirmed
                    //
                    if (db.PhoneExist_reg()) {//update
                        txt_display_reg_status.setText("Already Registered");
                        //

                        //Toast.makeText(RegisterActivity.this, "Phone to be updated: " + var_phone_no, Toast.LENGTH_SHORT).show();

                    } else {//Insert new
                        //Toast.makeText(RegisterActivity.this, "New Phone Registered: " + var_phone_no, Toast.LENGTH_SHORT).show();
                        //
                        txt_display_reg_status.setText("Not Registered, Proceed to Register");

                        //
                        //db.addClient_reg(var_phone_no.trim());

                    }
                }

            }

        }

        );}

    // Fetch the stored data in onResume()
    // Because this is what will be called
    // when the app opens again
    @Override
    protected void onResume() {
        super.onResume();

        // Fetching the stored data
        // from the SharedPreference
        SharedPreferences sh = getSharedPreferences("Prefclient", MODE_PRIVATE);

        String  mobile_line_value = sh.getString("mobile_line", "");
        // Setting the fetched data
        // in the EditTexts
        mobile_line.setText(mobile_line_value);

    }

    // Store the data in the SharedPreference
    // in the onPause() method
    // When the user closes the application
    // onPause() will be called
    // and data will be stored
    @Override
    protected void onPause() {
        super.onPause();

        // Creating a shared pref object
        // with a file name "MySharedPref"
        // in private mode
        SharedPreferences sharedPreferences = getSharedPreferences("Prefclient", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();

        // write all the data entered by the user in SharedPreference and apply
        myEdit.putString("mobile_line", mobile_line.getText().toString());
        myEdit.apply();
    }



}