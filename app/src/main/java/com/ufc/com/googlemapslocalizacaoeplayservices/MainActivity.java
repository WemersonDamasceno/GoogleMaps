package com.ufc.com.googlemapslocalizacaoeplayservices;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity {
    FusedLocationProviderClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //PEGAR EVENTOS DA LOCALIZAÇÃO DO CLIENTE
        client = LocationServices.getFusedLocationProviderClient(this);








    }



    @Override
    protected void onResume() {
        //saber se o google services está atualizado
        int erro = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if((erro == ConnectionResult.SERVICE_MISSING)
                || (erro == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED)
                || (erro == ConnectionResult.SERVICE_DISABLED)) {
            Toast.makeText(this, "Fail | Baixe/ Ative/ Atualize o Google Play Services", Toast.LENGTH_SHORT).show();
            GoogleApiAvailability.getInstance()
                    .getErrorDialog(this, erro, 0, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    //matar a activity, pois o programa nao pode rodar sem o google play
                    finish();
                }
            }).show();
        }else if(erro == ConnectionResult.SUCCESS){
            //Toast.makeText(this, "Sucess | Google services", Toast.LENGTH_SHORT).show();
        }

        super.onResume();
    }
}
