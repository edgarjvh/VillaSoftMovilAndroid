package com.villasoftgps.movil;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Locale;
import classes.Client;
import classes.Preferences;
import classes.WebService;
import controls.VillaDialog;
import controls.VillaImageView;
import controls.VillaTextView;

public class Act_Login extends AppCompatActivity {

    private SharedPreferences villaprefs;
    private SharedPreferences.Editor prefsedit;
    private static String PREFS_NAME = "villaprefs";
    private static String PROPERTY_CLIENT = "client";
    private static String PROPERTY_PREFS = "preferences";
    private Client client;
    private Preferences preferences;
    private static String TAG = "EJVH";
    private String mensaje;
    private VillaDialog villaDialog;
    private EditText txtEmail,txtPassword;
    private VillaTextView lblPasswordForgotten;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (villaprefs == null){
            villaprefs = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        }

        String sysLang = getResources().getConfiguration().locale.toString();
        final String curLang;
        gson = new Gson();

        if(villaprefs.getString(PROPERTY_PREFS,"").equals("")){
            if (sysLang.contains("es")){
                curLang = "es";
            }else{
                curLang = "en";
            }

            preferences = new Preferences();
            preferences.setLanguage(curLang);

            prefsedit = villaprefs.edit();
            prefsedit.putString(PROPERTY_PREFS, gson.toJson(preferences));
            prefsedit.apply();
        }else{
            preferences = gson.fromJson(villaprefs.getString(PROPERTY_PREFS,""), Preferences.class);
            curLang = preferences.getLanguage();

            if (!sysLang.contains(curLang)){
                Locale locale = new Locale(curLang);
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                getBaseContext().getResources().updateConfiguration(config,
                        getBaseContext().getResources().getDisplayMetrics());
                restartApp();
            }
        }

        /*
        validar preferences y status de sesion de usuario
         */

        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        VillaImageView imgLogo = (VillaImageView) findViewById(R.id.imgLogo);
        imgLogo.setVisibility(View.VISIBLE);
        Glide.with(Act_Login.this)
                .load(R.drawable.logo)
                .into(imgLogo);

        txtEmail = (EditText)findViewById(R.id.txtEmail);
        txtPassword = (EditText)findViewById(R.id.txtPassword);
        lblPasswordForgotten = (VillaTextView)findViewById(R.id.lblPasswordForgotten);
        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        Button btnRegister = (Button) findViewById(R.id.btnRegister);
        Button btnSpanish = (Button) findViewById(R.id.btnSpanish);
        Button btnEnglish = (Button) findViewById(R.id.btnEnglish);

