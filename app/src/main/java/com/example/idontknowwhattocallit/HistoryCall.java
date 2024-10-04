package com.example.idontknowwhattocallit;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.idontknowwhattocallit.support.DateUtils;

import java.util.ArrayList;
import java.util.Collections;

public class HistoryCall extends AppCompatActivity {
    ListView listView;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> arrayList;
    Button btnback;
    Context context;
    SharedPreferences prfs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.historycontact);
        context = this;
        prfs = getSharedPreferences("number", MODE_PRIVATE);
        listView = findViewById(R.id.history_lv);
        btnback = (Button)findViewById (R.id.Backbtn);
        btnback.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
        DataSet();
    }

    private void DataSet() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED){
            // Truy vấn CallLog để lấy lịch sử cuộc gọi
            Cursor cursor = getContentResolver().query(android.provider.CallLog.Calls.CONTENT_URI,null,null,null,null);

            int numberIndex = cursor.getColumnIndex(CallLog.Calls.NUMBER);
            int typeIndex = cursor.getColumnIndex(CallLog.Calls.TYPE);
            int dateIndex = cursor.getColumnIndex(CallLog.Calls.DATE);
            int durationIndex = cursor.getColumnIndex(CallLog.Calls.DURATION);

            arrayList = new ArrayList<>();
            while(cursor.moveToNext()){
                String number = cursor.getString(numberIndex);
                String type = cursor.getString(typeIndex);
                long dateInMillis = cursor.getLong(dateIndex);
                String formattedDate = DateUtils.formatDate(dateInMillis);
                String duration = cursor.getString(durationIndex);

                // Chuyển đổi kiểu cuộc gọi (gọi đi, gọi đến, nhỡ)
                int Calltype = Integer.parseInt(type);
                String CalltypeString = null;
                switch (Calltype) {
                    case CallLog.Calls.OUTGOING_TYPE:
                        CalltypeString = "Outgoing";
                        break;
                    case CallLog.Calls.INCOMING_TYPE:
                        CalltypeString = "Incoming";
                        break;
                    case CallLog.Calls.MISSED_TYPE:
                        CalltypeString = "Missed";
                        break;
                    case CallLog.Calls.REJECTED_TYPE:
                        CalltypeString = rejectbyblock(number);
                        break;
                    case CallLog.Calls.BLOCKED_TYPE:
                        CalltypeString = "Blocked";
                        break;
                }

                // Thêm thông tin cuộc gọi vào ArrayList
                String callDetails = "Number: " + number + "\nType: " + CalltypeString +"\nDate: " + formattedDate + "\nDuration: "  + duration + " seconds";
                arrayList.add(callDetails);

            }
            cursor.close();
            Collections.reverse(arrayList);
        }else{
            arrayList.add("No permission");
        }
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(arrayAdapter);
    }

    private String rejectbyblock(String number) {
        if(prfs.contains(number)){
            return "Rejected by blocklist";
        }else return "Rejected";
    }
}
