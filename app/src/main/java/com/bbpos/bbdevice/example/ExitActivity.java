package com.bbpos.bbdevice.example;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

/**
 * Created by anyeli on 15/06/17.
 */

public class ExitActivity extends Activity
{
    ProgressDialog progressDialogRing;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button_exit);
    }

    public void sendClose(View view)
    {
        progressDialogRing = new ProgressDialog(this);
        progressDialogRing.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialogRing.setMessage("Procesando Transaccion");
        progressDialogRing.setCancelable(false);
        progressDialogRing.setMax(100);

        try
        {
            String androidIDV = Utils.getAndroidId(getApplicationContext());
            String keyV = Utils.getkey(getApplicationContext(),androidIDV);

            CancelActivityService cancelActivity = new CancelActivityService(this.getApplicationContext(),keyV,androidIDV);
            cancelActivity.execute();

        } catch (Exception e)
        {
            Toast.makeText(ExitActivity.this, "Error Generar",
                    Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void durationProgressDialog()
    {
        try
        {
            Thread.sleep(1000);
        }catch (InterruptedException e)
        {
            Toast.makeText(ExitActivity.this, "Error Generar",
                    Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public class CancelActivityService extends AsyncTask<Void, Integer, Boolean>
    {

        private Context context;
        private String key;
        private String androidID;

        public CancelActivityService(Context c, String k, String aID) {
            this.context = c;
            this.key = k;
            this.androidID = aID;
        }

        private String NAMESPACE;
        private String URL;
        private String METHOD_NAME ;
        private String responceReceived;
        private static final int CANTIDAD_PARAMETROS_DE_ENTRADA = 2;

        @Override
        protected Boolean doInBackground(Void... params)
        {
            try
            {
                //Namespace of the Webservice - can be found in WSDL
                NAMESPACE = "http://core.service.network.acquiring.com/";
                //Webservice URL - WSDL File location
                URL = "http://ec2-35-167-158-127.us-west-2.compute.amazonaws.com:8080/WsCoreService/WsCore";
                //SOAP Action URI again Namespace + Web method name
                METHOD_NAME = "closeLotByTerminalId";

                final String  androidIdCifrado = Utils.sha256(androidID);
                // final String  androidIdCifrado = Utils.sha256("");

                EncryptedParameter encrypterParam[]  = new EncryptedParameter[CANTIDAD_PARAMETROS_DE_ENTRADA];
                EncryptedParameter encrypterParamObject = new EncryptedParameter();

                encrypterParamObject.name = "deviceId";
                encrypterParamObject.encryptedString = androidIdCifrado;
                encrypterParamObject.type = PropertyInfo.STRING_CLASS;
                encrypterParam[0] = encrypterParamObject;

                SoapObject requestReceived =  Utils.buildRequest(encrypterParam,NAMESPACE,METHOD_NAME);
                responceReceived = Utils.processPetition(requestReceived, URL, key);

                for(int i=1; i<=10; i++)
                {
                    durationProgressDialog();
                    publishProgress(i*10);

                    if(isCancelled())
                        break;
                }
            }catch (Exception e)
            {
                Toast.makeText(ExitActivity.this, "erroooor!",
                        Toast.LENGTH_SHORT).show();
            }

            return true;
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
                    CancelActivityService.this.cancel(true);
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
                Intent i = new Intent(ExitActivity.this, ClosingInformation.class);
                i.putExtra("parametros", responceReceived);
                startActivity(i);
                progressDialogRing.dismiss();
            }
        }

        @Override
        protected void onCancelled()
        {
            Toast.makeText(ExitActivity.this, "Tarea cancelada!",
                    Toast.LENGTH_SHORT).show();
        }/**/
    }

}
