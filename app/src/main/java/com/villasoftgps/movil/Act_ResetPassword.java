package com.villasoftgps.movil;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import classes.WebService;
import controls.VillaDialog;

public class Act_ResetPassword extends Activity {

    private VillaDialog villaDialog;
    private EditText txtEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        txtEmail = (EditText)findViewById(R.id.txtEmail);
        Button btnResetPassword = (Button)findViewById(R.id.btnResetPassword);

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtEmail.getText().toString().trim().equals("")){
                    String mensaje = getString(R.string.warning_must_enter_email);
                    mostrarMensaje(false,DialogType.MESSAGE,R.drawable.icon_warning,mensaje);
                    return;
                }

                new AsyncResetPassword().execute(txtEmail.getText().toString().trim());
            }
        });
    }

    private class AsyncResetPassword extends AsyncTask<String, Integer, Integer> {
        @Override
        protected Integer doInBackground(String... params) {
            publishProgress(0);

            ArrayList<Object> parametros = new ArrayList<>(2);
            parametros.add(0, "email*" + params[0]);
            parametros.add(1, "ResetPassword");

            WebService ws = new WebService();
            Object response = ws.getData(parametros);

            try {
                JSONObject json = new JSONObject(response.toString());

                String result = json.get("Result").toString();

                switch (result) {
                    case "OK":
                        publishProgress(1);
                        return 1;
                    case "UNVALIDATED":
                        publishProgress(2);
                        return 1;
                    case "NO USER":
                        publishProgress(3);
                        return 1;
                    default:
                        Log.d("EJVH resetting1",json.get("Message").toString());
                        publishProgress(4);
                        return 0;
                }
            } catch (JSONException e) {
                Log.d("EJVH resetting2",e.getMessage());
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
                    String mensaje = getString(R.string.getting_reset_password_code);
                    mostrarMensaje(false,DialogType.PROGRESS,0, mensaje);
                    break;
                case 1:
                    mensaje = getString(R.string.reset_password_code_updated);
                    mostrarMensaje(true, DialogType.MESSAGE, R.drawable.icon_ok, mensaje);
                    break;
                case 2:
                    mensaje = getString(R.string.unvalidated_email);
                    mostrarMensaje(false, DialogType.MESSAGE, R.drawable.icon_warning, mensaje);
                    break;
                case 3:
                    mensaje = getString(R.string.warning_no_user);
                    mostrarMensaje(false, DialogType.MESSAGE, R.drawable.icon_warning, mensaje);
                    break;
                case 4:
                    mensaje = getString(R.string.error_getting_validation_code);
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

                villaDialog = new VillaDialog(Act_ResetPassword.this, VillaDialog.DialogType.MESSAGE, message,icon);
                villaDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                villaDialog.setCanceledOnTouchOutside(false);
                villaDialog.show();

                CountDownTimer timer = new CountDownTimer(3000,1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {
                        Intent frm = new Intent(Act_ResetPassword.this,Act_ResetPasswordValidation.class);
                        frm.putExtra("email",txtEmail.getText().toString().trim());
                        startActivity(frm);

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

                    villaDialog = new VillaDialog(Act_ResetPassword.this, VillaDialog.DialogType.PROGRESS, message);
                    villaDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    villaDialog.setCanceledOnTouchOutside(false);
                    villaDialog.show();

                }else{
                    if(villaDialog != null) {
                        villaDialog.dismiss();
                        villaDialog = null;
                    }

                    villaDialog = new VillaDialog(Act_ResetPassword.this, VillaDialog.DialogType.MESSAGE, message, icon);
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
