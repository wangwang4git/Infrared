package com.powergrid.victor.infraredpowergrid.infrared.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.powergrid.victor.infraredpowergrid.infrared.R;
import com.powergrid.victor.infraredpowergrid.infrared.json.DeviceInterval;
import com.powergrid.victor.infraredpowergrid.infrared.json.JsonUtil;

import java.io.Serializable;
import java.util.List;

public class PreviewSettingActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "PreviewSettingActivity";
    public static final String DEVICE_INTERVAL_NAME = "DEVICE_INTERVAL_NAME";

    private List<DeviceInterval> mDeviceIntervals;
    private EditText mLoadValueEdt;
    private EditText mEnvTempEdt;
    private TextView mDeviceIntervalBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_setting);

        String json_str = JsonUtil.getJsonStr(this);
        if (TextUtils.isEmpty(json_str)) {
            Toast.makeText(this, "请将设备间隔json文件存入sdcard目录", Toast.LENGTH_SHORT).show();
        }
//        Log.i(TAG, json_str);

        mDeviceIntervals = JsonUtil.getDeviceIntervals(json_str);
        for (DeviceInterval deviceInterval : mDeviceIntervals) {
            Log.i(TAG, deviceInterval.toString());
        }

        mLoadValueEdt = (EditText) findViewById(R.id.setting_load_value_edt);
        mEnvTempEdt = (EditText) findViewById(R.id.setting_env_temp_edt);
        mDeviceIntervalBtn = (TextView) findViewById(R.id.setting_device_interval_btn);
        mDeviceIntervalBtn.setOnClickListener(this);

        mLoadValueEdt.setText(getIntent().getStringExtra(PreviewActivity.LOAD_VALUE));
        mEnvTempEdt.setText(getIntent().getStringExtra(PreviewActivity.ENV_TEMP));
        mDeviceIntervalBtn.setText(getIntent().getStringExtra(PreviewActivity.DEVICE_INTERVAL));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_device_interval_btn: {
                Intent intent = new Intent(this, DevicesOneListActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(DevicesOneListActivity.DATA_SOURCE, (Serializable) mDeviceIntervals);
                intent.putExtras(bundle);
                startActivityForResult(intent, 123);
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == Activity.RESULT_OK) {
            String name = data.getStringExtra(DEVICE_INTERVAL_NAME);
            mDeviceIntervalBtn.setText(name);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(PreviewActivity.LOAD_VALUE, mLoadValueEdt.getText().toString());
        intent.putExtra(PreviewActivity.ENV_TEMP, mEnvTempEdt.getText().toString());
        intent.putExtra(PreviewActivity.DEVICE_INTERVAL, mDeviceIntervalBtn.getText().toString());
        setResult(Activity.RESULT_OK, intent);
        finish();
        return;
    }

}
