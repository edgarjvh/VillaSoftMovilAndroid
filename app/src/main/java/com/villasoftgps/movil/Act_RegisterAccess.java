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

public class Act_RegisterAccess extends Activity {

    private VillaDialog villaDialog;
    private EditText txtEmail;
    private EditText txtPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_access);

        txtEmail = (EditText)findViewById(R.id.txtEmail);
        txtPassword = (EditText)findViewById(R.id.txtPassword);
        final EditText txtPasswordConfirmation = (EditText)findViewById(R.id.txtPasswordConfirm);

        Button btnNext = (Button)findViewById(R.id.btnRegisterAccess);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtEmail.getText().toString().equals("")){
                    String msj = getString(R.string.warning_must_enter_email);
                    mostrarMensaje(false, DialogType.MESSAGE,R.drawable.icon_warning,msj);
                    return;
                }

                if (txtPassword.getText().toString().equals("")){
                    String msj = getString(R.string.warning_must_enter_password);
                    mostrarMensaje(false, DialogType.MESSAGE,R.drawable.icon_warning,msj);
                    return;
                }

                if (txtPasswordConfirmation.getText().toString().equals("")){
                    String msj = getString(R.string.warning_must_enter_password_confirmation);
                    mostrarMensaje(false, DialogType.MESSAGE,R.drawable.icon_warning,msj);
                    return;
                }

                if (!txtPassword.getText().toString().equals(txtPasswordConfirmation.getText().toString())){
                    String msj = getString(R.string.warning_password_not_match);
                    mostrarMensaje(false, DialogType.MESSAGE,R.drawable.icon_warning,msj);
                    return;
                }

                new AsyncEmailValidation().execute(txtEmail.getText().toString().trim());
            }
        });
    }

    private class AsyncEmailValidation extends AsyncTask<String, Integer, Integer> {
        @Override
        protected Integer doInBackground(String... params) {
            publishProgress(0);

            ArrayList<Object> parametros = new ArrayList<>(2);
            parametros.add(0, "email*" + params[0]);
            parametros.add(1, "EmailValidation");

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
                    Intent frm = new Intent(Act_RegisterAccess.this,Act_RegisterPersonal.class);
                    frm.putExtra("email",txtEmail.getText().toString().trim());
                    frm.putExtra("password",txtPassword.getText().toString().trim());
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

                villaDialog = new VillaDialog(Act_RegisterAccess.this, VillaDialog.DialogType.MESSAGE, message,icon);
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

                    villaDialog = new VillaDialog(Act_RegisterAccess.this, VillaDialog.DialogType.PROGRESS, message);
                    villaDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    villaDialog.setCanceledOnTouchOutside(false);
                    villaDialog.show();

                }else{
                    if(villaDialog != null) {
                        villaDialog.dismiss();
                        villaDialog = null;
                    }

                    villaDialog = new VillaDialog(Act_RegisterAccess.this, VillaDialog.DialogType.MESSAGE, message, icon);
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
