package com.bbpos.bbdevice.example;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by anyeli on 15/06/17.
 */

public class ClosingInformation extends Activity
{

    private static final String CHARACTER_SEPARATOR = ";";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_closing_information);

        String parametrosDeCierre = getIntent().getStringExtra("parametros");

        if(parametrosDeCierre != null && parametrosDeCierre != " " )
        {
            String[] closingInformation = parametrosDeCierre.split(CHARACTER_SEPARATOR);

            int totalTransactionsSuccess =  Integer.parseInt(closingInformation[9].substring(16));
            int totalTransactionsCancel = Integer.parseInt(closingInformation[10].substring(17));
            String totalTransactionsSent = String.valueOf(totalTransactionsSuccess + totalTransactionsCancel);

            TextView txtHOUR             = (TextView) findViewById(R.id.txtHour);
            TextView txtDATE             = (TextView) findViewById(R.id.txtDate);
            TextView txtLOT              = (TextView) findViewById(R.id.txtLot);
            TextView txtTotalApproved    = (TextView) findViewById(R.id.txtTotalApproved);
            TextView txtSentTransactions = (TextView) findViewById(R.id.txtSentTransactions);
            TextView txtClosingAmount    = (TextView) findViewById(R.id.txtClosingAmount);

            txtHOUR.setText(closingInformation[3].substring(22,30));
            txtDATE.setText(closingInformation[3].substring(11,21));
            txtLOT.setText(closingInformation[5].substring(7,9));
            txtTotalApproved.setText(closingInformation[10].substring(17));
            txtSentTransactions.setText(totalTransactionsSent);
            txtClosingAmount.setText(closingInformation[14].substring(16));
        }
    }
}
