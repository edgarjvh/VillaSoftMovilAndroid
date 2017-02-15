package adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.villasoftgps.movil.R;

import java.util.ArrayList;
import java.util.List;
import controls.VillaImageView;
import controls.VillaTextView;
import models.MenuItem;

public class MenuItemAdapter extends ArrayAdapter {

    private Context c;
    private int res;
    private ArrayList<MenuItem> data;

    public MenuItemAdapter(Context c, int res, ArrayList<MenuItem> data) {
        super(c, res, data);
        this.c = c;
        this.res = res;
        this.data = data;
    }

    private static class MenuItemHolder{
        VillaImageView imgIcon;
        VillaTextView lblText;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        MenuItemHolder holder;

        if (convertView == null){
            LayoutInflater inflater = ((Activity)c).getLayoutInflater();
            convertView = inflater.inflate(res, parent, false);

            holder = new MenuItemHolder();
            holder.imgIcon = (VillaImageView)convertView.findViewById(R.id.imgIcon);
            holder.lblText = (VillaTextView)convertView.findViewById(R.id.lblText);

            convertView.setTag(holder);
        }else{
            holder = (MenuItemHolder)convertView.getTag();
        }

        MenuItem item = data.get(position);

        holder.imgIcon.setImageResource(item.getIcon());
        holder.lblText.setText(item.getText());

        return convertView;
    }
}
