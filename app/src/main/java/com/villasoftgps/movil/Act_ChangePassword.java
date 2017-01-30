package com.villasoftgps.movil;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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

public class Act_ChangePassword extends Activity {

    private VillaDialog villaDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Bundle extras = getIntent().getExtras();
        final String email = extras == null ? "" : extras.getString("email");

        final EditText txtPassword = (EditText)findViewById(R.id.txtPassword);
        final EditText txtPasswordConfirm = (EditText)findViewById(R.id.txtPasswordConfirm);
        Button btnChangePassword = (Button)findViewById(R.id.btnChangePassword);



        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtPassword.getText().toString().trim().equals("")){
                    String mensaje = getString(R.string.warning_must_enter_password);
                    mostrarMensaje(false,DialogType.MESSAGE,R.drawable.icon_warning,mensaje);
                    return;
                }

                if (txtPassword.getText().toString().trim().length() < 8){
                    String mensaje = getString(R.string.warning_password_incomplete);
                    mostrarMensaje(false,DialogType.MESSAGE,R.drawable.icon_warning,mensaje);
                    return;
                }

                if (txtPasswordConfirm.getText().toString().trim().equals("")){
                    String msj = getString(R.string.warning_must_enter_password_confirmation);
                    mostrarMensaje(false, DialogType.MESSAGE,R.drawable.icon_warning,msj);
                    return;
                }

                if (!txtPassword.getText().toString().trim().equals(txtPasswordConfirm.getText().toString().trim())){
                    String msj = getString(R.string.warning_password_not_match);
                    mostrarMensaje(false, DialogType.MESSAGE,R.drawable.icon_warning,msj);
                    return;
                }

                new AsyncChangePassword().execute(email, txtPassword.getText().toString().trim());
            }
        });
        txtPassword.requestFocus();
    }

    private class AsyncChangePassword extends AsyncTask<String, Integer, Integer> {
        @Override
        protected Integer doInBackground(String... params) {
            publishProgress(0);

            ArrayList<Object> parametros = new ArrayList<>(3);
            parametros.add(0, "email*" + params[0]);
            parametros.add(1, "password*" + params[1]);
            parametros.add(2, "ChangePassword");

            WebService ws = new WebService();
            Object response = ws.getData(parametros);

            try {
                JSONObject json = new JSONObject(response.toString());

                String result = json.get("Result").toString();

                switch (result) {
                    case "OK":
                        publishProgress(1);
                        return 1;
                    default:
                        publishProgress(2);
                        return 0;
                }
            } catch (JSONException e) {
                publishProgress(2);
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
                    mensaje = getString(R.string.password_changed);
                    mostrarMensaje(true,DialogType.MESSAGE,R.drawable.icon_ok,mensaje);
                    break;
                case 2:
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

                villaDialog = new VillaDialog(Act_ChangePassword.this, VillaDialog.DialogType.MESSAGE, message,icon);
                villaDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                villaDialog.setCanceledOnTouchOutside(false);
                villaDialog.show();

                CountDownTimer timer = new CountDownTimer(3000,1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {
                        Intent frm = new Intent(Act_ChangePassword.this,Act_Login.class);
                        startActivity(frm);
                        finish();

                        if(villaDialog != null) {
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

                    villaDialog = new VillaDialog(Act_ChangePassword.this, VillaDialog.DialogType.PROGRESS, message);
                    villaDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    villaDialog.setCanceledOnTouchOutside(false);
                    villaDialog.show();

                }else{
                    if(villaDialog != null) {
                        villaDialog.dismiss();
                        villaDialog = null;
                    }

                    villaDialog = new VillaDialog(Act_ChangePassword.this, VillaDialog.DialogType.MESSAGE, message, icon);
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
