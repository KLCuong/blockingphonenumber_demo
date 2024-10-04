package com.example.idontknowwhattocallit.Callog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.idontknowwhattocallit.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Call_Log_profile extends AppCompatActivity {
    private String name;
    private TextView textView;
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_log_profile);
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        imageView = findViewById(R.id.ivback);
        textView = findViewById(R.id.textView3);
        textView.setText(name);
        imageView.setOnClickListener(v -> {
            Intent intent1 = new Intent(this, Call_Log.class);
            startActivity(intent1);
        });
    }


}
