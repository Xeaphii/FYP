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

public class SignUpActivity extends Activity {

    TextView UserName, Password, Email;
    Boolean UserNameAvailable = false,Signed= false;
    int Version = 0;
    Button SignUp;
    String User, Passwd, MailAddress;
    SharedPreferences prefs;
    public static final String MyPREFERENCES = "MyPrefs" ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);;
        setContentView(R.layout.activity_sign_up_screen);
        UserName = (TextView) findViewById(R.id.etUserName);
        Password = (TextView) findViewById(R.id.etPass);
        SignUp = (Button) findViewById(R.id.btnSingUp);
        Email = (TextView) findViewById(R.id.etEmail);
        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User = UserName.getText().toString();
                //Passwd = Password.getText().toString();
                //MailAddress= Email.getText().toString();
                new CheckUserNameAvailability(SignUpActivity.this).execute();
                //
            }
        });
    }

    public class CheckUserNameAvailability extends AsyncTask<String, Void, Boolean> {

        public CheckUserNameAvailability(Context activity) {
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
            if (UserNameAvailable) {
                // PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("is_initialized ", "1").commit();
                //PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("Version ", Integer.toString(Version)).commit();
                User = UserName.getText().toString();
                Passwd = Password.getText().toString();
                MailAddress = Email.getText().toString();
                new SignUp(SignUpActivity.this).execute();

            }else{
                Toast.makeText(getApplicationContext(),"Username already exist. Choose different username",Toast.LENGTH_LONG).show();

            }
        }

        protected Boolean doInBackground(final String... args) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;
            String responseString = null;
            try {
                response = httpclient.execute(new HttpGet("http://xeamphiil.co.nf/UserNameCheck.php?username=" +
                        User +
                        ""));
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    responseString = out.toString();
                    out.close();
                    if (responseString.equals("1"))
                        UserNameAvailable = false;
                    else {
                        UserNameAvailable = true;
                        //Version = Integer.parseInt(responseString.split(":")[1].trim());
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

    public class SignUp extends AsyncTask<String, Void, Boolean> {

        public SignUp(Context activity) {
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
            if (Signed) {
                prefs.edit().putString("is_initialized","1").commit();
                prefs.edit().putString("Version",Integer.toString(Version)).commit();
                prefs.edit().putString("UserName",User).commit();
                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
            }
            else{
                Toast.makeText(getApplicationContext(),"Error occur while Signing up",Toast.LENGTH_LONG).show();
            }
        }

        protected Boolean doInBackground(final String... args) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;
            String responseString = null;
            try {
                response = httpclient.execute(new HttpGet("http://xeamphiil.co.nf/SignUp.php?username=" +
                        User +
                        "&password=" +
                        Passwd +
                        "&email=" +
                        MailAddress +
                        ""));
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    responseString = out.toString();
                    out.close();
                    if (responseString.equals("Success"))
                        Signed = true;
                    else {
                        Signed = false;
                        //Version = Integer.parseInt(responseString.split(":")[1].trim());
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
    

