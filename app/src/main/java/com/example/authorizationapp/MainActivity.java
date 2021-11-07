package com.example.authorizationapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText inputTextBox;
    private Button loginButton;
    private TextView statusView;

    SensorManager sensorManager;
    Sensor accelerometerSensor;
    boolean accelerometerPresent;

    private boolean phoneFacingDown = false;


    /*PARAMETERS REQUIRED :
    -Password matching battery precentage
    -Phone must be facing down (fliped)
    -Phone must be connected to wifi
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputTextBox = findViewById(R.id.main_TXTF_pass);
        statusView = findViewById(R.id.main_LBL_status);

        initButtonListeners();


        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if(sensorList.size() > 0){
            accelerometerPresent = true;
            accelerometerSensor = sensorList.get(0);
        }
        else{
            accelerometerPresent = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(accelerometerPresent){
            sensorManager.registerListener(accelerometerListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(accelerometerPresent){
            sensorManager.unregisterListener(accelerometerListener);
        }
    }

    private void initButtonListeners(){
        loginButton = findViewById(R.id.main_BTN_enter);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
               Enter(inputTextBox.getEditableText().toString());
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void Enter(String enteredPass){
        if (checkBatteryLevel(enteredPass) && checkIfConnectedToWifi() && phoneFacingDown) {
            statusView.setText("Approved");
        }else{
            statusView.setText("Denied");
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean checkIfConnectedToWifi(){
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        return mWifi.isConnected();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public boolean checkBatteryLevel(String enteredPass){
        BatteryManager bm = (BatteryManager) this.getSystemService(BATTERY_SERVICE);
        int batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

        if (enteredPass.equals(String.valueOf(batLevel)))
            return true;
        else
            return false;
    }

    private SensorEventListener accelerometerListener = new SensorEventListener(){

        @Override
        public void onAccuracyChanged(Sensor arg0, int arg1) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onSensorChanged(SensorEvent arg0) {
            // TODO Auto-generated method stub
            float z_value = arg0.values[2];
            if (z_value >= 0){
                phoneFacingDown = false;
            } else{
                phoneFacingDown = true;
            }
        }};

}