        lblPasswordForgotten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent frm = new Intent(Act_Login.this,Act_ResetPassword.class);
                startActivity(frm);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtEmail.getText().toString().trim().equals("")){
                    String msj = getString(R.string.warning_must_enter_email);
                    mostrarMensaje(false,DialogType.MESSAGE,R.drawable.icon_warning,msj,Ejecutar.DO_NOTHING);
                    return;
                }

                if (txtPassword.getText().toString().trim().equals("")){
                    String msj = getString(R.string.warning_must_enter_clave);
                    mostrarMensaje(false,DialogType.MESSAGE,R.drawable.icon_warning,msj,Ejecutar.DO_NOTHING);
                    return;
                }

                new AsyncLogin().execute(
                        txtEmail.getText().toString().trim(),
                        txtPassword.getText().toString().trim());
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent frm = new Intent(Act_Login.this,Act_RegisterAccess.class);
                startActivity(frm);
            }
        });

        btnSpanish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gson = new Gson();
                preferences = gson.fromJson(villaprefs.getString(PROPERTY_PREFS,""), Preferences.class);

                if (!preferences.getLanguage().equals("es")){
                    Locale locale = new Locale("es");
                    Locale.setDefault(locale);
                    Configuration config = new Configuration();
                    config.locale = locale;
                    getBaseContext().getResources().updateConfiguration(config,
                            getBaseContext().getResources().getDisplayMetrics());

                    preferences.setLanguage("es");
                    prefsedit = villaprefs.edit();
                    prefsedit.putString(PROPERTY_PREFS,gson.toJson(preferences));
                    prefsedit.apply();

                    String msj = getString(R.string.restarting_due_lang_changed);
                    mostrarMensaje(false,DialogType.MESSAGE,R.drawable.icon_ok,msj,Ejecutar.RESTART_APP);
                }
            }
        });

        btnEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gson = new Gson();
                preferences = gson.fromJson(villaprefs.getString(PROPERTY_PREFS,""), Preferences.class);

                if (!preferences.getLanguage().equals("en")){
                    Locale locale = new Locale("en");
                    Locale.setDefault(locale);
                    Configuration config = new Configuration();
                    config.locale = locale;
                    getBaseContext().getResources().updateConfiguration(config,
                            getBaseContext().getResources().getDisplayMetrics());

                    preferences.setLanguage("en");
                    prefsedit = villaprefs.edit();
                    prefsedit.putString(PROPERTY_PREFS,gson.toJson(preferences));
                    prefsedit.apply();

                    String msj = getString(R.string.restarting_due_lang_changed);
                    mostrarMensaje(false,DialogType.MESSAGE,R.drawable.icon_ok,msj,Ejecutar.RESTART_APP);
                }
            }
        });
        txtEmail.requestFocus();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private class AsyncLogin extends AsyncTask<String, Integer, Integer> {
        @Override
        protected Integer doInBackground(String... params) {
            publishProgress(0);

            ArrayList<Object> parametros = new ArrayList<>(3);
            parametros.add(0, "email*" + params[0]);
            parametros.add(1, "password*"+ params[1]);
            parametros.add(2, "Login");

            WebService ws = new WebService();
            Object response = ws.getData(parametros);

            try {
                JSONObject json = new JSONObject(response.toString());

                String result = json.get("Result").toString();

                switch (result) {
                    case "OK":
                        JSONObject array = new JSONObject(json.get("Client").toString());

                        client = new Client();
                        client.setClientId(array.getInt("ClientId"));
                        client.setFirstName(array.get("FirstName").toString());
                        client.setLastName(array.getString("LastName"));
                        client.setPhoneNumber1(array.getString("PhoneNumber1"));
                        client.setPhoneNumber2(array.getString("PhoneNumber2"));
                        client.setAddress(array.getString("Address"));
                        client.setEmail(array.get("Email").toString());
                        client.setImage(array.getString("Image"));
                        publishProgress(1);
                        return 1;
                    case "NO USER":
                        publishProgress(2);
                        return 1;
                    case "NO PASS":
                        publishProgress(3);
                        return 1;
                    case "NO ROWS":
                        publishProgress(4);
                        return 1;
                    case "UNVALIDATED":
                        publishProgress(5);
                        return 1;
                    default:
                        publishProgress(6);
                        return 0;
                }
            } catch (JSONException e) {
                publishProgress(6);
                return 0;
            }
        }

        @SuppressWarnings("ConstantConditions")
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            switch (values[0]){
                case 0:
                    mensaje = getString(R.string.iniciandoSesion);
                    mostrarMensaje(false,DialogType.PROGRESS,0,mensaje,Ejecutar.DO_NOTHING);
                    break;
                case 1:
                    mensaje = getString(R.string.bienvenidoCliente) + "\n" + client.getFirstName() + " " + client.getLastName();
                    mostrarMensaje(true, DialogType.MESSAGE, R.drawable.icon_ok, mensaje,Ejecutar.DO_NOTHING);
                    break;
                case 2:
                    mensaje = getString(R.string.warning_no_user);
                    mostrarMensaje(false, DialogType.MESSAGE, R.drawable.icon_warning, mensaje,Ejecutar.DO_NOTHING);
                    break;
                case 3:
                    mensaje = getString(R.string.warning_no_pass);
                    mostrarMensaje(false, DialogType.MESSAGE, R.drawable.icon_warning, mensaje,Ejecutar.DO_NOTHING);
                    break;
                case 4:
                    mensaje = getString(R.string.warning_no_user);
                    mostrarMensaje(false, DialogType.MESSAGE, R.drawable.icon_warning, mensaje,Ejecutar.DO_NOTHING);
                    break;
                case 5:
                    mensaje = getString(R.string.unvalidated);
                    mostrarMensaje(true, DialogType.MESSAGE, R.drawable.icon_warning, mensaje,Ejecutar.VALIDATE);
                    break;
                case 6:
                    mostrarMensaje(false, DialogType.MESSAGE, R.drawable.icon_error, getString(R.string.connection_error),Ejecutar.DO_NOTHING);
                    break;
            }
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
        }
    }

    private enum DialogType{
        PROGRESS,
        MESSAGE,
        PROMPT,
        IMAGE
    }

    private enum Ejecutar{
        DO_NOTHING,
        RESTART_APP,
        VALIDATE
    }

    private void mostrarMensaje(boolean isWelcome, final DialogType dialogType, int icon, String message, final Ejecutar ejecutar){
        try{
            if(isWelcome){
                if(villaDialog != null) {
                    villaDialog.dismiss();
                    villaDialog = null;
                }

                villaDialog = new VillaDialog(Act_Login.this, VillaDialog.DialogType.MESSAGE, message,icon);
                villaDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                villaDialog.setCanceledOnTouchOutside(false);
                villaDialog.show();

                CountDownTimer timer = new CountDownTimer(3000,1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {
                        if (ejecutar == Ejecutar.VALIDATE){
                            Intent frm = new Intent(Act_Login.this, Act_RegisterValidation.class);
                            frm.putExtra("email",txtEmail.getText().toString().trim());
                            txtEmail.setText(null);
                            txtPassword.setText(null);
                            txtEmail.requestFocus();
                            startActivity(frm);
                        }else{
                            txtEmail.setText(null);
                            txtPassword.setText(null);
                            txtEmail.requestFocus();

                            Intent frm = new Intent(Act_Login.this, Act_Main.class);
                            startActivity(frm);
                        }

                        if (villaDialog != null){
                            villaDialog.dismiss();
                            villaDialog = null;
                        }
                    }
                };
                timer.start();
            }else{
                if(dialogType == DialogType.PROGRESS){
                    if(villaDialog != null){
                        villaDialog.dismiss();
                        villaDialog = null;
                    }

                    villaDialog = new VillaDialog(Act_Login.this, VillaDialog.DialogType.PROGRESS, message);
                    villaDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    villaDialog.setCanceledOnTouchOutside(false);
                    villaDialog.show();

                }else{
                    if(villaDialog != null) {
                        villaDialog.dismiss();
                        villaDialog = null;
                    }

                    villaDialog = new VillaDialog(Act_Login.this, VillaDialog.DialogType.MESSAGE, message, icon);
                    villaDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    villaDialog.setCanceledOnTouchOutside(true);
                    villaDialog.show();

                    CountDownTimer timer = new CountDownTimer(3000,1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                        }

                        @Override
                        public void onFinish() {

                            switch (ejecutar){
                                case RESTART_APP:
                                    restartApp();
                                    break;
                            }

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

    private void restartApp(){
        Intent i = new Intent(Act_Login.this, Act_Login.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Act_Login.this.startActivity(i);
    }
}