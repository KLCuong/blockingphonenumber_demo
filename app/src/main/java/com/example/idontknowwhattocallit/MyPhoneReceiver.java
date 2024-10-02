package com.example.idontknowwhattocallit;

import static android.content.Context.MODE_PRIVATE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

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

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        // Gọi processCall trên luồng nền
        new Thread(() -> processCall(context, intent)).start();

    }

    private void processCall(Context context, Intent intent) {
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        if(state.equals(TelephonyManager.EXTRA_STATE_RINGING)){
            // Hiển thị Toast trên luồng chính
            new Handler(Looper.getMainLooper()).post(() ->
                    Toast.makeText(context, "Call incoming", Toast.LENGTH_SHORT).show());

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

    private void rejectCall(Context context,String incomingnumber) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Class<?> c = Class.forName(tm.getClass().getName());
            Method m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            ITelephony telephonyService = (ITelephony) m.invoke(tm);


            // Ghi lại số điện thoại vào log
            Log.d("INCOMING", incomingnumber);

            // Kết thúc cuộc gọi mà không cần kiểm tra phoneNumber có null hay không
            telephonyService.endCall();
            Log.d("HANG UP", incomingnumber);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}