package com.bbpos.bbdevice.example;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

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
    EditText edtNewPassword ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        progressBar = (ProgressBar) findViewById(R.id.progressBarPassword);
       // final EditText edTxtNewPassword = (EditText) findViewById(R.id.editTextNewPassword);
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
        final EditText edtCurrentPassword  = (EditText) findViewById(R.id.editTextCurrentPassword);
        final EditText edtConfirmPassword  = (EditText) findViewById(R.id.editTextConfirmPassword);
        currentPassword = edtCurrentPassword.getText().toString();
        newPassword     = edtNewPassword.getText().toString();
        confirmPassword = edtConfirmPassword.getText().toString();

        if(!currentPassword.trim().equals("") && !newPassword.trim().equals("") && !confirmPassword.trim().equals(""))
        {
            messageForToast= Utils.validatePassword(newPassword, confirmPassword);
            //TODO- metodo que valida que la clave actual suministada sea correcta

            if(messageForToast != 0)
            {
                Utils.createToast(getApplicationContext(), messageForToast);
            }else
            {
                //TODO- metodo que llama al servicio de registro
            }
        }else
        {
            Utils.createToast(getApplicationContext(), R.string.required_fields);
        }
    }
}

