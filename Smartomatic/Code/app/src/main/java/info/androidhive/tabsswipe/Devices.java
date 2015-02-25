package info.androidhive.tabsswipe;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;

import info.androidhive.tabsswipe.adapter.DeviceAdapter;

public class Devices extends Fragment {
    GridView gv;
    Context context;
    ArrayList prgmName;
    String Version;

    SharedPreferences prefs;
    public static final String MyPREFERENCES = "MyPrefs";
    ConnectionDetector cd;
    String UserName;
    Boolean NoDevice = true;
    String DevicesStore;

    ArrayList<String> Names = new ArrayList<String>();
    ArrayList<String> Values = new ArrayList<String>();
    ArrayList<String> ImageLocs = new ArrayList<String>();
    ArrayList<String> Keys = new ArrayList<String>();

    public static int[] prgmImages = {R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher
            , R.drawable.ic_launcher,
            R.drawable.ic_launcher};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_devices, container, false);
        prefs = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        cd = new ConnectionDetector(getActivity());
        UserName = prefs.getString("UserName", "");
        Version = prefs.getString("Version", "");
        gv = (GridView) rootView.findViewById(R.id.gridView1);

        if (cd.isConnectingToInternet()) {
            new CheckVersion(getActivity()).execute("");
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

                    } else {
                        DevicesStore = responseString;
                        prefs.edit().putString("DevicesStore", DevicesStore).commit();
                        new SetItems().execute("");
                        NoDevice = false;

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

    public class SetItems extends AsyncTask<String, Void, Boolean> {


        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (NoDevice) {

                Toast.makeText(getActivity(), "No Devices", Toast.LENGTH_LONG).show();
            } else {
                String[] NamesArray = Names.toArray(new String[Names.size()]);
                String[] ImagsArray = ImageLocs.toArray(new String[ImageLocs.size()]);
                String[] KeysArray = Keys.toArray(new String[Keys.size()]);
                String[] ValuessArray = Values.toArray(new String[Values.size()]);
                gv.setAdapter(new DeviceAdapter(getActivity(), NamesArray, ImagsArray,ValuessArray,KeysArray));

            }
        }

        protected Boolean doInBackground(final String... args) {
            if (NoDevice) {
                Names.add("");
                Values.add("");
                ImageLocs.add("");
            } else {
                String Devices[] = DevicesStore.split(";");
                for (int i = 0; i < Devices.length; i++) {
                    Names.add(Devices[i].split(",")[2].split(":")[1]);
                    Values.add(Devices[i].split(",")[1].split(":")[1]);
                    ImageLocs.add(Devices[i].split(",")[3].split(":")[1]);
                    Keys.add(Devices[i].split(",")[0].split(":")[1]);
                    String url = "http://xeamphiil.co.nf/user/"+MD5("")+"/"+Devices[i].split(",")[3].split(":")[1];
                    DownloadImage(Devices[i].split(",")[3].split(":")[1],url);
                }
                Names.add("");
                Values.add("");
                ImageLocs.add("");
            }
            return false;
        }
    }

    public void DownloadImage(String FileName,String Url) {
        String path = Environment.getExternalStorageDirectory().toString();
        File file = new File(path, "Smartomatic" + FileName); // the File to save to


        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            getBitmapFromURL(Url).compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Bitmap getBitmapFromURL(String src) {
        try {
            java.net.URL url = new java.net.URL(src);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public class CheckVersion extends AsyncTask<String, Void, Boolean> {

        public CheckVersion(Context activity) {
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
        }

        protected Boolean doInBackground(final String... args) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;
            String responseString = null;
            try {
                response = httpclient.execute(new HttpGet("http://xeamphiil.co.nf/VersionCheck.php?username=" +
                        UserName +
                        ""));
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    responseString = out.toString();
                    out.close();
                    if (responseString.equals(Version)) {
                        new GetStatus(getActivity()).execute("");
                    } else {
                        if (cd.isConnectingToInternet()) {
                            new GetDevices(getActivity()).execute();
                        }
                        Version = responseString;
                        prefs.edit().putString("Version", Version).commit();
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
    public class GetStatus extends AsyncTask<String, Void, Boolean> {

        public GetStatus(Context activity) {
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

        }

        protected Boolean doInBackground(final String... args) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;
            String responseString = null;
            try {
                response = httpclient.execute(new HttpGet("http://xeamphiil.co.nf/GetStatus.php?username=" +
                        UserName +
                        ""));
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    responseString = out.toString();
                    out.close();
                    if (responseString.equals("0")) {
//                        NoDevice = true;

                    } else {
                        String Response[] = responseString.split(",");

                        DevicesStore = prefs.getString("DevicesStore", "");

                        String Devices[] = DevicesStore.split(";");
                        for (int i = 0; i < Devices.length; i++) {
                            Keys.add(Devices[i].split(",")[0].split(":")[1]);
                            Names.add(Devices[i].split(",")[2].split(":")[1]);
                            Values.add(Response[i].split(":")[1]);
                            ImageLocs.add(Devices[i].split(",")[3].split(":")[1]);
                        }
                        DevicesStore = "";
                        for (int i = 0 ; i < Keys.size();i++)
                        {
                            DevicesStore +="key:"+Keys.get(i)+",value:"+Values.get(i)+",name:"+Names.get(i)+
                            ",image_loc:"+ImageLocs+";";
                        }
                        prefs.edit().putString("DevicesStore", DevicesStore).commit();
                        new SetItems().execute();
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

    public String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }
}
