package com.villasoftgps.movil;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import classes.Cliente;
import classes.WebService;
import controls.VillaDialog;
import controls.VillaImageView;

public class Act_Login extends AppCompatActivity {

    private SharedPreferences villaprefs;
    private static String PROPERTY_CLIENT = "cliente";
    private static String PROPERTY_PREFS = "preferencias";
    private static String TAG = "EJVH";
    private String mensaje;
    private VillaDialog villaDialog;
    private EditText txtCedula,txtClave;
    private Cliente cliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_login);

        VillaImageView imgLogo = (VillaImageView) findViewById(R.id.imgLogo);
        imgLogo.setImageResource(R.drawable.logo);
        imgLogo.setVisibility(View.VISIBLE);

        txtCedula = (EditText)findViewById(R.id.txtCedula);
        txtClave = (EditText)findViewById(R.id.txtClave);
        Button btnIngresar = (Button) findViewById(R.id.btnIngresar);

        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtCedula.getText().toString().trim().equals("")){
                    String msj = getString(R.string.warning_must_enter_cedula);
                    mostrarMensaje(false,DialogType.MESSAGE,R.drawable.icon_warning,msj);
                    return;
                }

                if (txtClave.getText().toString().trim().equals("")){
                    String msj = getString(R.string.warning_must_enter_clave);
                    mostrarMensaje(false,DialogType.MESSAGE,R.drawable.icon_warning,msj);
                    return;
                }

                new AsyncLogin().execute(
                        txtCedula.getText().toString().trim(),
                        txtClave.getText().toString().trim());
            }
        });
    }

    private class AsyncLogin extends AsyncTask<String, Integer, Integer> {
        @Override
        protected Integer doInBackground(String... params) {
            publishProgress(0);

            ArrayList<Object> parametros = new ArrayList<>(3);
            parametros.add(0, "cedula*" + params[0]);
            parametros.add(1, "clave*"+ params[1]);
            parametros.add(2, "Login");

            WebService ws = new WebService();
            Object response = ws.getData(parametros);

            try {
                JSONObject json = new JSONObject(response.toString());

                String result = json.get("Result").toString();

                switch (result) {
                    case "OK": {
                        JSONObject array = new JSONObject(json.get("Cliente").toString());

                        cliente = new Cliente();
                        cliente.setIdCliente(array.getInt("IdCliente"));
                        cliente.setCedula(array.getDouble("Cedula"));
                        cliente.setNombres(array.get("Nombres").toString());
                        cliente.setApellidos(array.getString("Apellidos"));
                        cliente.setTelefono1(array.getDouble("Telefono1"));
                        cliente.setTelefono2(array.getDouble("Telefono2"));
                        cliente.setDireccion(array.getString("Direccion"));
                        cliente.setCorreoElectronico(array.get("Direccion").toString());
                        cliente.setImagen(array.getString("Imagen"));
                        publishProgress(1);
                        return 1;
                    }
                    case "NO USER": {
                        publishProgress(2);
                        return 1;
                    }
                    case "NO PASS": {
                        publishProgress(3);
                        return 1;
                    }
                    case "NO ROWS": {
                        publishProgress(4);
                        return 1;
                    }
                    default:
                        mensaje = json.get("Message").toString();
                        publishProgress(5);
                        return 0;
                }
            } catch (JSONException e) {
                mensaje = e.getMessage();
                publishProgress(5);
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
                    mostrarMensaje(false,DialogType.PROGRESS,0,mensaje);
                    break;
                case 1:
                    mensaje = getString(R.string.bienvenidoCliente) + "\n" + cliente.getNombres() + " " + cliente.getApellidos();
                    mostrarMensaje(true, DialogType.MESSAGE, R.drawable.icon_info, mensaje);
                    break;
                case 2:
                    mensaje = getString(R.string.warning_no_user);
                    mostrarMensaje(false, DialogType.MESSAGE, R.drawable.icon_warning, mensaje);
                    break;
                case 3:
                    mensaje = getString(R.string.warning_no_pass);
                    mostrarMensaje(false, DialogType.MESSAGE, R.drawable.icon_warning, mensaje);
                    break;
                case 4:
                    mensaje = getString(R.string.warning_no_user);
                    mostrarMensaje(false, DialogType.MESSAGE, R.drawable.icon_warning, mensaje);
                    break;
                case 5:
                    mostrarMensaje(false, DialogType.MESSAGE, R.drawable.icon_error, mensaje);
                    break;
                case 6:
                    mostrarMensaje(false, DialogType.MESSAGE, R.drawable.icon_error, mensaje);
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

    private void mostrarMensaje(boolean isWelcome, DialogType dialogType, int icon, String message){
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
                        Intent frm = new Intent(Act_Login.this, Act_Main.class);
                        startActivity(frm);

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
                    villaDialog.setCanceledOnTouchOutside(true);
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
}