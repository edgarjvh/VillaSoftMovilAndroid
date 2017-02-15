package adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.villasoftgps.movil.R;
import java.util.ArrayList;
import controls.VillaTextView;
import models.Device;

public class DevicesAdapter extends BaseAdapter {

    private Context c;
    private ArrayList<Device> data;

    public DevicesAdapter(Context c, ArrayList<Device> data){
        this.c = c;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View row, ViewGroup parent) {
        DeviceHolder deviceHolder;

        if (row == null){
            LayoutInflater inflater = ((Activity)c).getLayoutInflater();
            row = inflater.inflate(R.layout.custom_device,null);

            deviceHolder = new DeviceHolder();
            deviceHolder.parent = row.findViewById(R.id.relParent);
            deviceHolder.lblDeviceInfo = (VillaTextView)row.findViewById(R.id.lblDeviceInfo);
            deviceHolder.btnSubscription = (VillaTextView)row.findViewById(R.id.btnSubscription);
            deviceHolder.btnEdit = (VillaTextView) row.findViewById(R.id.btnEdit);
            deviceHolder.cboxShowOnMap = (CheckBox)row.findViewById(R.id.cboxShowOnMap);
            row.setTag(deviceHolder);
        }else{
            deviceHolder = (DeviceHolder)row.getTag();
        }

        final Device device = data.get(position);

        String deviceModel = device.getDeviceModel().getModel();
        String deviceImei = device.getImei();
        String vehicleLicense = device.getVehicle().getLicensePlate();
        String vehicleBrand = device.getVehicle().getBrand();
        String vehicleModel = device.getVehicle().getModel();
        String vehicleColor = device.getVehicle().getColor();
        String status;

        long now = System.currentTimeMillis();

        if (device.getExpirationDate() < now){
            status = c.getString(R.string.renewal);
            deviceHolder.parent.setBackgroundColor(Color.parseColor("#F5A9A9"));
        }else{
            status = c.getString(R.string.active);
            deviceHolder.parent.setBackgroundColor(Color.parseColor("#A9F5A9"));
        }

        if (device.getVehicle().getStatus() == 0){
            status = c.getString(R.string.inactive);
            deviceHolder.parent.setBackgroundColor(Color.parseColor("#F5A9A9"));
        }

        int vehicleYear = device.getVehicle().getYear();

        Spanned devicewInfo = Html.fromHtml(
                        "<font color='#000000'>" + c.getString(R.string.device) + ":</font> " +
                        "<font color='#610B0B'>" + deviceModel + "</font> " +
                        "<font color='#000000'>" + c.getString(R.string.imei) + ":</font> " +
                        "<font color='#610B0B'>" + deviceImei + "</font><br>" +
                        "<font color='#000000'>" + c.getString(R.string.vehicle) + ":</font> " +
                        "<font color='#610B0B'>" + vehicleBrand + "</font> " +
                        "<font color='#5E610B'>" + vehicleModel + "</font> " +
                        "<font color='#610B0B'>" + vehicleColor + "</font> " +
                        "<font color='#5E610B'>" + vehicleYear + "</font><br>" +
                        "<font color='#000000'>" + c.getString(R.string.license_plate) + ":</font> " +
                        "<font color='#610B0B'>" + vehicleLicense + "</font> " +
                        "<font color='#000000'>" + c.getString(R.string.status) + ":</font> " +
                        "<font color='#610B0B'>" + status + "</font> "

        );

        deviceHolder.lblDeviceInfo.setText(devicewInfo, TextView.BufferType.SPANNABLE);

        deviceHolder.btnSubscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(c,"Suscripcion Dispositivo " + device.getImei(),Toast.LENGTH_SHORT).show();
            }
        });

        deviceHolder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(c,"Editar Dispositivo " + device.getImei(),Toast.LENGTH_SHORT).show();
            }
        });

        deviceHolder.cboxShowOnMap.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Toast.makeText(c, Boolean.toString(isChecked) + " Posicion Nº " + Integer.toString(position),Toast.LENGTH_SHORT).show();
            }
        });

        return row;
    }

    private static class DeviceHolder{
        View parent;
        VillaTextView lblDeviceInfo;
        VillaTextView btnSubscription;
        VillaTextView btnEdit;
        CheckBox cboxShowOnMap;
    }
}