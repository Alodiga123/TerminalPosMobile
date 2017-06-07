package com.bbpos.bbdevice.example;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.bluetooth.BluetoothAdapter;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.*;
import org.ksoap2.transport.HttpTransportSE;


import com.alodiga.security.encryption.Encrypt;
import com.alodiga.security.encryption.S3cur1ty3Cryt3r;



import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;

import static com.bbpos.bbdevice.example.BaseActivity.dialog;


public class LoginActivity extends Activity {
    private ProgressBar spinner;

    String password="";
    String usuario="";
    int tries = 0;
    int limit = 3;






    protected static Dialog dialog;
    protected static LoginActivity currentActivity;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.login_activity);
        //final CheckBox loginCheck = (CheckBox) findViewById(R.id.KeepSingIn);
        final EditText txtLogin = (EditText) findViewById(R.id.txtUser);
        final EditText txtPassword = (EditText) findViewById(R.id.txtPassword);
        final Button btnSingIn = (Button) findViewById(R.id.btnSign);
        spinner = (ProgressBar)findViewById(R.id.progressBar);
        spinner.setVisibility(View.INVISIBLE);
        usuario="";
        password="";




        btnSingIn.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        try {

                            password = ((EditText)findViewById(R.id.txtPassword)).getText().toString();
                            usuario = ((EditText)findViewById(R.id.txtUser)).getText().toString();

                            Utils util = new Utils();
                            final String key = util.getkey(getApplicationContext());
                            String deviceId = android.provider.Settings.Secure.getString(getApplicationContext().getContentResolver(),
                                    android.provider.Settings.Secure.ANDROID_ID);

                            final String  deEvice = Utils.sha256(deviceId);
                            final String number1 = S3cur1ty3Cryt3r.aloEncrypter((usuario+","+password),key,null);



                            if (usuario.equals("")) {
                                Toast.makeText( getApplicationContext(), getString(R.string.user_not_empty),Toast.LENGTH_LONG).show();
                                spinner.setVisibility(View.INVISIBLE);
                                ((EditText)findViewById(R.id.txtUser)).setFocusable(true);
                                return;
                            }
                            if (password.equals("")) {
                                Toast.makeText( getApplicationContext(), getString(R.string.pass_not_empty),Toast.LENGTH_LONG).show();
                                spinner.setVisibility(View.INVISIBLE);
                                ((EditText)findViewById(R.id.txtPassword)).setFocusable(true);

                                return;
                            }




                            new Thread(new Runnable() {
                                public void run() {
                                    try {
                                        try {
                                            String value = WebService.invokeGetAutoConfigString(number1,deEvice,"loginTerminal","http://ec2-35-167-158-127.us-west-2.compute.amazonaws.com:8080/AcquiringWSServicesProviderService/AcquiringWSServicesProvider");
                                            String response = S3cur1ty3Cryt3r.aloDesencrypter(value,key,null);
                                            String[] auth = response.split(",");


                                            if(Integer.valueOf(auth[0].toString().trim()).equals(0)){
//                                if(loginCheck.isChecked()){
//
//                                }
                                                Intent newForms = new Intent(LoginActivity.this, IndexActivity.class);
                                                newForms.putExtra("token",auth[2]);
                                                startActivity(newForms);
                                                spinner.setVisibility(View.INVISIBLE);
                                            }else if((Integer.valueOf(auth[0].toString().trim()).equals(1))){
                                                runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        tries++;
                                                        limit = 3-tries;
                                                        if(limit ==0){
                                                            dialog = new Dialog(LoginActivity.this);
                                                            dialog.setContentView(R.layout.max_attemps_dialog);
                                                            dialog.setTitle(getString(R.string.max_attemp_title));
                                                            ((TextView) (dialog.findViewById(R.id.messageTextView))).setText(getString(R.string.max_attemps));
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
                                                            Toast.makeText(getApplicationContext(), getString(R.string.invalid_password) + " Te quedan " + limit + " intentos ", Toast.LENGTH_SHORT).show();
                                                            spinner.setVisibility(View.INVISIBLE);
                                                        }
                                                    }
                                                });



                                                return;
                                            }


                                        }catch (Exception e){

                                            e.printStackTrace();
                                            e.getMessage();
                                        }



















                                        runOnUiThread(new Runnable() {
                                            public void run() {

                                            }
                                        });
                                    } catch (Exception e) {

                                        e.printStackTrace();
                                    }

                                }
                            }).start();












                        }catch (Exception ex){
                            Toast.makeText( getApplicationContext(), getString(R.string.general_error) +ex.getMessage(),Toast.LENGTH_LONG).show();
                        }






                    }
                }

        );







    }






}
