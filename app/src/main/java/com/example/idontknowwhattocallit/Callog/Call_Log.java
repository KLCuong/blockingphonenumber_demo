package com.example.idontknowwhattocallit.Callog;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.idontknowwhattocallit.MainActivity;
import com.example.idontknowwhattocallit.R;

import java.util.ArrayList;

public class Call_Log extends AppCompatActivity {
    ListView listView;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> arrayList;
    Button btnback;
    ContentResolver contentResolver;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_log);
        listView = findViewById(R.id.LV2);
        context = this;
        btnback = (Button)findViewById (R.id.btnback2);
        try {
            getData();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        btnback.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(context, Call_Log_profile.class);
                        intent.putExtra("name", arrayList.get(position));
                        startActivity(intent);
                    }
                }
        );
    }

    private void getData() throws Exception {
        arrayList = new ArrayList<>();
        contentResolver = getContentResolver(); // URI truy vấn danh bạ
        Uri uri = ContactsContract.Contacts.CONTENT_URI;

        // Các cột cần lấy
        String[] projection = new String[]{
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME
        };

        // Điều kiện lọc: Chỉ lấy liên hệ có số điện thoại
        String selection = ContactsContract.Contacts.HAS_PHONE_NUMBER + " > 0";

        // Truy vấn danh bạ
        Cursor cursor = contentResolver.query(uri, projection, selection, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                try {
                    // Lấy chỉ số cột contactId và displayName với try-catch
                    String contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                    String displayName = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));

                    // Lấy số điện thoại của liên hệ
                    Uri phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                    String phoneSelection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?";
                    Cursor phoneCursor = contentResolver.query(phoneUri, null, phoneSelection, new String[]{contactId}, null);

                    if (phoneCursor != null && phoneCursor.getCount() > 0) {
                        while (phoneCursor.moveToNext()) {
                            // Lấy số điện thoại với try-catch
                            String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            String contactInfo = displayName + "\n " + phoneNumber;
                            arrayList.add(contactInfo);
                        }
                        phoneCursor.close();
                    }

                } catch (Exception e) {
                    // Ghi log hoặc xử lý lỗi nếu xảy ra ngoại lệ
                    e.printStackTrace();
                    throw e; // Quăng lại ngoại lệ nếu muốn xử lý tiếp ngoài hàm
                }
            }
            cursor.close();
        } else {
            arrayList.add("No contact");
        }

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(arrayAdapter);
    }

}
