package info.androidhive.tabsswipe.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import info.androidhive.tabsswipe.R;

public class WifiDevice extends ArrayAdapter<String> {
    String[] color_names;
    Context context;

    public WifiDevice(Context context, String[] text) {
        super(context, R.layout.wifi_device, text);
        // TODO Auto-generated constructor stub
        this.color_names = text;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View single_row = inflater.inflate(R.layout.wifi_device, null,
                true);
        TextView textView = (TextView) single_row.findViewById(R.id.textView);
        textView.setText(color_names[position]);
        return single_row;
    }
}