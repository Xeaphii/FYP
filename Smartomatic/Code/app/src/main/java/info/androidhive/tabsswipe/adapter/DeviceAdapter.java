package info.androidhive.tabsswipe.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import info.androidhive.tabsswipe.ConnectionDetector;
import info.androidhive.tabsswipe.R;

public class DeviceAdapter extends BaseAdapter{

    String [] Names;
    String [] Values;
    Context context;
    String [] imageId;
    String [] Keys;
    private static LayoutInflater inflater=null;
    SharedPreferences prefs;
    public static final String MyPREFERENCES = "MyPrefs";
    ConnectionDetector cd;
    String UserName;

    public DeviceAdapter(Context mainActivity, String[] prgmNameList, String[] ImageLoc,String[] values,String [] keys) {
        // TODO Auto-generated constructor stub
        prefs = mainActivity.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        cd = new ConnectionDetector(mainActivity);
        UserName = prefs.getString("UserName", "");
        Names=prgmNameList;
        context=mainActivity;
        imageId=ImageLoc;
        Values=values;
        Keys = keys;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return Names.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder
    {
        TextView tv;
        ImageView img;
        ToggleButton tb;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final Holder holder=new Holder();
        final View rowView;

        rowView = inflater.inflate(R.layout.device_layout, null);
        holder.tv=(TextView) rowView.findViewById(R.id.textView1);
        holder.img=(ImageView) rowView.findViewById(R.id.imageView1);
        holder.tb=(ToggleButton) rowView.findViewById(R.id.chkState);
        if (position == getCount()-1){
            holder.tv.setText("");
            holder.tb.setVisibility(View.GONE);
            holder.img.setImageResource(R.drawable.add);
            holder.img.getLayoutParams().height = 170;
            holder.img.getLayoutParams().width = 170;

        }else {
            holder.tv.setText(Names[position]);
           // File imgFile = new  File(imageId[position]);
            String path = Environment.getExternalStorageDirectory().toString();
            File imgFile = new File(path, "Smartomatic" + imageId[position]);
            holder.tb.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    ToggleButton t = (ToggleButton) rowView.findViewById(R.id.chkState);
                    new SetStatus().execute(Integer.toString(position),Boolean.toString(t.isChecked()));
                }
            });
            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                holder.img.setImageBitmap(myBitmap);
            }
            if (Values.equals("0"))
                holder.tb.setChecked(false);
            else
                holder.tb.setChecked(true);

        }
        rowView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(context, "You Clicked " + Names[position], Toast.LENGTH_LONG).show();
            }
        });

        return rowView;
    }

    public class SetStatus extends AsyncTask<String, Void, Boolean> {



        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(final Boolean success) {

        }

        protected Boolean doInBackground(final String... args) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;
            String responseString = null;
            try {
                response = httpclient.execute(new HttpGet("http://xeamphiil.co.nf/ChangeStatus.php?username=" +
                        UserName +
                        "&key=" +
                        Keys[Integer.parseInt(args[0])] +
                        "&value=" +
                        args[1]));
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    responseString = out.toString();
                    out.close();

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