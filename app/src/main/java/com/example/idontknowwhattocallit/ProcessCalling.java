package com.example.idontknowwhattocallit;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ProcessCalling extends AppCompatActivity {
    String phonenumber;
    TextView phonenumber_TV;
    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.processcalling);
        phonenumber = getIntent().getStringExtra("number");
        phonenumber_TV = (TextView)findViewById(R.id.Phonenumber_TV);
    }

    public void ShowPhonenumber(View view){
        if(phonenumber !=null){
            phonenumber_TV.setText(phonenumber);
        }else{
            Toast.makeText(this, "No number received", Toast.LENGTH_SHORT).show();
        }
    }
}
