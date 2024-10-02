package com.example.idontknowwhattocallit.support;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    public static String formatDate(long dateInMillis) {
        // Tạo một đối tượng SimpleDateFormat với định dạng mong muốn
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        // Chuyển đổi giá trị milliseconds sang Date
        Date date = new Date(dateInMillis);
        // Định dạng và trả về chuỗi
        return sdf.format(date);
    }
}
