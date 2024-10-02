package com.example.idontknowwhattocallit.support;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Map;

public class BlockListCheck extends AppCompatActivity {


    public static int checkBlock(String number, ArrayList<String> numblocks){
        int check = 0;
        if(numblocks.contains(number)){
            check = 1;
        }
        return check;
    }

}
