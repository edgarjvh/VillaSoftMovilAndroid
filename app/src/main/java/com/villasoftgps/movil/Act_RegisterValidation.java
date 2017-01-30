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
import controls.VillaTextView;

public class Act_RegisterValidation extends Activity {

    private VillaDialog villaDialog;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_validation);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Bundle extras = getIntent().getExtras();
        email = extras == null ? "" : extras.getString("email");

        final VillaTextView lblEmail = (VillaTextView)findViewById(R.id.lblEmail);
        lblEmail.setText(email);

        final EditText txtValidationCode = (EditText)findViewById(R.id.txtValidatioCode);

        VillaTextView lblGetNewCode = (VillaTextView)findViewById(R.id.lblGetNewCode);
        Button btnValidationCode = (Button)findViewById(R.id.btnValidateCode);

        lblGetNewCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncGetNewCode().execute(lblEmail.getText().toString());
            }
        });

        btnValidationCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtValidationCode.getText().toString().trim().equals("")){
                    String mensaje = getString(R.string.validation_code_empty);
                    mostrarMensaje(false, DialogType.MESSAGE, R.drawable.icon_warning, mensaje);
                    return;
                }

                if (txtValidationCode.getText().toString().trim().length() < 8){
                    String mensaje = getString(R.string.validation_code_incomplete);
                    mostrarMensaje(false, DialogType.MESSAGE, R.drawable.icon_warning, mensaje);
                    return;
                }

                new AsyncSendValidationCode().execute(email, txtValidationCode.getText().toString().trim());
            }
        });

        Button btnCancel = (Button)findViewById(R.id.btnCancel);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent frm = new Intent(Act_RegisterValidation.this,Act_Login.class);
                startActivity(frm);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private class AsyncGetNewCode extends AsyncTask<String, Integer, Integer> {
        @Override
        protected Integer doInBackground(String... params) {
            publishProgress(0);

            ArrayList<Object> parametros = new ArrayList<>(2);
            parametros.add(0, "email*" + params[0]);
            parametros.add(1, "UpdateValidationCode");

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
                        Log.d("EJVH result1", json.get("Message").toString());
                        publishProgress(2);
                        return 0;
                }
            } catch (JSONException e) {
                Log.d("EJVH result2", e.getMessage());
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
                    String mensaje = getString(R.string.getting_validation_code);
                    mostrarMensaje(false,DialogType.PROGRESS,0, mensaje);
                    break;
                case 1:
                    mensaje = getString(R.string.validation_code_updated);
                    mostrarMensaje(false, DialogType.MESSAGE, R.drawable.icon_ok, mensaje);
                    break;
                case 2:
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

    private class AsyncSendValidationCode extends AsyncTask<String, Integer, Integer> {
        @Override
        protected Integer doInBackground(String... params) {
            publishProgress(0);

            ArrayList<Object> parametros = new ArrayList<>(3);
            parametros.add(0, "email*" + params[0]);
            parametros.add(1, "validationCode*" + params[1]);
            parametros.add(2, "ConfirmValidationCode");

            WebService ws = new WebService();
            Object response = ws.getData(parametros);

            try {
                JSONObject json = new JSONObject(response.toString());

                String result = json.get("Result").toString();

                switch (result) {
                    case "OK":
                        publishProgress(1);
                        return 1;
                    case "WRONG CODE":
                        publishProgress(2);
                        return 1;
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
                    String mensaje = getString(R.string.sending_validation_code);
                    mostrarMensaje(false,DialogType.PROGRESS,0, mensaje);
                    break;
                case 1:
                    mensaje = getString(R.string.message_code_validated);
                    mostrarMensaje(true, DialogType.MESSAGE, R.drawable.icon_ok, mensaje);
                    break;
                case 2:
                    mensaje = getString(R.string.wrong_validation_code);
                    mostrarMensaje(false, DialogType.MESSAGE, R.drawable.icon_warning, mensaje);
                    break;
                case 3:
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

                villaDialog = new VillaDialog(Act_RegisterValidation.this, VillaDialog.DialogType.MESSAGE, message,icon);
                villaDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                villaDialog.setCanceledOnTouchOutside(false);
                villaDialog.show();

                CountDownTimer timer = new CountDownTimer(3000,1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {
                        Intent frm = new Intent(Act_RegisterValidation.this,Act_Login.class);
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

                    villaDialog = new VillaDialog(Act_RegisterValidation.this, VillaDialog.DialogType.PROGRESS, message);
                    villaDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    villaDialog.setCanceledOnTouchOutside(false);
                    villaDialog.show();

                }else{
                    if(villaDialog != null) {
                        villaDialog.dismiss();
                        villaDialog = null;
                    }

                    villaDialog = new VillaDialog(Act_RegisterValidation.this, VillaDialog.DialogType.MESSAGE, message, icon);
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
