package com.example.idontknowwhattocallit;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.idontknowwhattocallit.Callog.Call_Log;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText editText;
    private Button button,historybtn,btn3,button2;
    private TextView textView;
    private ListView listView;
    private ArrayList<String> arrayList;
    private String number;
    SharedPreferences prfs;
    Map<String, ?> map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainactivity);
        prfs = getSharedPreferences("number", MODE_PRIVATE);
        map = prfs.getAll();


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ANSWER_PHONE_CALLS) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.ANSWER_PHONE_CALLS,
                    Manifest.permission.READ_CALL_LOG,
                    Manifest.permission.READ_CONTACTS
            }, 1);
        } else {
            connectView();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0) {
                boolean allGranted = true;
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        allGranted = false;
                        break;
                    }
                }
                if (allGranted) {
                    connectView();
                } else {
                    // Có quyền không được cấp, xử lý theo ý bạn
                    textView.setText("All permissions are required to run this app.");
                    finishAffinity();
                }
            }
        }
    }


    private void getData() {
        arrayList = new ArrayList<>();
        if(!map.isEmpty()) {
            for (Map.Entry<String, ?> entry : map.entrySet()) {
                arrayList.add(entry.getKey());
            };
        }else{
            arrayList.add("No number in blocklist");
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(arrayAdapter);
    }

    private void connectView() {
        listView = findViewById(R.id.listView);
        editText = findViewById(R.id.editTextText);
        textView = findViewById(R.id.textView);
        button = findViewById(R.id.button);
        historybtn = findViewById(R.id.history);
        btn3 = findViewById(R.id.call_log);
        button2 = findViewById(R.id.button2);
        editText.setText("");
        getData();
        button.setOnClickListener(this);
        historybtn.setOnClickListener(this);
        btn3.setOnClickListener(this);
        button2.setOnClickListener(this);
    }

    private void blockNumber() {
        SharedPreferences.Editor editor = prfs.edit();
        String number = editText.getText().toString();
        int check = CheckNumber(number);
        if(check == 0){
            int check2 = checkblock(number);
            if(check2 == 1){
                textView.setText("This number is already in blocklist");
            }else{
                editor.putString(number, number);
                editor.commit();
                textView.setText("Block successfully");
            }
        }
    }
    private int checkblock(String number){
        String c = number.toString();
        if(prfs.contains(c)){
            return 1;
        }else{
            return 0;
        }
    }
    private void unblockNumber() {
        SharedPreferences.Editor editor = prfs.edit();
        String number = editText.getText().toString();
        int check = CheckNumber(number);
        if(check == 0){
            int check2 = checkblock(number);
            if(check2 == 0){
                textView.setText("This number is not in blocklist");
            }else{
                editor.remove(number);
                editor.commit();
                textView.setText("Unblock successfully");
            }
        }

    }
    private int CheckNumber(String number) {
        if (number.isEmpty()) {
            textView.setText("Please enter a number");
            return 1;
        } else {
            if (number.length() < 10 || number.length() > 13 || !number.matches("[0-9]+")) {
                textView.setText("Please enter a valid number");
                return 1;
            }else return 0;
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.button) blockNumber();
        else if(id == R.id.history) {
            Intent intent = new Intent(this, HistoryCall.class);
            startActivity(intent);
        }else if(id == R.id.button2){
            unblockNumber();
        }else if(id == R.id.call_log){
            Intent intent = new Intent(this, Call_Log.class);
            startActivity(intent);
        } else{
            finishAffinity();
        }


    }
}
