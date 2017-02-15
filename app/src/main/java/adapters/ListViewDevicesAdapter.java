package adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.ArrayList;
import models.Device;

public class ListViewDevicesAdapter extends BaseAdapter {

    private Context c;
    private ArrayList<Device> data;

    public ListViewDevicesAdapter(Context c, ArrayList<Device> data){
        this.c = c;
        this.data = data;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    private static class DeviceHolder{

    }
}
