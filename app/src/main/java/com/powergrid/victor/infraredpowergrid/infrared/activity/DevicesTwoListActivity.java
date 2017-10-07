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

public class DevicesTwoListActivity extends AppCompatActivity {

    private static final String TAG = "DevicesTwoListActivity";

    public static final String DATA_SOURCE = "DATA_SOURCE";
    public static final String DATA_SOURCE_ONE_NAME = "DATA_SOURCE_ONE_NAME";

    private ListView mTwoListView;
    private DeviceInterval mDeviceInterval;
    private String mOneName;
    private List<String> mDataSets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices_two_list);

        mDeviceInterval = (DeviceInterval) getIntent().getExtras().getSerializable(DATA_SOURCE);
        mOneName = getIntent().getExtras().getString(DATA_SOURCE_ONE_NAME);
        mDataSets = getData();

        mTwoListView = (ListView) findViewById(R.id.two_list);
        mTwoListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, mDataSets));
        mTwoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String clickStr = mDataSets.get(position);
                Log.i(TAG, "clickStr = " + clickStr);

                Intent intent = new Intent(DevicesTwoListActivity.this, DevicesThreeListActivity.class);
                Bundle bundle = new Bundle();
                DeviceInterval.Interval clickInterval = null;
                for (DeviceInterval.Interval interval : mDeviceInterval.getIntervals()) {
                    if (TextUtils.equals(interval.getName(), clickStr)) {
                        clickInterval = interval;
                        break;
                    }
                }
                bundle.putSerializable(DevicesThreeListActivity.DATA_SOURCE, clickInterval);
                bundle.putString(DevicesThreeListActivity.DATA_SOURCE_ONE_NAME, mOneName);
                bundle.putString(DevicesThreeListActivity.DATA_SOURCE_TWO_NAME, clickStr);
                intent.putExtras(bundle);
                startActivityForResult(intent, 123);
            }
        });
    }

    private List<String> getData() {
        List<String> names = new ArrayList<String>();
        for (DeviceInterval.Interval interval : mDeviceInterval.getIntervals()) {
            names.add(interval.getName());
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
