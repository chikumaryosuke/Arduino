package com.example.chikumaryousuke.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.database.DefaultDatabaseErrorHandler;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.v4.app.ActivityCompat;


import android.content.Intent;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;
import android.Manifest;


public class MainActivity extends ActionBarActivity implements LocationListener, Runnable, View.OnClickListener {

    private LocationManager locationManager;

    /* tag */
    private static final String TAG = "BluetoothSample";

    /* Bluetooth Adapter */
    private BluetoothAdapter mAdapter;

    /* Bluetoothデバイス */
    private BluetoothDevice mDevice;

    /* Bluetooth UUID */
    private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    /* Soket */
    private BluetoothSocket mSocket;

    /* Thread */
    private Thread mThread;

    /* Threadの状態を表す */
    private boolean isRunning;

    /**
     * 接続ボタン.
     */
    private Button connectButton;

    /**
     * LED ONボタン.
     */
    private Button ledOnButton;
    private Button lButton;
    /**
     * LED OFFボタン.
     */
    private Button ledOffButton;

    /**
     * ステータス.
     */
    private TextView mStatusTextView;

    /**
     * Bluetoothから受信した値.
     */
    private TextView mInputTextView;
    private TextView mInputTextView2;
    private TextView mInputTextView3;
    private TextView mInputTextView4;
    private TextView mInputTextView5;
    private TextView mInputTextView6;
    private TextView mInputTextView7;
    private TextView mInputTextView8;
    private TextView mInputTextView9;
    private TextView mInputTextView10;
    private TextView mInputTextView11;
    private TextView mInputTextView12;

    /**
     * Action(ステータス表示).
     */
    private static final int VIEW_STATUS = 0;

    /**
     * Action(取得文字列).
     */
    private static final int VIEW_INPUT = 1;

    /**
     * BluetoothのOutputStream.
     */
    OutputStream mmOutputStream = null;

    /**
     * Connect状態確認用フラグ.
     */
    private boolean connectFlg = false;

    private String out_Text = "";
    /**
     * 取得データの終了文字以降(2文字目以降)を格納(文字列)
     */
    private String out_Text_bk = "";

    /**
     * defaultはArduino用 (false)
     * cubicのときにTrueにする
     * @param savedInstanceState
     */
    private boolean forCubic = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mInputTextView = (TextView) findViewById(R.id.inputValue);
        mInputTextView2 = (TextView) findViewById(R.id.inputValue2);
        mInputTextView3 = (TextView) findViewById(R.id.inputValue3);
        mInputTextView4 = (TextView) findViewById(R.id.inputValue4);
        mInputTextView5 = (TextView) findViewById(R.id.inputValue5);
        mInputTextView6 = (TextView) findViewById(R.id.inputValue6);
        mInputTextView7 = (TextView) findViewById(R.id.inputValue7);
        mInputTextView8 = (TextView) findViewById(R.id.inputValue8);
        mInputTextView9 = (TextView) findViewById(R.id.inputValue9);
        mInputTextView10 = (TextView) findViewById(R.id.inputValue10);

        mStatusTextView = (TextView) findViewById(R.id.statusValue);
        mStatusTextView = (Button) findViewById(R.id.statusValue);

        connectButton = (Button) findViewById(R.id.connectButton);
        ledOnButton = (Button) findViewById(R.id.ledOnButton);
        ledOffButton = (Button) findViewById(R.id.ledOffButton);
        lButton = (Button) findViewById(R.id.lButton);

        connectButton.setOnClickListener(this);

        ledOnButton.setOnClickListener(this);
        ledOffButton.setOnClickListener(this);
        lButton.setOnClickListener(this);

        // Bluetoothのデバイス名を取得
        // デバイス名は、RNBT-XXXXになるため、
        // DEVICE_NAMEでデバイス名を定義
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mStatusTextView.setText("ペアリングしてください");
        Set<BluetoothDevice> devices = mAdapter.getBondedDevices();
        for (BluetoothDevice device : devices) {
            mStatusTextView.setText("find: " + device.getName() + "\n" + device.getAddress());
            mDevice = device;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        } else {
            locationStart();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        isRunning = false;
        try {
            mSocket.close();
        } catch (Exception e) {
        }
    }

