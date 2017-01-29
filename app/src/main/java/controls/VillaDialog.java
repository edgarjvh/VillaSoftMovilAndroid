package controls;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.villasoftgps.movil.R;

public class VillaDialog extends Dialog {

    public enum DialogType{
        PROGRESS,
        MESSAGE,
        PROMPT,
        IMAGE
    }

    private Context context;
    private int layout;
    private int icon;
    private String message;
    private Bitmap image;
    private DialogType dialogType;

    int pbarVis;
    int imgVis;

    public DialogListener getListener() {
        return listener;
    }

    public void setListener(DialogListener listener) {
        this.listener = listener;
    }

    private DialogListener listener;

    private ProgressBar pbarDialog;
    private ImageView imgIconDialog;
    private VillaTextView lblTextDialog;
    private View linButtons;
    private Button btnNo;
    private Button btnSi;

    public VillaDialog(Context context, DialogType dialogType, String message) {
        super(context);
        this.context = context;
        this.layout = R.layout.custom_dialog;
        this.message = message;
        this.dialogType = dialogType;
    }

    public VillaDialog(Context context, DialogType dialogType, Bitmap image) {
        super(context);
        this.context = context;
        this.layout = R.layout.image_dialog;
        this.image = image;
        this.dialogType = dialogType;
    }

    public VillaDialog(Context context, DialogType dialogType, String message, int icon) {
        super(context);
        this.context = context;
        this.layout = R.layout.custom_dialog;
        this.message = message;
        this.icon = icon;
        this.dialogType = dialogType;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(layout);

        switch (dialogType){
            case PROGRESS:
                pbarDialog = (ProgressBar)findViewById(R.id.pbarDialog);
                imgIconDialog = (ImageView)findViewById(R.id.imgIconDialog);
                lblTextDialog = (VillaTextView)findViewById(R.id.lblTextDialog);
                linButtons = findViewById(R.id.linButtons);

                linButtons.setVisibility(View.GONE);
                imgIconDialog.setImageResource(R.drawable.icon_loading);
                imgIconDialog.setVisibility(View.GONE);

                if (pbarDialog.getVisibility() != View.VISIBLE){
                    pbarDialog.setVisibility(View.VISIBLE);
                }

                lblTextDialog.setText(message);
                break;

            case MESSAGE:
                pbarDialog = (ProgressBar)findViewById(R.id.pbarDialog);
                imgIconDialog = (ImageView)findViewById(R.id.imgIconDialog);
                lblTextDialog = (VillaTextView)findViewById(R.id.lblTextDialog);
                linButtons = findViewById(R.id.linButtons);

                linButtons.setVisibility(View.GONE);
                imgIconDialog.setImageResource(icon);
                imgIconDialog.setVisibility(View.VISIBLE);
                pbarDialog.setVisibility(View.GONE);
                lblTextDialog.setText(message);
                break;

            case PROMPT:
                pbarDialog = (ProgressBar)findViewById(R.id.pbarDialog);
                imgIconDialog = (ImageView)findViewById(R.id.imgIconDialog);
                lblTextDialog = (VillaTextView)findViewById(R.id.lblTextDialog);
                linButtons = findViewById(R.id.linButtons);
                btnSi = (Button)findViewById(R.id.btnSi);
                btnNo = (Button)findViewById(R.id.btnNo);

                linButtons.setVisibility(View.VISIBLE);
                imgIconDialog.setImageResource(icon);
                imgIconDialog.setVisibility(View.VISIBLE);
                pbarDialog.setVisibility(View.GONE);
                lblTextDialog.setText(message);

                btnSi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.Ok();
                        dismiss();
                    }
                });

                btnNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.Cancel();
                        dismiss();
                    }
                });
                break;

            case IMAGE:

                break;
            default:
                break;
        }
    }

    public interface DialogListener
    {
        void Ok();
        void Cancel();
    }
}
