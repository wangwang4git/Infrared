package com.powergrid.victor.infraredpowergrid.infrared.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * Created by wangwang on 2017/9/17.
 */

public class ObjectSaveUtil {

    private final static String SP_FILENAME = "sp_object_save";

    public static final String LAST_MEASURE_USER = "last_measure_user";
    public static final String LAST_RECORD_USER = "last_record_user";

    public static void setLastMeasureUser(Context context, String lastMeasureUser) {
        if (TextUtils.isEmpty(lastMeasureUser)) {
            return;
        }

        SharedPreferences.Editor sp = context.getSharedPreferences(SP_FILENAME, 0).edit();
        sp.putString(LAST_MEASURE_USER, lastMeasureUser).commit();
    }

    public static String getLastMeasureUser(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_FILENAME, 0);
        return sp.getString(LAST_MEASURE_USER, "");
    }

    public static void setLastRecordUser(Context context, String lastRecordUser) {
        if (TextUtils.isEmpty(lastRecordUser)) {
            return;
        }

        SharedPreferences.Editor sp = context.getSharedPreferences(SP_FILENAME, 0).edit();
        sp.putString(LAST_RECORD_USER, lastRecordUser).commit();
    }

    public static String getLastRecordUser(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_FILENAME, 0);
        return sp.getString(LAST_RECORD_USER, "");
    }

}
