package com.powergrid.victor.infraredpowergrid.infrared.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flir.flironesdk.Device;
import com.flir.flironesdk.Frame;
import com.flir.flironesdk.FrameProcessor;
import com.flir.flironesdk.RenderedImage;
import com.powergrid.victor.infraredpowergrid.infrared.R;
import com.powergrid.victor.infraredpowergrid.infrared.util.ActivityFullScreenUtil;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 测温页面
 */
public class PreviewActivity extends AppCompatActivity implements Device.Delegate, Device.StreamDelegate, FrameProcessor.Delegate {

    private static final String TAG = "PreviewActivity";
    public static final String LOAD_VALUE = "LOAD_VALUE";
    public static final String ENV_TEMP = "ENV_TEMP";
    public static final String DEVICE_INTERVAL = "DEVICE_INTERVAL";

    private ImageView mPreviewView;
    private LinearLayout mPreviewCalculateView;
    private TextView mPreviewCalculateCenter;
    private TextView mPreviewCalculateHottest;
    private TextView mPreviewLoadValue;
    private TextView mPreviewEnvTemp;
    private TextView mPreviewDeviceInterval;
    private TextView mPreviewTime;
    private ImageView mPreviewCenter;
    private LinearLayout mPreviewConnectToast;
    private LinearLayout mPreviewTuningToast;

    private volatile Device mFlirDevice;
    private Device.TuningState mCurTuningState = Device.TuningState.Unknown;
    private FrameProcessor mFrameProcessor;

    private Bitmap mThermalBitmap;
    private boolean mIsFull;
    private long mFrameCount;

    private ExecutorService mSingleThreadPool = Executors.newFixedThreadPool(5);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        mPreviewView = (ImageView) findViewById(R.id.preview_view);
        mPreviewCalculateView = (LinearLayout) findViewById(R.id.preview_calculate_view);
        mPreviewCalculateCenter = (TextView) findViewById(R.id.preview_calculate_center);
        mPreviewCalculateHottest = (TextView) findViewById(R.id.preview_calculate_hottest);
        mPreviewLoadValue = (TextView) findViewById(R.id.preview_load_value);
        mPreviewEnvTemp = (TextView) findViewById(R.id.preview_env_temp);
        mPreviewDeviceInterval = (TextView) findViewById(R.id.preview_device_interval);
        mPreviewTime = (TextView) findViewById(R.id.preview_time);
        mPreviewCenter = (ImageView) findViewById(R.id.preview_center_icon);
        mPreviewConnectToast = (LinearLayout) findViewById(R.id.preview_connect_toast);
        mPreviewTuningToast = (LinearLayout) findViewById(R.id.preview_tuning_toast);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.i(TAG, "activity start");

        try {
            Device.startDiscovery(this, this);
        } catch (IllegalStateException e) {
            Log.w(TAG, e.toString());

            // it's okay if we've already started discovery
        } catch (SecurityException e) {
            Log.e(TAG, e.toString());

            // On some platforms, we need the user to select the app to give us permisison to the USB device
            Toast.makeText(this, "Please insert FLIR One and select " + getString(R.string.app_name), Toast.LENGTH_LONG).show();

            // There is likely a cleaner way to recover, but for now, exit the activity and wait for user to follow the instructions
            finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.i(TAG, "activity resume");

        if (mFlirDevice != null) {
            mFlirDevice.startFrameStream(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.i(TAG, "activity pause");

        if (mFlirDevice != null) {
            mFlirDevice.stopFrameStream();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();

        Log.i(TAG, "activity stop");

        Device.stopDiscovery();
    }

    public void previewViewClick(View view) {
        if (!mIsFull) {
            mIsFull = true;
            ActivityFullScreenUtil.setFullScreen(this);
        } else {
            mIsFull = false;
            ActivityFullScreenUtil.cancelFullScreen(this);
        }
    }

    // ********** implements Device.Delegate **********
    @Override
    public void onTuningStateChanged(Device.TuningState tuningState) {
        Log.i(TAG, "device tuning");

        mCurTuningState = tuningState;
        if (tuningState == Device.TuningState.InProgress) {
            runOnUiThread(new Thread() {
                @Override
                public void run() {
                    super.run();

                    mPreviewView.setColorFilter(Color.DKGRAY, PorterDuff.Mode.DARKEN);
                    mPreviewTuningToast.setVisibility(View.VISIBLE);
                }
            });
        } else {
            runOnUiThread(new Thread() {
                @Override
                public void run() {
                    super.run();

                    mPreviewView.clearColorFilter();
                    mPreviewTuningToast.setVisibility(View.GONE);
                }
            });
        }

    }

    @Override
    public void onAutomaticTuningChanged(boolean b) {

    }

    @Override
    public void onDeviceConnected(Device device) {
        Log.i(TAG, "device connect");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mPreviewConnectToast.setVisibility(View.GONE);
                mPreviewCenter.setVisibility(View.VISIBLE);
                mPreviewCalculateView.setVisibility(View.VISIBLE);
            }
        });

        mFlirDevice = device;
        mFrameCount = 0;
        mFlirDevice.startFrameStream(this);
        if (mFrameProcessor == null) {
            mFrameProcessor = new FrameProcessor(this, this,
                    EnumSet.of(RenderedImage.ImageType.BlendedMSXRGBA8888Image, RenderedImage.ImageType.ThermalRadiometricKelvinImage));
        }
    }

