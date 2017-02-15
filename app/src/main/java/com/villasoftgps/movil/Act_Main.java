package com.villasoftgps.movil;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import adapters.MenuItemAdapter;
import adapters.DevicesAdapter;
import classes.Preferences;
import classes.WebService;
import controls.VillaDialog;
import controls.VillaImageView;
import controls.VillaTextView;
import models.Client;
import models.Dealer;
import models.DeviceModel;
import models.DeviceTrace;
import models.MenuItem;
import models.Vehicle;
import models.Device;

public class Act_Main extends FragmentActivity implements OnMapReadyCallback {

    private SharedPreferences villaprefs;
    private SharedPreferences.Editor prefsedit;
    private static String PREFS_NAME = "villaprefs";
    private static String PROPERTY_USER = "client";
    private static String PROPERTY_PREFS = "preferences";
    private static String TAG = "EJVH";
    private GoogleMap mapa;
    private VillaDialog villaDialog;
    private Button btnMenu;
    private Button btnMapaRoad;
    private Button btnMapaSatelite;
    private Button btnLock;
    private Button btnLogout;
    private DevicesAdapter devicesAdapter;
    private DrawerLayout drawerLayout;
    private ListView lvDevices;
    private Client client;
    private Preferences preferences;
    private Gson gson;
    private String mensaje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            Intent frm = new Intent(Act_Main.this,Act_Login.class);
            startActivity(frm);
            finish();
        }

        gson = new Gson();

        /*
        se instancia la clase cliente con los datos del usuario almacenado en las preferencias
         */
        client = new Client();
        client = gson.fromJson(strClient,Client.class);

        /*
        se instancia la clase que contiene los atributos u opciones de las preferencias
         */
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
            Intent frm_Locked = new Intent(Act_Main.this,Act_Locked.class);
            startActivity(frm_Locked);
        }

        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        /*
        se instancia el objeto del mapa
         */
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        /*
        se instancia el listview que contendrá la lista de dispositivos
        y vehiculos del cliente logueado.
         */
        lvDevices = (ListView)findViewById(R.id.lvDevices);

        View lvDevicesHeader = getLayoutInflater().inflate(R.layout.lv_menu_header,null);
        VillaTextView lblEmail = (VillaTextView)lvDevicesHeader.findViewById(R.id.lblEmail);
        VillaTextView lblUser = (VillaTextView)lvDevicesHeader.findViewById(R.id.lblUser);
        VillaImageView imgProfile = (VillaImageView)lvDevicesHeader.findViewById(R.id.imgProfile);

        String userName = client.getFirstName() + " " + client.getLastName();
        lblUser.setText(userName);
        String userEmail = "(" + client.getEmail() + ")";
        lblEmail.setText(userEmail);

        if (client.getImage().equals("")){
            Glide.with(Act_Main.this)
                    .load(R.drawable.icon_profile)
                    .centerCrop()
                    .crossFade()
                    .into(imgProfile);
        }else{
            byte[] image = Base64.decode(client.getImage(), 0);

            Glide.with(Act_Main.this)
                    .load(image)
                    .centerCrop()
                    .crossFade()
                    .into(imgProfile);
        }

        /*
        se cargan los items del lvDevices y el adapter
         */
        lvDevices.addHeaderView(lvDevicesHeader);
        devicesAdapter = new DevicesAdapter(Act_Main.this,client.getDevices());
        lvDevices.setAdapter(devicesAdapter);

        /*
        se ejecuta el procedimiento asincrono para cargar la lista de dispositivos
        correspondientes al cliente logueado
         */
        new AsyncGetDevices().execute(client.getClientId());

        /*
        se instancian el resto de controles que contiene la actividad
         */
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_menu);
        btnMenu = (Button)findViewById(R.id.btnMenu);
        btnMapaRoad = (Button)findViewById(R.id.btnMapaRoad);
        btnMapaSatelite = (Button)findViewById(R.id.btnMapaSatelite);
        btnLock = (Button)findViewById(R.id.btnLock);
        btnLogout = (Button)findViewById(R.id.btnLogout);

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarOcultarMenu();
            }
        });

        btnMapaRoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        });

        btnMapaSatelite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapa.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            }
        });

        btnLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent frmLocked = new Intent(Act_Main.this,Act_Locked.class);
                startActivity(frmLocked);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        LOGOUT,
        DO_NOTHING,
        RESTART_APP,
        VALIDATE,
        GCMREGISTER
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
        prefsedit.putString(PROPERTY_USER,"");
        prefsedit.apply();
        startActivity(frm);
        this.finish();
    }

    public void mostrarOcultarMenu(){
        if (drawerLayout.isDrawerOpen(lvDevices)) {
            drawerLayout.closeDrawers();
        } else {
            drawerLayout.openDrawer(lvDevices);
        }
    }

    private class AsyncGetDevices extends AsyncTask<Integer, Integer, Integer> {
        @Override
        protected Integer doInBackground(Integer... params) {
            publishProgress(0);

            ArrayList<Object> parametros = new ArrayList<>(2);
            parametros.add(0, "clientId*" + params[0]);
            parametros.add(1, "getDevices");

            WebService ws = new WebService();
            Object response = ws.getData(parametros);

            try {
                JSONObject json = new JSONObject(response.toString());
                String result = json.get("Result").toString();

                switch (result) {
                    case "OK":
                        JSONArray array = json.getJSONArray("Devices");

                        client.getDevices().clear();

                        for (int i = 0; i < array.length(); i++){
                            /*
                            se instancias las variables que contendrán los datos correspondiente
                            a las clases modelos que estan dentro de la clase "Device"
                             */
                            JSONObject dev = array.getJSONObject(i);
                            JSONObject veh = new JSONObject(dev.getString("Vehicle"));
                            JSONObject devMod = new JSONObject(dev.getString("DeviceModel"));
                            JSONObject dea = new JSONObject(dev.getString("Dealer"));
                            JSONObject tra = new JSONObject(dev.getString("DeviceTrace"));

                            /*
                            variables que se utilizaran para parsear la fecha registro y expiracion
                            a valores de tipo Long validos
                             */
                            String regDate = "";
                            String expDate = "";
                            String traDate = "";
                            /*
                            se instancia la clase Device
                             */
                            Device device = new Device();

                            /*
                            se llena la informacion correspondiente al dispositivo
                             */
                            device.setDeviceId(dev.getInt("DeviceId"));
                            device.setImei(dev.getString("Imei"));
                            device.setPhoneNumber(dev.getString("PhoneNumber"));
                            device.setIp(dev.getString("Ip"));
                            device.setPort(dev.getInt("Port"));

                            if (dev.getString("RegistrationDate").matches("^/Date\\(\\d+\\)/$")) {
                                regDate = dev.getString("RegistrationDate").replaceAll("^/Date\\((\\d+)\\)/$", "$1");
                            }
                            device.setRegistrationDate(Long.parseLong(regDate));

                            if (dev.getString("ExpirationDate").matches("^/Date\\(\\d+\\)/$")) {
                                expDate = dev.getString("ExpirationDate").replaceAll("^/Date\\((\\d+)\\)/$", "$1");
                            }
                            device.setExpirationDate(Long.parseLong(expDate));
                            device.setStatusDealer(dev.getInt("StatusDealer"));
                            device.setStatusSell(dev.getInt("StatusSell"));
                            device.setFreeSuspension(dev.getInt("FreeSuspension"));
                            device.setBatteryEvent(dev.getInt("BatteryEvent"));
                            device.setGeofenceEvent(dev.getInt("GeofenceEvent"));
                            device.setSpeedEvent(dev.getInt("SpeedEvent"));
                            device.setPanicEvent(dev.getInt("PanicEvent"));
                            device.setIgnitionNotification(dev.getInt("IgnitionNotification"));
                            device.setBatteryNotification(dev.getInt("BatteryNotification"));
                            device.setGeofenceNotification(dev.getInt("GeofenceNotification"));
                            device.setSpeedNotification(dev.getInt("SpeedNotification"));

                            /*
                            se llena la informacion correspondiente al vehiculo
                             */
                            Vehicle vehicle = new Vehicle();
                            vehicle.setVehicleId(veh.getInt("VehicleId"));
                            vehicle.setLicensePlate(veh.getString("LicensePlate"));
                            vehicle.setBrand(veh.getString("Brand"));
                            vehicle.setModel(veh.getString("Model"));
                            vehicle.setYear(veh.getInt("Year"));
                            vehicle.setColor(veh.getString("Color"));
                            vehicle.setStatus(veh.getInt("Status"));
                            vehicle.setImage(veh.getString("Image"));
                            device.setVehicle(vehicle);

                            /*
                            se llena la informacion correspondiente al modelo del dispositivo
                             */
                            DeviceModel deviceModel = new DeviceModel();
                            deviceModel.setDeviceModelId(devMod.getInt("DeviceModelId"));
                            deviceModel.setBrand(devMod.getString("Brand"));
                            deviceModel.setModel(devMod.getString("Model"));
                            device.setDeviceModel(deviceModel);

                            /*
                            se llena la informacion correspondiente al dealer
                             */
                            Dealer dealer = new Dealer();
                            dealer.setDealerId(dea.getInt("DealerId"));
                            dealer.setId(dea.getLong("Id"));
                            dealer.setFirstName(dea.getString("FirstName"));
                            dealer.setLastName(dea.getString("LastName"));
                            dealer.setEmail(dea.getString("Email"));
                            dealer.setPhoneNumber1(dea.getString("PhoneNumber1"));
                            dealer.setPhoneNumber2(dea.getString("PhoneNumber2"));
                            dealer.setAddress(dea.getString("Address"));
                            dealer.setStatus(dea.getInt("Status"));
                            dealer.setImage(dea.getString("Image"));
                            device.setDealer(dealer);

                            /*
                            se llena la informacion correspondiente a la ubicacion actual
                             */
                            DeviceTrace deviceTrace = new DeviceTrace();
                            if (tra.getInt("DeviceTraceId") != 0){
                                deviceTrace.setDeviceTraceId(tra.getInt("DeviceTraceId"));
                                deviceTrace.setEvent(tra.getString("Event"));
                                if (tra.getString("DateTime").matches("^/Date\\(\\d+\\)/$")) {
                                    traDate = tra.getString("DateTime").replaceAll("^/Date\\((\\d+)\\)/$", "$1");
                                }
                                deviceTrace.setDateTime(Long.parseLong(traDate));
                                deviceTrace.setFix(tra.getString("Fix"));
                                deviceTrace.setLatitude((float)tra.get("Latitude"));
                                deviceTrace.setLongitude((float)tra.get("Longitude"));
                                deviceTrace.setSpeed(tra.getInt("Speed"));
                                deviceTrace.setOrientation(tra.getInt("Orientation"));
                            }
                            device.setDeviceTrace(deviceTrace);

                            /*
                            se agrega el dispositivo a la lista
                             */
                            client.getDevices().add(device);
                        }

                        if (client.getDevices().isEmpty()){
                            publishProgress(1);
                        }else{
                            publishProgress(2);
                        }

                        return 1;
                    case "NO VEHICLES":
                        publishProgress(3);
                        return 1;
                    default:
                        publishProgress(4);
                        return 0;
                }
            } catch (JSONException e) {
                publishProgress(4);
                return 0;
            }
        }

        @SuppressWarnings("ConstantConditions")
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            switch (values[0]){
                case 0:
                    break;
                case 1:
                    break;
                case 2:
                    prefsedit.putString(PROPERTY_USER,gson.toJson(client));
                    prefsedit.apply();
                    devicesAdapter.notifyDataSetChanged();
                    break;
                case 3:
                    mensaje = getString(R.string.warning_no_pass);
                    mostrarMensaje(false, DialogType.MESSAGE, R.drawable.icon_warning, mensaje,Ejecutar.DO_NOTHING);
                    break;
                case 4:
                    mensaje = getString(R.string.warning_no_pass);
                    mostrarMensaje(false, DialogType.MESSAGE, R.drawable.icon_warning, mensaje,Ejecutar.DO_NOTHING);
                    break;
            }
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
        }
    }
}