    @Override
    public void run() {
        InputStream mmInStream = null;

        Message valueMsg = new Message();
        valueMsg.what = VIEW_STATUS;
        valueMsg.obj = "connecting...";
        mHandler.sendMessage(valueMsg);

        try {
            // 取得したデバイス名を使ってBluetoothでSocket接続
            mSocket = mDevice.createRfcommSocketToServiceRecord(MY_UUID);
            mSocket.connect();
            mmInStream = mSocket.getInputStream();
            mmOutputStream = mSocket.getOutputStream();

            // InputStreamのバッファを格納
            byte[] buffer = new byte[1024];

            // 取得したバッファのサイズを格納
            int bytes;
            valueMsg = new Message();
            valueMsg.what = VIEW_STATUS;
            valueMsg.obj = "connected.";
            mHandler.sendMessage(valueMsg);

            connectFlg = true;

            while (isRunning) {

                if (forCubic == true) {
                    // cubic
                    // InputStreamの読み込み
                    bytes = mmInStream.read(buffer);
                    Log.i(TAG, "bytes=" + bytes);
                    // String型に変換
                    String readMsg = new String(buffer, 0, bytes);
                    String[] rmessage = new String[20];


                    if (bytes > 199) {
                        // separate data from c-cubic
                        for (int i = 0; i < 20; i++) {
                            rmessage[i] = readMsg.substring(i * 10, i * 10 + 9);
                        }

                        // for debug
                        StringBuilder builder = new StringBuilder();
                        for (int i = 0; i < rmessage.length; i++) {
                            builder.append(rmessage[i]);
                            if (i != rmessage.length - 1) {
                                builder.append(".");
                            }
                        }
                        String str = builder.toString();

                        // null以外なら表示
                        if (readMsg.trim() != null && !readMsg.trim().equals("")) {
                            Log.i(TAG, "value=" + readMsg.trim());
                            valueMsg = new Message();
                            valueMsg.what = VIEW_INPUT;
                            valueMsg.obj = rmessage;
                            mHandler.sendMessage(valueMsg);
                        } else {
                            // Log.i(TAG,"value=nodata");
                        }
                    }
                } else {
                    // arduino

                    // InputStreamの読み込み
                    bytes = mmInStream.read(buffer);
                    Log.i(TAG, "bytes=" + bytes);
                    // String型に変換(退避用に文字が存在する場合はそれも含めて設定)
                    String readMsg = out_Text_bk + new String(buffer, 0, bytes);
                    // 退避用変数の初期化
                    out_Text_bk = "";
                    // 出力フラグの初期化
                    Boolean dataEndFlg = false;

                    // 読み込んだ文字列を1文字ずつ取得
                    for (int i = 0; readMsg.length() > i; i++) {
                        char readChar = readMsg.charAt(i);
                        // 文字判定
                        if (readChar == '*') {
                            // 終了文字を確認した時点で出力フラグを立てる
                            dataEndFlg = true;
                            // 終了文字以降
                        } else if (dataEndFlg) {
                            // 文字を退避
                            out_Text_bk = out_Text_bk + readChar;
                            // 終了文字以前
                        } else {
                            // 出力用に設定
                            out_Text = out_Text + readChar;
                        }
                    }

                    // 出力フラグがtrueかつnull(空文字含む)以外なら表示
                    if (dataEndFlg && out_Text.trim() != null && !out_Text.trim().equals("")) {
                        Log.i(TAG, "value=" + out_Text.trim());

                        valueMsg = new Message();
                        valueMsg.what = VIEW_INPUT;
                        valueMsg.obj = out_Text.trim();
                        mHandler.sendMessage(valueMsg);
                        // 出力用変数の初期化
                        out_Text = "";
                    }

                }
            }

        } catch (Exception e) {

            valueMsg = new Message();
            valueMsg.what = VIEW_STATUS;
            valueMsg.obj = "Error1:" + e;
            mHandler.sendMessage(valueMsg);

            try {
                mSocket.close();
            } catch (Exception ee) {
            }
            isRunning = false;
        }
    }


