package info.androidhive.tabsswipe;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

/**
 * Created by Sunny on 2/26/2015.
 */
public class AddDevice extends Activity {
    EditText DName;
    Button Configure, AddDevice;
    private static final int CAMERA_REQUEST = 1888;
    private ImageView imageView;
    SharedPreferences prefs;
    public static final String MyPREFERENCES = "MyPrefs";
    ConnectionDetector cd;
    String UserName;
    String DeviceName;
    Boolean verification = false;
    String ImageString;
    String DevicesStore;
    String Key,ImageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_device);
        this.imageView = (ImageView) this.findViewById(R.id.imageView1);
        prefs = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        cd = new ConnectionDetector(getApplicationContext());
        UserName = prefs.getString("UserName", "");
        DName = (EditText) findViewById(R.id.et_device_name);
        DevicesStore = prefs.getString("DevicesStore", "");
        Configure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),Configure.class);
                startActivity(i);
            }
        });

        AddDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AddDeviceToServer(AddDevice.this).execute("");
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DeviceName = DName.getText().toString();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            ImageString = BitMapToString(photo);
            String FileName = DeviceName.toLowerCase();
            FileName = FileName.replace(" ","_");
            FileName +=".jpg";
            SaveBitmap(photo,FileName);
            imageView.setImageBitmap(photo);
        }
    }

    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();

        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }
    public class AddDeviceToServer extends AsyncTask<String, Void, Boolean> {

        public AddDeviceToServer(Context activity) {
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
            if (verification)
            {
                String newDevice = "key:" +
                        Key +
                        ",value:" +
                        "0" +
                        ",name:" +
                        DeviceName +
                        "image_loc:" +
                        ImageName +
                        ";";
                DevicesStore +=newDevice;
                prefs.edit().putString("DevicesStore", DevicesStore).commit();
                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
                finish();
            }else
            {
                Toast.makeText(getApplicationContext(), "Authentication Failed", Toast.LENGTH_LONG).show();

            }
        }

        protected Boolean doInBackground(final String... args) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;
            String responseString = null;
            try {
                response = httpclient.execute(new HttpGet("http://xeamphiil.co.nf/add_device.php?username=" +
                        UserName +
                        "&name=" +
                        DeviceName +
                        "&image=" +
                        ImageString +
                        ""));
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    responseString = out.toString();
                    out.close();
                    if (responseString.equals("0"))
                        verification=false;
                    else
                    {
                        verification = true;
                        Key = responseString.split(";")[1];
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
    public void SaveBitmap(Bitmap Image,String FileName) {
        String path = Environment.getExternalStorageDirectory().toString();
        File file = new File(path, "Smartomatic" + FileName); // the File to save to

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            Image.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
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
}
