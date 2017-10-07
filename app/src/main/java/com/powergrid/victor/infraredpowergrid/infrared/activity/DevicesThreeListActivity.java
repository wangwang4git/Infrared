package com.powergrid.victor.infraredpowergrid.infrared.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.powergrid.victor.infraredpowergrid.infrared.R;
import com.powergrid.victor.infraredpowergrid.infrared.json.DeviceInterval;

import java.util.Arrays;
import java.util.List;

public class DevicesThreeListActivity extends AppCompatActivity {

    private static final String TAG = "DevicesThreeListActivit";

    public static final String DATA_SOURCE = "DATA_SOURCE";
    public static final String DATA_SOURCE_ONE_NAME = "DATA_SOURCE_ONE_NAME";
    public static final String DATA_SOURCE_TWO_NAME = "DATA_SOURCE_TWO_NAME";

    private ListView mThreeListView;
    private DeviceInterval.Interval mInterval;
    private String mOneName;
    private String mTwoName;
    private List<String> mDataSets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices_three_list);

        mInterval = (DeviceInterval.Interval) getIntent().getExtras().getSerializable(DATA_SOURCE);
        mOneName = getIntent().getExtras().getString(DATA_SOURCE_ONE_NAME);
        mTwoName = getIntent().getExtras().getString(DATA_SOURCE_TWO_NAME);
        mDataSets = getData();

        mThreeListView = (ListView) findViewById(R.id.three_list);
        mThreeListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, mDataSets));
        mThreeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String clickStr = mDataSets.get(position);
                Log.i(TAG, "clickStr = " + clickStr);

                String allDeviceName = mOneName + "-" + mTwoName + "-" + clickStr;
                Toast.makeText(DevicesThreeListActivity.this, "选择的设备间隔：" + allDeviceName, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent();
                intent.putExtra(PreviewSettingActivity.DEVICE_INTERVAL_NAME, allDeviceName);
                setResult(Activity.RESULT_OK, intent);
                DevicesThreeListActivity.this.finish();
            }
        });
    }

    private List<String> getData() {
        List<String> names = Arrays.asList(mInterval.getDescs());
        Log.i(TAG, "name = " + names);
        return names;
    }

}