    @Override
    public void onDeviceDisconnected(Device device) {
        Log.i(TAG, "device disconnect");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mPreviewConnectToast != null) {
                    mPreviewConnectToast.setVisibility(View.VISIBLE);
                }

                if (mPreviewCenter != null) {
                    mPreviewCenter.setVisibility(View.GONE);
                }

                if (mPreviewCalculateView != null) {
                    mPreviewCalculateView.setVisibility(View.GONE);
                }

                if (mPreviewView != null) {
                    mPreviewView.setImageBitmap(Bitmap.createBitmap(1, 1, Bitmap.Config.ALPHA_8));
                    mPreviewView.clearColorFilter();
                }
            }
        });

        mFlirDevice = null;
        mFrameCount = 0;
    }
    // ********** implements Device.Delegate **********

    // ********** implements Device.StreamDelegate **********
    @Override
    public void onFrameReceived(Frame frame) {
//        Log.i(TAG, "frame receive");

        if (mCurTuningState != Device.TuningState.InProgress) {
            mFrameProcessor.processFrame(frame);
        } else {
            mFrameCount = 0;
        }
    }
    // ********** implements Device.StreamDelegate **********


    // ********** implements FrameProcessor.Delegate **********
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    @Override
    public void onFrameProcessed(RenderedImage renderedImage) {
//        Log.i(TAG, "frame process");

        // TODO: 2017/7/16 性能待优化
        if (renderedImage.imageType() == RenderedImage.ImageType.ThermalRadiometricKelvinImage) {
            long start = System.nanoTime();
            mFrameCount++;

            final int[] thermalPixels = renderedImage.thermalPixelValues();
            final int width = renderedImage.width();
            final int height = renderedImage.height();

//            Log.i(TAG, "width = " + width + ", height = " + height);

            if (mFrameCount % 5 == 0) {
                // 起线程池计算
                mSingleThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        double averageTemp = 0;
                        final NumberFormat numberFormat = NumberFormat.getInstance();
                        numberFormat.setMaximumFractionDigits(2);
                        numberFormat.setMinimumFractionDigits(2);
                        // 计算中心点温度
                        int centerPixelIndex = width * (height / 2) + (width / 2);
                        int[] centerPixelIndexes = new int[]
                                {
                                        centerPixelIndex, centerPixelIndex - 1, centerPixelIndex + 1,
                                        centerPixelIndex - width,
                                        centerPixelIndex - width - 1,
                                        centerPixelIndex - width + 1,
                                        centerPixelIndex + width,
                                        centerPixelIndex + width - 1,
                                        centerPixelIndex + width + 1
                                };

                        for (int i = 0; i < centerPixelIndexes.length; i++) {
                            int pixelValue = (thermalPixels[centerPixelIndexes[i]]);
                            averageTemp += (((double) pixelValue) - averageTemp) / ((double) i + 1);
                        }
                        final double averageC = (averageTemp / 100) - 273.15;
                        final String str = String.format("中心点温度：%sºC", numberFormat.format(averageC));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                final String str = String.format("中心点温度：%sºC", numberFormat.format(averageC));
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mPreviewCalculateCenter.setText(str);
                                    }
                                });
                            }
                        });

                        // 计算最热点温度
                        // 1. 先做1:4采样，降到60*80，原尺寸为240*320
                        int width2 = width / 4;
                        int height2 = height / 4;
                        int[] thermalPixels2 = new int[width2 * height2];
                        int index = 0;
                        for (int i = 0; i < height; i += 4) {
                            for (int j = 0; j < width; j += 4) {
                                thermalPixels2[index] = thermalPixels[i * width + j];
                                index++;
                            }
                        }
                        // 2. 3*4的像素点，扫描计算最热温度点
                        double maxTemp = 0;
                        for (int i = 0; i < width2; i += 3) {
                            averageTemp = 0;
                            for (int j = 0; j < height2; j += 4) {
                                int[] pixelValues = new int[]
                                        {
                                                thermalPixels2[i * width2 + j],
                                                thermalPixels2[i * width2 + j + 1],
                                                thermalPixels2[i * width2 + j + 2],
                                                thermalPixels2[i * width2 + j + 3],
                                                thermalPixels2[(i + 1) * width2 + j],
                                                thermalPixels2[(i + 1) * width2 + j + 1],
                                                thermalPixels2[(i + 1) * width2 + j + 2],
                                                thermalPixels2[(i + 1) * width2 + j + 3],
                                                thermalPixels2[(i + 2) * width2 + j],
                                                thermalPixels2[(i + 2) * width2 + j + 1],
                                                thermalPixels2[(i + 2) * width2 + j + 2],
                                                thermalPixels2[(i + 2) * width2 + j + 3],
                                        };
                                for (int k = 0; k < pixelValues.length; ++k) {
                                    averageTemp += (((double) pixelValues[k]) - averageTemp) / ((double) k + 1);
                                }
                                if (maxTemp < averageTemp) {
                                    maxTemp = averageTemp;
                                }
                            }
                        }
                        //  3. 更新UI
                        double maxC = (maxTemp / 100) - 273.15;
                        final String str2 = String.format("最热点温度：%sºC", numberFormat.format(maxC));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mPreviewCalculateHottest.setText(str2);
                            }
                        });
                    }
                });
            }
            long end = System.nanoTime();

            // 计算当前时间，格式yyyy-mm-dd-hh-mm
            Date currentTime = new Date();
            String dateString = formatter.format(currentTime);
            final String str2 = String.format("测温时间：%s", dateString);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mPreviewTime.setText(str2);
                }
            });

            Log.i(TAG, "frame process cost = " + (end - start) / 1000);
        } else {
            if (mThermalBitmap == null) {
                mThermalBitmap = renderedImage.getBitmap();
            } else {
                try {
                    renderedImage.copyToBitmap(mThermalBitmap);
                } catch (IllegalArgumentException e) {
                    mThermalBitmap = renderedImage.getBitmap();
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mPreviewView.setImageBitmap(mThermalBitmap);
                }
            });
        }
    }
    // ********** implements FrameProcessor.Delegate **********

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.preview_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
            case R.id.action_setting:
                Intent intent = new Intent(this, PreviewSettingActivity.class);
                String str = mPreviewLoadValue.getText().toString();
                intent.putExtra(LOAD_VALUE, str.substring("负荷值：".length()));
                str = mPreviewEnvTemp.getText().toString();
                intent.putExtra(ENV_TEMP, str.substring("环境温度：".length()));
                str = mPreviewDeviceInterval.getText().toString();
                intent.putExtra(DEVICE_INTERVAL, str.substring("设备间隔：".length()));
                startActivityForResult(intent, 123);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == Activity.RESULT_OK) {
            String loadValue = data.getStringExtra(LOAD_VALUE);
            String envTemp = data.getStringExtra(ENV_TEMP);
            String deviceInterval = data.getStringExtra(DEVICE_INTERVAL);

            mPreviewLoadValue.setText("负荷值：" + loadValue);
            mPreviewEnvTemp.setText("环境温度：" + envTemp);
            mPreviewDeviceInterval.setText("设备间隔：" + deviceInterval);
        }
    }
}
