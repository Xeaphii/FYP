package info.androidhive.tabsswipe;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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

public class SignInActivity extends Activity {

    TextView UserName, Password;
    Boolean Verification = false;
    int Version = 0;
    Button Verf;
    String User,Passwd;
    SharedPreferences prefs ;
    public static final String MyPREFERENCES = "MyPrefs" ;
    ConnectionDetector cd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        cd = new ConnectionDetector(getApplicationContext());
        setContentView(R.layout.activity_sign_in_screen);
        UserName = (TextView) findViewById(R.id.etUserName);
        Password = (TextView) findViewById(R.id.etPass);
        Verf = (Button) findViewById(R.id.btnSingIn);

        Verf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cd.isConnectingToInternet()) {
                    User = UserName.getText().toString();
                    Passwd = Password.getText().toString();
                    new VerifyUserTask(SignInActivity.this).execute();
                    //
                }else{
                    Toast.makeText(getApplicationContext(),"No Internet Present.",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public class VerifyUserTask extends AsyncTask<String, Void, Boolean> {

        public VerifyUserTask(Context activity) {
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
            if (Verification)
            {
                prefs.edit().putString("is_initialized","1").commit();
                prefs.edit().putString("Version",Integer.toString(Version)).commit();
                prefs.edit().putString("UserName", User).commit();

                //Toast.makeText(getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("is_initialized", "0"), Toast.LENGTH_LONG).show();

                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
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
                response = httpclient.execute(new HttpGet("http://xeamphiil.co.nf/user_verf.php?username=" +
                        User +
                        "&password=" +
                        Passwd +
                        ""));
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    responseString = out.toString();
                    out.close();
                    if (responseString.equals("0"))
                        Verification=false;
                    else
                    {
                        Verification = true;
                        Version = Integer.parseInt(responseString.split(":")[1].trim());
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
    

