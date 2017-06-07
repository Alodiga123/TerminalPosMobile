package com.bbpos.bbdevice.example;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by usuario on 24/05/17.
 */

public class IndexActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.index_activity);
        Intent intent = getIntent();
        final String token = intent.getStringExtra("token");


        final Button Sales=(Button) findViewById(R.id.btnVentas);
        Sales.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newForms = new Intent(com.bbpos.bbdevice.example.IndexActivity.this, com.bbpos.bbdevice.example.MainActivity.class);
                newForms.putExtra("token",token);
                startActivity(newForms);
            }
        });
    }
}
