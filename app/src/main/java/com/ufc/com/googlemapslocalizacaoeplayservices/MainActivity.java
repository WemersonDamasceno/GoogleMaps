package com.ufc.com.googlemapslocalizacaoeplayservices;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

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

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS)
                != PackageManager.PERMISSION_DENIED){
            return;
        }

        //Pegando a ultima localização
        client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    Log.i("Sucess_Location","Lat "+location.getLatitude()+" Long "+location.getLongitude());
                }else {
                    Log.i("Erro_Location", "Location is Null");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        //Pegar posicao mais precisa com o FINE
        final LocationRequest locationRequest = LocationRequest.create();
        //Intervalo de vezes que o app vai procurar a loxalização do usuario defini 15 segundos
        locationRequest.setInterval(15 * 1000);
        //Localizacao vindas de outros App's
        locationRequest.setFastestInterval(5 * 1000);
        //Prioridade de precisao e bateria
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);


        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);


        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(builder.build())
                .addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        //verificar se o GPS esta ativo e outras coisas
                        // locationSettingsResponse.getLocationSettingsStates().isGpsPresent();
                        Log.i("Teste", locationSettingsResponse.
                                getLocationSettingsStates().isNetworkLocationPresent()+" que tem net");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if(e instanceof ResolvableApiException){
                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MainActivity.this,10);
                    }catch (IntentSender.SendIntentException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        LocationCallback locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if(locationRequest == null){
                    Log.i("teste2", "Location is null");
                    return;
                }
                for ( Location location : locationResult.getLocations()) {
                    Log.i("teste2", location.getLatitude()+"");
                }
            }

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                //se a localização esta disponivel ou nao
                Log.i("teste2", locationAvailability.isLocationAvailable()+"");
                super.onLocationAvailability(locationAvailability);
            }
        };
        client.requestLocationUpdates(locationRequest, locationCallback, null);


        super.onResume();
    }
}
