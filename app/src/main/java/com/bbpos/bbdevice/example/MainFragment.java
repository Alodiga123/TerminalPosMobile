package com.bbpos.bbdevice.example;

/**
 * Created by usuario on 13/06/17.
 */

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import java.util.Set;

import static com.bbpos.bbdevice.example.BaseActivity.amountEditText;
import static com.bbpos.bbdevice.example.BaseActivity.bbDeviceController;

import static com.bbpos.bbdevice.example.BaseActivity.statusEditText;

public class MainFragment extends Fragment{
    View rootView =null;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = inflater.inflate(R.layout.main_fragment,container,false);
        final Button startButton = (Button) rootView.findViewById(R.id.startButton);
        final BaseActivity activity = (BaseActivity) getActivity();

        startButton.setOnClickListener(new View.OnClickListener() {





            @Override
            public void onClick(View v) {
                statusEditText.setText("");

//                if (v == startButton) {
                  //  isPinCanceled = false;
/*                    amountEditText.setText("");

                    statusEditText.setText(R.string.starting);*/
                    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    Set<BluetoothDevice> pairedDevice = bluetoothAdapter.getBondedDevices();
                    if(pairedDevice.size()>0) {
                        for (BluetoothDevice device : pairedDevice) {

                            if (device.getAddress().toString().trim().equals("2F:11:2F:CF:0F:DC")) {

                                bbDeviceController.connectBT(device);
                            }

                            try {
                               activity.promptForEmail();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }
                    }
                }
            //}
        } );

    return rootView;}


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
