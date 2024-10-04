package com.example.idontknowwhattocallit;

import static android.content.Context.MODE_PRIVATE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.example.idontknowwhattocallit.support.BlockListCheck;
import com.example.idontknowwhattocallit.telephony.ITelephony;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;

public class MyPhoneReceiver extends BroadcastReceiver {
    Context context;
    SharedPreferences prfs;
    Map<String, ?> map;
    ArrayList<String> numblocks;
    private ITelephony telephonyService;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        // Gọi processCall trên luồng nền
        new Thread(() -> processCall(context, intent)).start();

    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void processCall(Context context, Intent intent) {
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        if(state.equals(TelephonyManager.EXTRA_STATE_RINGING)){
            // Hiển thị Toast trên luồng chính
            /*new Handler(Looper.getMainLooper()).post(() ->
                    Toast.makeText(context, "Call incoming", Toast.LENGTH_SHORT).show());*/

            String incomingnumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

            if(incomingnumber !=null){
                prfs = context.getSharedPreferences("number", MODE_PRIVATE);
                map = prfs.getAll();
                numblocks = new ArrayList<>();
                if(!map.isEmpty()) {
                    for (Map.Entry<String, ?> entry : map.entrySet()) {
                        numblocks.add(entry.getKey());
                    }
                }
                int check = BlockListCheck.checkBlock(incomingnumber,numblocks);
                if(check == 1){
                    new Handler(Looper.getMainLooper()).post(() ->
                            Toast.makeText(context, "Call blocked", Toast.LENGTH_SHORT).show());
                    // Từ chối cuộc gọi
                    rejectCall(context,incomingnumber);
                    return;
                }else {
                    // Khởi tạo ProcessCalling Activity trên luồng nền
                    Intent intent1 = new Intent(context, ProcessCalling.class);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent1.putExtra("number", incomingnumber);
                    // Khởi chạy Activity trên luồng chính
                    context.startActivity(intent1);
                }
            }

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void rejectCall(Context context, String incomingnumber) {
        // Get the TelephonyManager instance

        /*TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            // Get the class object of TelephonyManager
            Class<?> c = Class.forName(tm.getClass().getName());

            // Access the hidden method "getITelephony" using reflection
            Method m = c.getDeclaredMethod("getITelephony");

            // Make the method accessible as it's not public
            m.setAccessible(true);

            // Invoke the method to get the ITelephony instance
            telephonyService = (ITelephony) m.invoke(tm);

            // Get the incoming number from the intent

            String phoneNumber = incomingnumber;

            // Log the incoming number
            Log.d("INCOMING", phoneNumber);

            // End the call
            telephonyService.endCall();
            Log.d("HANG UP", phoneNumber);

        } catch (Exception e) {
            e.printStackTrace(); // Catch and print any exception that occurs
        }*/
        TelecomManager tm = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
        if (tm != null) {
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ANSWER_PHONE_CALLS) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            boolean success = tm.endCall();
            // success == true if call was terminated.
        }


    }

}