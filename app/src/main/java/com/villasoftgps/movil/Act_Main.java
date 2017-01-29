package com.villasoftgps.movil;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import controls.VillaDialog;

public class Act_Main extends FragmentActivity implements OnMapReadyCallback {

    private SharedPreferences villaprefs;
    private static String PROPERTY_CLIENT = "cliente";
    private static String PROPERTY_PREFS = "preferencias";
    private static String TAG = "EJVH";
    private GoogleMap mapa;
    private VillaDialog villaDialog;
    private Button btnMenu;
    private Button btnMapaRoad;
    private Button btnMapaSatelite;
    private Button btnStopEngine;
    private Button btnResumeEngine;
    private Button btnLock;
    private Button btnLogout;
    private Spinner cboDispositivos;
    private DrawerLayout drawerLayout;
    private ListView lstMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_menu);
        lstMenu = (ListView)findViewById(R.id.lstMenu);
        btnMenu = (Button)findViewById(R.id.btnMenu);
        btnMapaRoad = (Button)findViewById(R.id.btnMapaRoad);
        btnMapaSatelite = (Button)findViewById(R.id.btnMapaSatelite);
        btnStopEngine = (Button)findViewById(R.id.btnStopEngine);
        btnResumeEngine = (Button)findViewById(R.id.btnResumeEngine);
        btnLock = (Button)findViewById(R.id.btnLock);
        btnLogout = (Button)findViewById(R.id.btnLogout);

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarOcultarMenu();
            }
        });

        final MediaPlayer mp = MediaPlayer.create(Act_Main.this,R.raw.button_sound2);

        btnMapaRoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();
                mapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        });

        btnMapaSatelite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();
                mapa.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            }
        });

        btnStopEngine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnResumeEngine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();

                mostrarMensaje(
                        false,
                        DialogType.PROMPT,
                        R.drawable.icon_prompt,
                        getString(R.string.prompt_cerrar_sesion),
                        Ejecutar.LOGOUT);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapa = googleMap;

        LatLng cabimas = new LatLng(10.409513, -71.440102);
        mapa.moveCamera(CameraUpdateFactory.newLatLng(cabimas));
    }

    private enum DialogType{
        PROGRESS,
        MESSAGE,
        PROMPT,
        IMAGE
    }

    private enum Ejecutar{
        LOGOUT
    }

    private void mostrarMensaje(boolean isWelcome, DialogType dialogType, int icon, String message, final Ejecutar ejecutar){
        try{
            if(isWelcome){
                if(villaDialog != null) {
                    villaDialog.dismiss();
                    villaDialog = null;
                }

                villaDialog = new VillaDialog(Act_Main.this, VillaDialog.DialogType.MESSAGE, message,icon);
                villaDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                villaDialog.setCanceledOnTouchOutside(false);
                villaDialog.show();

                CountDownTimer timer = new CountDownTimer(3000,1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {

                    }
                };
                timer.start();
            }else{
                if(dialogType == DialogType.PROGRESS){
                    if(villaDialog != null){
                        villaDialog.dismiss();
                        villaDialog = null;
                    }

                    villaDialog = new VillaDialog(Act_Main.this, VillaDialog.DialogType.PROGRESS, message);
                    villaDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    villaDialog.setCanceledOnTouchOutside(true);
                    villaDialog.show();
                }
                else if(dialogType == DialogType.PROMPT){
                    if(villaDialog != null) {
                        villaDialog.dismiss();
                        villaDialog = null;
                    }

                    villaDialog = new VillaDialog(Act_Main.this, VillaDialog.DialogType.PROMPT, message, icon);
                    villaDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    villaDialog.setCanceledOnTouchOutside(false);

                    villaDialog.setListener(new VillaDialog.DialogListener() {
                        @Override
                        public void Ok() {
                            switch (ejecutar){
                                case LOGOUT:
                                    cerrarSesion();
                                    break;
                            }
                        }

                        @Override
                        public void Cancel() {
                            if(villaDialog != null) {
                                villaDialog.dismiss();
                                villaDialog = null;
                            }
                        }
                    });

                    villaDialog.show();
                }
                else{
                    if(villaDialog != null) {
                        villaDialog.dismiss();
                        villaDialog = null;
                    }

                    villaDialog = new VillaDialog(Act_Main.this, VillaDialog.DialogType.MESSAGE, message, icon);
                    villaDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    villaDialog.setCanceledOnTouchOutside(true);
                    villaDialog.show();

                    CountDownTimer timer = new CountDownTimer(3000,1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                        }

                        @Override
                        public void onFinish() {
                            if(villaDialog != null){
                                villaDialog.dismiss();
                                villaDialog = null;
                            }
                        }
                    };
                    timer.start();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void cerrarSesion(){
        Intent frm = new Intent(Act_Main.this,Act_Login.class);
        startActivity(frm);
        this.finish();
    }

    public void mostrarOcultarMenu(){
        if (drawerLayout.isDrawerOpen(lstMenu)) {
            drawerLayout.closeDrawers();
        } else {
            drawerLayout.openDrawer(lstMenu);
        }
    }
}