    @Override
    public void onClick(View v) {
        if (v.equals(connectButton)) {
            if (!connectFlg) {
                mStatusTextView.setText("Try connect");

                mThread = new Thread(this);
                // Threadを起動し、Bluetooth接続
                isRunning = true;
                mThread.start();
            }
        } else if (v.equals(ledOnButton)) {
            if (connectFlg) {
                try {
                    mmOutputStream.write("1".getBytes());
                    mStatusTextView.setText("forward:");
                } catch (IOException e) {
                    Message valueMsg = new Message();
                    valueMsg.what = VIEW_STATUS;
                    valueMsg.obj = "Error3:" + e;
                    mHandler.sendMessage(valueMsg);
                }
            } else {
                mStatusTextView.setText("Please push the connect button");
            }
        } else if (v.equals(ledOffButton)) {
            if (connectFlg) {
                try {
                    mmOutputStream.write("0".getBytes());
                    mStatusTextView.setText("back:");
                } catch (IOException e) {
                    Message valueMsg = new Message();
                    valueMsg.what = VIEW_STATUS;
                    valueMsg.obj = "Error4:" + e;
                    mHandler.sendMessage(valueMsg);
                }
            } else {
                mStatusTextView.setText("Please push the connect button");
            }
        } else if (v.equals(lButton)) {
            if (connectFlg) {
                try {
                    mmOutputStream.write("2".getBytes());
                    mStatusTextView.setText("stop:");
                } catch (IOException e) {
                    Message valueMsg = new Message();
                    valueMsg.what = VIEW_STATUS;
                    valueMsg.obj = "Error5:" + e;
                    mHandler.sendMessage(valueMsg);
                }
            }
        }
    }

    /**
     * 描画処理はHandlerでおこなう
     */
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int action = msg.what;
            if (forCubic == true) {
                if (action == VIEW_INPUT) {
                    String[] messages = (String[]) msg.obj;
                    mInputTextView.setText(messages[0]);
                    mInputTextView2.setText(messages[1]);
                    mInputTextView3.setText(messages[2]);
                    mInputTextView4.setText(messages[3]);
                    mInputTextView5.setText(messages[4]);
                    mInputTextView6.setText(messages[5]);
                    mInputTextView7.setText(messages[6]);
                    mInputTextView8.setText(messages[7]);
                    mInputTextView9.setText(messages[8]);
                    mInputTextView10.setText(messages[9]);
                } else if (action == VIEW_STATUS) {
                    String messages = (String) msg.obj;
                    mStatusTextView.setText(messages);
                }
            } else {
                String messages = (String) msg.obj;
                if (action == VIEW_INPUT) {
                    mInputTextView.setText(messages);
                } else if (action == VIEW_STATUS) {
                    mStatusTextView.setText(messages);
                }
            }

        }
    };

    private void locationStart() {
        Log.d("debug", "locationStart()");

        // LocationManager インスタンス生成
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            // GPSを設定するように促す
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
            Log.d("debug", "gpsEnable, startActivity");
        } else {
            Log.d("debug", "gpsEnabled");
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);

            Log.d("debug", "checkSelfPermission false");
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 50, this);
    }

    // 結果の受け取り
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1000) {
            // 使用が許可された
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("debug", "checkSelfPermission true");

                locationStart();
                return;

            } else {
                // それでも拒否された時の対応
                Toast toast = Toast.makeText(this, "これ以上なにもできません", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        switch (status) {
            case LocationProvider.AVAILABLE:
                Log.d("debug", "LocationProvider.AVAILABLE");
                break;
            case LocationProvider.OUT_OF_SERVICE:
                Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                break;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
//        // 緯度の表示
//        TextView textView1 = (TextView) findViewById(R.id.text_View1);
//        textView1.setText("Latitude:" + location.getLatitude());
//
//        // 経度の表示
//        TextView textView2 = (TextView) findViewById(R.id.text_View2);
//        textView2.setText("Longitude:" + location.getLongitude());
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}