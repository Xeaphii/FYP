package info.androidhive.tabsswipe;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import info.androidhive.tabsswipe.adapter.DeviceAdapter;

public class Devices extends Fragment {
    GridView gv;
    Context context;
    ArrayList prgmName;

    SharedPreferences prefs ;
    public static final String MyPREFERENCES = "MyPrefs" ;
    ConnectionDetector cd;
    String UserName;
    Boolean NoDevice = true;

    ArrayList<String> Names = new ArrayList<String>();
    ArrayList<String> Values = new ArrayList<String>();
    ArrayList<String> ImageLocs = new ArrayList<String>();

    public static int [] prgmImages={R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher
            ,R.drawable.ic_launcher,
            R.drawable.ic_launcher};
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {


		View rootView = inflater.inflate(R.layout.fragment_devices, container, false);
        prefs = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        cd = new ConnectionDetector(getActivity());
        UserName = prefs.getString("UserName", "");
        gv=(GridView) rootView.findViewById(R.id.gridView1);
        if (cd.isConnectingToInternet()) {
            new GetDevices(getActivity()).execute();
        }
		return rootView;
	}
    public class GetDevices extends AsyncTask<String, Void, Boolean> {

        public GetDevices(Context activity) {
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
            if (NoDevice)
            {

                Toast.makeText(activity, "No Devices", Toast.LENGTH_LONG).show();
            }else
            {
                String[] NamesArray = Names.toArray(new String[Names.size()]);
                gv.setAdapter(new DeviceAdapter(getActivity(), NamesArray,prgmImages));

            }
        }

        protected Boolean doInBackground(final String... args) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;
            String responseString = null;
            try {
                response = httpclient.execute(new HttpGet("http://xeamphiil.co.nf/GetDevices.php?username=" +
                        UserName +
                        ""));
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    responseString = out.toString();
                    out.close();
                    if (responseString.equals("0")) {
                        NoDevice = true;
                        Names.add("");
                        Values.add("");
                        ImageLocs.add("");
                    }
                    else
                    {
                        NoDevice = false;
                        String Devices[] = responseString.split(";");
                        for (int i = 0 ; i < Devices.length;i++)
                        {
                            Names.add(Devices[i].split(",")[2].split(":")[1]);
                            Values.add(Devices[i].split(",")[1].split(":")[1]);
                            ImageLocs.add(Devices[i].split(",")[3].split(":")[1]);
                        }
                        Names.add("");
                        Values.add("");
                        ImageLocs.add("");
                    }
                } else {
                    //Closes the connection.
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (ClientProtocolException e) {
                //TODO Handle problems..
            } catch (IOException e) {
                //TODO Handle problems..
            }
            return false;
        }
    }

}
