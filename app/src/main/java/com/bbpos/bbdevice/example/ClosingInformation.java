package com.bbpos.bbdevice.example;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by anyeli on 15/06/17.
 */

public class ClosingInformation extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_closing_information);

        String parametrosDeCierre;
        parametrosDeCierre = getIntent().getStringExtra("parametros");

        String[] informacionCierre = parametrosDeCierre.split(",");

        String hora               = informacionCierre[1];
        String fecha              = informacionCierre[1];
        String lote               = informacionCierre[2];
        String totalAprobado      = informacionCierre[3];
        String totalTransEnviadas = informacionCierre[4];
        String montoTotalCierre   = informacionCierre[5];


        TextView txtHORA               = (TextView) findViewById(R.id.txtHora);
        TextView txtFECHA              = (TextView) findViewById(R.id.txtFecha);
        TextView txtLOTE               = (TextView) findViewById(R.id.txtLote);
        TextView txtTotalAprobado      = (TextView) findViewById(R.id.txtAprobado);
        TextView txtTotalTransEnviadas = (TextView) findViewById(R.id.txtTransaccionEnviada);
        TextView txtmontoTotalCierre   = (TextView) findViewById(R.id.txtMontoCierre);

        txtHORA.setText(hora);
        txtFECHA.setText(fecha);
        txtLOTE.setText(lote);
        txtTotalAprobado.setText(totalAprobado);
        txtTotalTransEnviadas.setText(totalTransEnviadas);
        txtmontoTotalCierre.setText(montoTotalCierre);

    }

}
