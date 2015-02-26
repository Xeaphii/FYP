package info.androidhive.tabsswipe;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import info.androidhive.tabsswipe.adapter.WifiDevice;

/**
 * Created by Sunny on 2/26/2015.
 */
public class Configure extends Activity{
    WifiManager wifi;
    int size = 0;
    String ITEM_KEY = "key";
    List<ScanResult> results;
    ArrayList<String> DevicesNames;
    WifiDevice adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configue_device);
        DevicesNames = new ArrayList<String>();

        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (wifi.isWifiEnabled() == false)
        {
            Toast.makeText(getApplicationContext(), "wifi is disabled..making it enabled", Toast.LENGTH_LONG).show();
            wifi.setWifiEnabled(true);
        }
        registerReceiver(new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context c, Intent intent)
            {
                results = wifi.getScanResults();
                size = results.size();
            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        DevicesNames.clear();


        Toast.makeText(this, "Scanning...." + size, Toast.LENGTH_SHORT).show();

    }
    public class GetSSID extends AsyncTask<String, Void, Boolean> {

        public GetSSID(Context activity) {
            this.activity = activity;
            dialog = new ProgressDialog(activity);
        }

        /**
         * progress dialog to show user that the backup is processing.
         */
        private ProgressDialog dialog;
        /**
         * application context.
         */
        private Context activity;

        protected void onPreExecute() {
            this.dialog.setMessage("Progress start");
            this.dialog.show();
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            adapter= new WifiDevice(activity, DevicesNames.toArray(new String[DevicesNames.size()]));
            ListView lv = (ListView) findViewById(R.id.listView);
            lv.setAdapter(adapter);
        }

        protected Boolean doInBackground(final String... args) {
            wifi.startScan();
            try
            {
                size = size - 1;
                while (size >= 0)
                {
                    size--;
                    DevicesNames.add( results.get(size).SSID);
                }
            }
            catch (Exception e)
            { }


            return false;
        }
    }
}
