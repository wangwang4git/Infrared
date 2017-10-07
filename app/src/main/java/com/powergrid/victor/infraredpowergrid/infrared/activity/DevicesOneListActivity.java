package com.powergrid.victor.infraredpowergrid.infrared.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.powergrid.victor.infraredpowergrid.infrared.R;
import com.powergrid.victor.infraredpowergrid.infrared.json.DeviceInterval;

import java.util.ArrayList;
import java.util.List;

public class DevicesOneListActivity extends AppCompatActivity {

    private static final String TAG = "DevicesOneListActivity";

    public static final String DATA_SOURCE = "DATA_SOURCE";

    private ListView mOneListView;
    private List<DeviceInterval> mDeviceIntervals;
    private List<String> mDataSets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices_one_list);

        mDeviceIntervals = (List<DeviceInterval>) getIntent().getExtras().getSerializable(DATA_SOURCE);
        mDataSets = getData();

        mOneListView = (ListView) findViewById(R.id.one_list);
        mOneListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, mDataSets));
        mOneListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String clickStr = mDataSets.get(position);
                Log.i(TAG, "clickStr = " + clickStr);

                Intent intent = new Intent(DevicesOneListActivity.this, DevicesTwoListActivity.class);
                Bundle bundle = new Bundle();
                DeviceInterval clickDeviceInterval = null;
                for (DeviceInterval deviceInterval : mDeviceIntervals) {
                    if (TextUtils.equals(deviceInterval.getName(), clickStr)) {
                        clickDeviceInterval = deviceInterval;
                        break;
                    }
                }
                bundle.putSerializable(DevicesTwoListActivity.DATA_SOURCE, clickDeviceInterval);
                bundle.putString(DevicesTwoListActivity.DATA_SOURCE_ONE_NAME, clickStr);
                intent.putExtras(bundle);
                startActivityForResult(intent, 123);
            }
        });
    }

    private List<String> getData() {
        List<String> names = new ArrayList<String>();
        for (DeviceInterval deviceInterval : mDeviceIntervals) {
            names.add(deviceInterval.getName());
        }
        Log.i(TAG, "name = " + names);
        return names;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == Activity.RESULT_OK) {
            Intent intent = new Intent();
            intent.putExtra(PreviewSettingActivity.DEVICE_INTERVAL_NAME, data.getStringExtra(PreviewSettingActivity.DEVICE_INTERVAL_NAME));
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }
}
