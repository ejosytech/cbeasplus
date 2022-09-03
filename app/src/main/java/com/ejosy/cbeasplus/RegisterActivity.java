package com.ejosy.cbeasplus;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {
    private DatabaseHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //
        db = new DatabaseHelper(this);
        //
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //
        EditText txt_display_reg_status = (EditText) findViewById(R.id.txt_display_reg_status);
        //
        Button btn_register = findViewById(R.id.btn_register);
        btn_register.setEnabled(false);
        btn_register.setVisibility(View.GONE);
        //
        Button btn_verify_reg = findViewById(R.id.btn_verify_reg);
        btn_verify_reg.setEnabled(true);
        //

        btn_verify_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText phone_no =  (EditText) findViewById(R.id.phone_no);
                String var_phone_no = phone_no.getText().toString();
                Toast.makeText(RegisterActivity.this, "Phone Number to Verify " + var_phone_no, Toast.LENGTH_SHORT).show();

                if (!db.PhoneExist_subscription(var_phone_no.trim()))
                {
                    txt_display_reg_status.setText("You have not Subscribed, hence you cant enjoy this service");
                    btn_register.setVisibility(View.GONE);
                }
                else
                { //Subscription Confirmed
                    btn_register.setVisibility(View.VISIBLE);
                    btn_verify_reg.setVisibility(View.GONE);
                    //
                    if (db.PhoneExist_reg()) {//update
                        txt_display_reg_status.setText("Already Registered");
                        //
                        btn_register.setText("UPDATE");
                        btn_register.setEnabled(true);
                        //
                        //Toast.makeText(RegisterActivity.this, "Phone to be updated: " + var_phone_no, Toast.LENGTH_SHORT).show();

                    } else {//Insert new
                        //Toast.makeText(RegisterActivity.this, "New Phone Registered: " + var_phone_no, Toast.LENGTH_SHORT).show();
                        //
                        txt_display_reg_status.setText("Not Registered, Proceed to Register");
                        //
                        btn_register.setText("REGISTER");
                        btn_register.setEnabled(true);
                        //
                        //db.addClient_reg(var_phone_no.trim());

                    }
                }

                }

            }


        );


        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText phone_no =  (EditText) findViewById(R.id.phone_no);

                String var_phone_no = phone_no.getText().toString();
                Toast.makeText(RegisterActivity.this, "Phone Number to Verify " + var_phone_no, Toast.LENGTH_SHORT).show();
                //
                 //
                if (db.PhoneExist_reg()) {//update
                    //
                    Toast.makeText(RegisterActivity.this, "Prev Phone " + db.getContent_reg(), Toast.LENGTH_SHORT).show();
                    db.updateClient_reg(db.getContent_reg(), var_phone_no.trim());
                    txt_display_reg_status.setText("Updated");

                } else {//Insert new
                    db.addClient_reg(var_phone_no.trim());
                    txt_display_reg_status.setText("Registered");
                    //Once registered, Client can then use Application

                }
                //
                                              }

                                          }
        );
    }
}


