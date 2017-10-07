package com.powergrid.victor.infraredpowergrid.infrared.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.powergrid.victor.infraredpowergrid.infrared.R;
import com.powergrid.victor.infraredpowergrid.infrared.util.ObjectSaveUtil;

/**
 * 主页面
 */
public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEdtHomeUser1;
    private TextView mTxtHomeUser1Add;
    private EditText mEdtHomeUser2;
    private TextView mTxtHomeUser2Add;
    private Button mBtnClear;
    private Button mBtnModify;
    private Button mBtnIntoMeasure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initViews();
    }

    private void initViews() {
        mEdtHomeUser1 = (EditText) findViewById(R.id.home_user1);
        mEdtHomeUser1.setText(ObjectSaveUtil.getLastMeasureUser(this));
        mTxtHomeUser1Add = (TextView) findViewById(R.id.home_user1_add);
        mTxtHomeUser1Add.setVisibility(View.GONE);
        mTxtHomeUser1Add.setOnClickListener(this);

        mEdtHomeUser2 = (EditText) findViewById(R.id.home_user2);
        mEdtHomeUser2.setText(ObjectSaveUtil.getLastRecordUser(this));
        mTxtHomeUser2Add = (TextView) findViewById(R.id.home_user2_add);
        mTxtHomeUser2Add.setVisibility(View.GONE);
        mTxtHomeUser2Add.setOnClickListener(this);

        mBtnClear = (Button) findViewById(R.id.home_clear);
        mBtnClear.setOnClickListener(this);

        mBtnModify = (Button) findViewById(R.id.home_modify);
        mBtnModify.setOnClickListener(this);

        mBtnIntoMeasure = (Button) findViewById(R.id.home_into_measure);
        mBtnIntoMeasure.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_clear:
                mEdtHomeUser1.setText("");
                mEdtHomeUser2.setText("");
                break;
            case R.id.home_modify:
                mEdtHomeUser1.setText("");
                mEdtHomeUser2.setText("");
                mTxtHomeUser1Add.setVisibility(View.VISIBLE);
                mTxtHomeUser2Add.setVisibility(View.VISIBLE);
                break;
            case R.id.home_into_measure:
                String user1 = mEdtHomeUser1.getText().toString();
                String user2 = mEdtHomeUser2.getText().toString();
                if (TextUtils.isEmpty(user1) || TextUtils.isEmpty(user2)) {
                    Toast.makeText(this, "请输入测温人、记录人信息~", Toast.LENGTH_SHORT).show();
                } else {
                    ObjectSaveUtil.setLastMeasureUser(this, user1);
                    ObjectSaveUtil.setLastRecordUser(this, user2);
                    Intent intent = new Intent(this, PreviewActivity.class);
                    startActivity(intent);
                }
                break;
            default:
                break;
        }
    }
}
