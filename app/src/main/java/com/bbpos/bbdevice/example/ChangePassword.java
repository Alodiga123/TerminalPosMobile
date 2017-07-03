package com.bbpos.bbdevice.example;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alodiga.security.encryption.S3cur1ty3Cryt3r;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

/**
 * Created by anyeli on 19/06/17.
 */

public class ChangePassword extends Activity {

    @NonNull
    private static String currentPassword;
    @NonNull
    private static String newPassword;
    @NonNull
    private static String confirmPassword;
    private static int messageForToast;
    private int forwardIndicator;
    ProgressBar progressBar;
    ProgressDialog progressDialogRing;
    private EditText edtNewPassword ;
    private EditText edtCurrentPassword;
    private EditText edtConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        progressBar = (ProgressBar) findViewById(R.id.progressBarPassword);
        edtNewPassword = (EditText) findViewById(R.id.editTextNewPassword);

        edtNewPassword.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                forwardIndicator= Utils.progressBar(edtNewPassword.getText().toString());
                progressBar.setProgress(forwardIndicator);
            }
            @Override
            public void afterTextChanged(Editable s)
            {
            }
        });
    }

    public void evchangePassword(View view)
    {
        edtNewPassword  = (EditText) findViewById(R.id.editTextNewPassword);
        edtCurrentPassword  = (EditText) findViewById(R.id.editTextCurrentPassword);
        edtConfirmPassword  = (EditText) findViewById(R.id.editTextConfirmPassword);
        currentPassword = edtCurrentPassword.getText().toString();
        newPassword     = edtNewPassword.getText().toString();
        confirmPassword = edtConfirmPassword.getText().toString();

        if(!currentPassword.trim().equals("") && !newPassword.trim().equals("") && !confirmPassword.trim().equals(""))
        {
            messageForToast= Utils.validatePassword(newPassword, confirmPassword);

            if(messageForToast != 0)
            {
                Utils.createToast(getApplicationContext(), messageForToast);
            }else
            {
                progressDialogRing = new ProgressDialog(this);
                progressDialogRing.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialogRing.setMessage("Procesando Transaccion");
                progressDialogRing.setCancelable(false);
                progressDialogRing.setMax(100);
                try
                {
                    String androidIDV = Utils.getAndroidId(getApplicationContext());
                    String keyV = Utils.getkey(androidIDV);
                    String password = currentPassword +";"+ newPassword;

                    ChangePassword.ChangePasswordService changePassword = new ChangePassword.ChangePasswordService(this.getApplicationContext(),keyV,androidIDV, password);
                    changePassword.execute();

                } catch (Exception e)
                {
                    Toast.makeText(ChangePassword.this, "Error Generar",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }else
        {
            Utils.createToast(getApplicationContext(), R.string.required_fields);
        }
    }

    private void durationProgressDialog()
    {
        try
        {
            Thread.sleep(1000);
        }catch (InterruptedException e)
        {
            Toast.makeText(ChangePassword.this, "Error Generar",
                    Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public class ChangePasswordService extends AsyncTask<Void, Integer, Boolean>
    {
        private Context context;
        private String key;
        private String androidID;
        private String password;

        public ChangePasswordService(Context c, String k, String aID, String pass ) {
            this.context = c;
            this.key = k;
            this.androidID = aID;
            this.password = pass;
        }

        private String NAMESPACE;
        private String URL;
        private String METHOD_NAME ;
        private String responceReceived;
        private static final int CANTIDAD_PARAMETROS_DE_ENTRADA = 2;
        private boolean serviceStatus;
        @Override
        protected Boolean doInBackground(Void... params)
        {
            try {
                //Namespace of the Webservice - can be found in WSDL
                NAMESPACE = "http://services.ws.acquiring.alodiga.com/";
                //Webservice URL - WSDL File location
                URL = "http://192.168.3.144:8080/AcquiringWSServicesProviderService/AcquiringWSServicesProvider";
                //SOAP Action URI again Namespace + Web method name
                METHOD_NAME = "updateTerminalPassword";
                final String androidIdEncrypted = Utils.sha256(androidID);
                final String passwordEncrypted = S3cur1ty3Cryt3r.aloEncrypter(password,key,null);
                EncryptedParameter encrypterParam[] = new EncryptedParameter[CANTIDAD_PARAMETROS_DE_ENTRADA];
                EncryptedParameter encrypterParamObject = new EncryptedParameter();
                EncryptedParameter encrypterParamObjectAndroid = new EncryptedParameter();

                encrypterParamObject.name = "encrypted";
                encrypterParamObject.encryptedString = passwordEncrypted;
                encrypterParamObject.type = PropertyInfo.STRING_CLASS;
                encrypterParam[0] = encrypterParamObject;

                encrypterParamObjectAndroid.name = "androidId";
                encrypterParamObjectAndroid.encryptedString = androidIdEncrypted;
                encrypterParamObjectAndroid.type = PropertyInfo.STRING_CLASS;
                encrypterParam[1] = encrypterParamObjectAndroid;

                SoapObject requestReceived = Utils.buildRequest(encrypterParam, NAMESPACE, METHOD_NAME);
                responceReceived = Utils.processPetition(requestReceived, URL, key);
                String[] var= responceReceived.split(",");

                if(var[0].equals(ErrorMessages.TERMINAL_OLD_PASSWORD_NOT_FOUND) || var[0].equals(ErrorMessages.TERMINAL_REPEATED_PASSWORD))
                {
                    serviceStatus = false;

                }else if(var[0].equals(ErrorMessages.SUCCESSFUL_OPERATION))
                {
                    for (int i = 1; i <= 2; i++)
                    {
                        durationProgressDialog();
                        publishProgress(i * 10);
                        if (isCancelled())
                            break;
                    }
                    serviceStatus = true;
                }

            } catch (IllegalArgumentException e)
            {
                e.printStackTrace();
                System.err.println(e);
                return false;

            } catch (Exception e)
            {
                e.printStackTrace();
                System.err.println(e);
                return false;
            }
           return serviceStatus;
        }

        @Override
        protected void onProgressUpdate(Integer... values)
        {
            int progreso = values[0].intValue();
            progressDialogRing.setProgress(progreso);
        }

        @Override
        protected void onPreExecute()
        {
            progressDialogRing.setOnCancelListener(new DialogInterface.OnCancelListener()
            {
                @Override
                public void onCancel(DialogInterface dialog)
                {
                    ChangePassword.ChangePasswordService.this.cancel(true);
                }
            });
            progressDialogRing.setProgress(0);
            progressDialogRing.show();
        }

        @Override
        protected void onPostExecute(Boolean result)
        {
            if(result)
            {
                edtNewPassword.getText().clear();
                edtCurrentPassword.getText().clear();
                edtConfirmPassword.getText().clear();
                Intent i = new Intent(ChangePassword.this, SuccessfulKeyChange.class);
                startActivity(i);
                progressDialogRing.dismiss();
            }else
            {
                Utils.createToast(context, R.string.invalid_password);
                progressDialogRing.dismiss();
            }
        }

        @Override
        protected void onCancelled()
        {
            Toast.makeText(ChangePassword.this, "Tarea cancelada!",
                    Toast.LENGTH_SHORT).show();
        }
    }

}

