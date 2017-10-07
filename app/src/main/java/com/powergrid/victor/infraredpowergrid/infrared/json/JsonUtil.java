package com.powergrid.victor.infraredpowergrid.infrared.json;

import android.content.Context;

import com.alibaba.fastjson.JSONArray;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by wangwang on 2017/9/17.
 */

public class JsonUtil {

    public static final String JSON_PATH = "/sdcard/device_interval.json";

    public static String getJsonStr(Context context) {
        StringBuffer json_str = new StringBuffer();
        try {
            File json_file = new File(JSON_PATH);
            InputStream is = new FileInputStream(json_file);
            InputStreamReader input = new InputStreamReader(is, "UTF-8");
            BufferedReader reader = new BufferedReader(input);
            String str;
            while ((str = reader.readLine()) != null) {
                json_str.append(str);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return json_str.toString();
    }

    public static List<DeviceInterval> getDeviceIntervals(String json_str) {
        List<DeviceInterval> deviceIntervals = JSONArray.parseArray(json_str, DeviceInterval.class);
        return deviceIntervals;
    }

}
