package com.bbpos.bbdevice.example;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bbpos.bbdevice.BBDeviceController;

import java.util.ArrayList;
import java.util.Set;

import static com.bbpos.bbdevice.example.BaseActivity.bbDeviceController;


/**
 * Created by usuario on 12/05/17.
 */
public class SplashScreenActivity extends Activity {
    BluetoothAdapter bluetoothAdapter = null;
    ArrayList<String> arrayListpaired;
    ArrayList<BluetoothDevice> arrayListPairedBluetoothDevices;
    ArrayAdapter<String> adapter, detectedAdapter;
    boolean connected = false;
    boolean bluetooth = false;
    private final static int REQUEST_ENABLE_BT = 1;

    Boolean pairedBt = false;
    protected static Dialog dialog;


    // Set the duration of the splash screen
    private static final long SPLASH_SCREEN_DELAY = 3000;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.splash_activity);
        arrayListpaired = new ArrayList<String>();


        arrayListPairedBluetoothDevices = new ArrayList<BluetoothDevice>();
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        } else
            connected = false;

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            bluetooth = false;
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
            else
                checkInternet();
        }


        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevice = bluetoothAdapter.getBondedDevices();
        if (pairedDevice.size() > 0) {
            for (BluetoothDevice device : pairedDevice) {

                if (device.getAddress().toString().trim().equals("2F:11:2F:CF:0F:DC")) {
                    pairedBt = true;
                    break;
                }

            }
            if (!pairedBt) {
                Toast.makeText(getApplicationContext(), getString(R.string.BluetoothNC), Toast.LENGTH_SHORT).show();
            }
        }

    }

    protected void checkInternet(){
        if (!connected) {
            dialog = new Dialog(this);
            dialog.setContentView(R.layout.internet_dialog);
            dialog.setTitle(getString(R.string.connection));
            ((TextView) (dialog.findViewById(R.id.messageTextView))).setText(getString(R.string.No_internet));
            dialog.findViewById(R.id.confirmButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pid = android.os.Process.myPid();
                    android.os.Process.killProcess(pid);
                }
            });
            dialog.setCancelable(false);
            dialog.show();

        }
        else {
            delay();
        }

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                checkInternet();
            }
            else{
                int pid = android.os.Process.myPid();
                android.os.Process.killProcess(pid);
            }

        }
    }

    public void delay() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                Intent newForms = new Intent(SplashScreenActivity.this, LoginActivity.class);
                // Close the activity so the user won't able to go back this
                // activity pressing Back button
                startActivity(newForms);
                finish();
            }
        }, SPLASH_SCREEN_DELAY);
        // Simulate a long loading process on application startup.
    }

}