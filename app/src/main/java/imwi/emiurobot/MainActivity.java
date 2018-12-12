package imwi.emiurobot;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class MainActivity extends AppCompatActivity {

    BluetoothSPP bt;
    TextView tvConnection;
    SeekBar sbLeftMotorSpeed;
    SeekBar sbHandAngle;
    SeekBar sbRightMotorSpeed;
    SeekBar sbSensorAngle;
    Button btShoot;
    TextView tvSensorAngle;
    TextView tvRightSpeed;
    TextView tvLeftSpeed;
    SeekBar sbDirection;
    SeekBar sbTwoMotorSpeed;
    TextView tvTwoMotorSpeed;
    TextView tvHandAngle;
    TextView tvYawValue;
    TextView tvPitchValue;
    TextView tvRollValue;
    TextView tvDistanceValue;
    TextView tvSteeringAngle;

    int LeftDirectionValue = 0;
    int RightDirectionValue = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        InitWidget();
        InitEvent();
    }

    void InitWidget()
    {
        tvConnection = (TextView) findViewById(R.id.tvConnection);
        sbLeftMotorSpeed = (SeekBar) findViewById(R.id.sbLeftMotorSpeed);
        sbRightMotorSpeed = (SeekBar) findViewById(R.id.sbRightMotorSpeed);
        sbSensorAngle = (SeekBar) findViewById(R.id.sbSensorAngle);
        sbHandAngle = (SeekBar) findViewById(R.id.sbHandAngle);
        btShoot = (Button) findViewById(R.id.btShoot);
        tvSensorAngle = (TextView) findViewById(R.id.tvSensorAngle);
        tvHandAngle = (TextView) findViewById(R.id.tvHandAngle);
        tvRightSpeed = (TextView) findViewById(R.id.tvRightSpeed);
        tvLeftSpeed = (TextView) findViewById(R.id.tvLeftSpeed);
        sbDirection = (SeekBar) findViewById(R.id.sbDirection);
        sbTwoMotorSpeed = (SeekBar) findViewById(R.id.sbTwoMotorSpeed);
        tvTwoMotorSpeed = (TextView) findViewById(R.id.tv2MotorSpeed);
        tvYawValue = (TextView) findViewById(R.id.tvYawValue);
        tvPitchValue = (TextView) findViewById(R.id.tvPitchValue);
        tvRollValue = (TextView) findViewById(R.id.tvRollValue);
        tvDistanceValue = (TextView) findViewById(R.id.tvDistanceValue);
        tvSteeringAngle = (TextView) findViewById(R.id.tvSteeringAngle);
        bt = new BluetoothSPP(this);
    }

    void InitEvent()
    {
        tvConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                bt.setDeviceTarget(BluetoothState.DEVICE_OTHER);
                Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
            }
        });

        if(!bt.isBluetoothAvailable()) {
            tvConnection.setText("Bluetooth is not available");
            finish();
        }

        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            public void onDataReceived(byte[] data, String message)
            {
                if (message.charAt(0) == 'Y')
                {
                    tvYawValue.setText(message.substring(1));
                }

                if (message.charAt(0) == 'P')
                {
                    tvPitchValue.setText(message.substring(1));
                }

                if (message.charAt(0) == 'R')
                {
                    tvRollValue.setText(message.substring(1));
                }

                if (message.charAt(0) == 'D')
                {
                    tvDistanceValue.setText(message.substring(1));
                }
            }
        });

        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            public void onDeviceDisconnected()
            {
                tvConnection.setText("Status : Not connect");
            }

            public void onDeviceConnectionFailed() {
                tvConnection.setText("Status : Connection failed");
            }

            public void onDeviceConnected(String name, String address)
            {
                tvConnection.setText("Status : Connected to " + name);
            }
        });

        btShoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (btShoot.getText() == "SHOOT") {
                    sbHandAngle.setProgress(170 / 5);
                    btShoot.setText("DOWN");
                }
                else {
                    sbHandAngle.setProgress(120 / 5);
                    btShoot.setText("SHOOT");
                }
            }
        });

        sbSensorAngle.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser)
            {
                int value = progressValue * 5;
                tvSensorAngle.setText("Sensor Angle: " + Integer.toString(value));
                bt.send(new String("sa-") + Integer.toString(value), true);
                bt.send("seta", true);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {


            }
        });

        sbHandAngle.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser)
            {
                int value = progressValue * 5;
                tvHandAngle.setText("Hand Angle: " + Integer.toString(value));
                bt.send(new String("ha-") + Integer.toString(value), true);
                bt.send("seta", true);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {


            }
        });

        sbLeftMotorSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser)
            {
                int value = (progressValue - 51) * 5 + LeftDirectionValue;
                tvLeftSpeed.setText("Left Speed: " + Integer.toString(value));
                bt.send(new String("ls-") + Integer.toString(value), true);
                bt.send("ss", true);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBar.setProgress(51);
                tvLeftSpeed.setText(Integer.toString(0));
                bt.send(new String("ls-") + Integer.toString(0), true);
                bt.send("ss", true);

            }
        });

        sbRightMotorSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser)
            {
                int value = (progressValue - 51) * 5 + RightDirectionValue;
                tvRightSpeed.setText("Right Speed: " + Integer.toString(value));
                bt.send(new String("rs-") + Integer.toString(value), true);
                bt.send("ss", true);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBar.setProgress(51);
                tvRightSpeed.setText(Integer.toString(0));
                bt.send(new String("rs-") + Integer.toString(0), true);
                bt.send("ss", true);

            }
        });

        sbTwoMotorSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser)
            {
                sbLeftMotorSpeed.setProgress(progressValue);
                sbRightMotorSpeed.setProgress(progressValue);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBar.setProgress(51);

            }
        });

        sbDirection.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser)
            {
                int value = (progressValue - 20) * 15;
                if (value > 0)
                {
                    LeftDirectionValue = 0;
                    RightDirectionValue = 0 - value;
                }
                else
                {
                    LeftDirectionValue = 0 + value;
                    RightDirectionValue = 0;
                }

                tvSteeringAngle.setText("Stearing Angle: " + Integer.toString(value));

                int output = (sbLeftMotorSpeed.getProgress() - 51) * 5 + LeftDirectionValue;
                tvLeftSpeed.setText("Left Speed: " + Integer.toString(output));
                bt.send(new String("ls-") + Integer.toString(output), true);
                bt.send("ss", true);

                int output2 = (sbRightMotorSpeed.getProgress() - 51) * 5 + RightDirectionValue;
                tvRightSpeed.setText("Right Speed: " + Integer.toString(output2));
                bt.send(new String("rs-") + Integer.toString(output2), true);
                bt.send("ss", true);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBar.setProgress(20);
                LeftDirectionValue = 0;
                RightDirectionValue = 0;

                int output = (sbLeftMotorSpeed.getProgress() - 51) * 5 + LeftDirectionValue;
                tvLeftSpeed.setText("Left Speed: " + Integer.toString(output));
                bt.send(new String("ls-") + Integer.toString(output), true);
                bt.send("ss", true);

                int output2 = (sbRightMotorSpeed.getProgress() - 51) * 5 + RightDirectionValue;
                tvRightSpeed.setText("Right Speed: " + Integer.toString(output2));
                bt.send(new String("rs-") + Integer.toString(output2), true);
                bt.send("ss", true);
            }
        });

    }

    public void onDestroy() {
        super.onDestroy();
        bt.stopService();
    }

    public void onStart() {
        super.onStart();
        if (!bt.isBluetoothEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            if(!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_ANDROID);
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if(resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if(requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if(resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_ANDROID);
            } else {
                tvConnection.setText("Bluetooth was not enabled.");
                finish();
            }
        }
    }
}