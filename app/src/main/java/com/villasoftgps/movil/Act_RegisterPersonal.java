package com.villasoftgps.movil;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import classes.WebService;
import controls.VillaDialog;

public class Act_RegisterPersonal extends Activity {

    private VillaDialog villaDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__register_personal);

        final EditText txtFirstName = (EditText)findViewById(R.id.txtFirstName);
        final EditText txtLastName = (EditText)findViewById(R.id.txtLastName);
        final EditText txtPhoneNumber1 = (EditText)findViewById(R.id.txtPhoneNumber1);
        final EditText txtPhoneNumber2 = (EditText)findViewById(R.id.txtPhoneNumber2);
        final EditText txtAddress = (EditText)findViewById(R.id.txtAddress);
        Button btnSend = (Button)findViewById(R.id.btnRegisterPersonal);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtFirstName.getText().toString().equals("")){
                    String msj = getString(R.string.warning_must_enter_first_name);
                    mostrarMensaje(false, DialogType.MESSAGE,R.drawable.icon_warning,msj);
                    return;
                }

                if (txtPhoneNumber1.getText().toString().equals("")){
                    String msj = getString(R.string.warning_must_enter_phone_number1);
                    mostrarMensaje(false, DialogType.MESSAGE,R.drawable.icon_warning,msj);
                    return;
                }

                if (txtAddress.getText().toString().equals("")){
                    String msj = getString(R.string.warning_must_enter_address);
                    mostrarMensaje(false, DialogType.MESSAGE,R.drawable.icon_warning,msj);
                    return;
                }

                Bundle extras = getIntent().getExtras();

                if (extras != null) {
                    new AsyncSendRegistration().execute(
                            extras.getString("email"),
                            extras.getString("password"),
                            txtFirstName.getText().toString().trim(),
                            txtLastName.getText().toString().trim(),
                            txtPhoneNumber1.getText().toString().trim(),
                            txtPhoneNumber2.getText().toString().trim(),
                            txtAddress.getText().toString().trim());
                }
            }
        });
    }

    private class AsyncSendRegistration extends AsyncTask<String, Integer, Integer> {
        @Override
        protected Integer doInBackground(String... params) {
            publishProgress(0);

            ArrayList<Object> parametros = new ArrayList<>(8);
            parametros.add(0, "email*" + params[0]);
            parametros.add(1, "password*" + params[1]);
            parametros.add(2, "firstName*" + params[2]);
            parametros.add(3, "lastName*" + params[3]);
            parametros.add(4, "phoneNumber1*" + params[4]);
            parametros.add(5, "phoneNumber2*" + params[5]);
            parametros.add(6, "address*" + params[6]);
            parametros.add(7, "ClientRegistration");

            WebService ws = new WebService();
            Object response = ws.getData(parametros);

            try {
                JSONObject json = new JSONObject(response.toString());

                String result = json.get("Result").toString();

                switch (result) {
                    case "OK": {
                        publishProgress(1);
                        return 1;
                    }
                    case "EXISTS": {
                        publishProgress(2);
                        return 1;
                    }
                    default:
                        publishProgress(3);
                        return 0;
                }
            } catch (JSONException e) {
                publishProgress(3);
                return 0;
            }
        }

        @SuppressWarnings("ConstantConditions")
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            switch (values[0]){
                case 0:
                    String mensaje = getString(R.string.validating_email);
                    mostrarMensaje(false,DialogType.PROGRESS,0, mensaje);
                    break;
                case 1:
                    Intent frm = new Intent(Act_RegisterPersonal.this,Act_RegisterPersonal.class);
                    startActivity(frm);

                    if (villaDialog != null){
                        villaDialog.dismiss();
                        villaDialog = null;
                    }
                    break;
                case 2:
                    mensaje = getString(R.string.email_duplicated);
                    mostrarMensaje(false, DialogType.MESSAGE, R.drawable.icon_warning, mensaje);
                    break;
                case 3:
                    mensaje = getString(R.string.connection_error);
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

                villaDialog = new VillaDialog(Act_RegisterPersonal.this, VillaDialog.DialogType.MESSAGE, message,icon);
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

                    villaDialog = new VillaDialog(Act_RegisterPersonal.this, VillaDialog.DialogType.PROGRESS, message);
                    villaDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    villaDialog.setCanceledOnTouchOutside(false);
                    villaDialog.show();

                }else{
                    if(villaDialog != null) {
                        villaDialog.dismiss();
                        villaDialog = null;
                    }

                    villaDialog = new VillaDialog(Act_RegisterPersonal.this, VillaDialog.DialogType.MESSAGE, message, icon);
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
