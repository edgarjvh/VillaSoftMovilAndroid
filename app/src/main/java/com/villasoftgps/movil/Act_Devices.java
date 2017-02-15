package com.villasoftgps.movil;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.ListView;

import com.google.gson.Gson;

import adapters.DevicesAdapter;
import classes.Preferences;
import controls.VillaDialog;
import models.Client;

public class Act_Devices extends Activity {

    private SharedPreferences villaprefs;
    private SharedPreferences.Editor prefsedit;
    private static String PREFS_NAME = "villaprefs";
    private static String PROPERTY_USER = "client";
    private static String PROPERTY_PREFS = "preferences";
    private static String TAG = "EJVH";
    private VillaDialog villaDialog;
    private Client client;
    private Preferences preferences;
    private Gson gson;
    private ListView lvDevices;
    private DevicesAdapter devicesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // se instancia las preferencias de la aplicacion
        if (villaprefs == null){
            villaprefs = getApplicationContext().getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        }

        // se instancia el editor de preferencias
        prefsedit = villaprefs.edit();

        /*
         * se valida que exista un usuario en las preferencia
         * de lo contrario se cierra la sesión y se redirecciona al login
         */
        String strClient = villaprefs.getString(PROPERTY_USER,"");
        if (strClient.equals("")){
            Intent frm = new Intent(Act_Devices.this,Act_Login.class);
            startActivity(frm);
            finish();
        }

        /*
        se instancia la clase que contiene los atributos u opciones de las preferencias
         */
        gson = new Gson();
        preferences = new Preferences();

        String strPreferences = villaprefs.getString(PROPERTY_PREFS,"");
        if (!strPreferences.equals("")){
            preferences = gson.fromJson(villaprefs.getString(PROPERTY_PREFS,""),Preferences.class);
        }

        /*
        se valida si la aplicacion se encuentra en status de bloqueo,
        de ser asi, se redirecciona a la actividad de bloqueado.
         */
        if (preferences.isLocked()){
            Intent frm_Locked = new Intent(Act_Devices.this,Act_Locked.class);
            startActivity(frm_Locked);
        }

        /*
        se instancia la clase cliente con los datos del usuario almacenado en las preferencias
         */
        client = new Client();
        client = gson.fromJson(villaprefs.getString(PROPERTY_USER,""),Client.class);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        lvDevices = (ListView)findViewById(R.id.lvDevices);
        devicesAdapter = new DevicesAdapter(Act_Devices.this,client.getDevices());
        lvDevices.setAdapter(devicesAdapter);
    }
}
