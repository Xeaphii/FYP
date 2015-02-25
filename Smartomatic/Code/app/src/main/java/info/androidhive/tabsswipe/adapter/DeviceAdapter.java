package info.androidhive.tabsswipe.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.io.File;

import info.androidhive.tabsswipe.R;

public class DeviceAdapter extends BaseAdapter{

    String [] Names;
    String [] Values;
    Context context;
    String [] imageId;
    private static LayoutInflater inflater=null;
    public DeviceAdapter(Context mainActivity, String[] prgmNameList, String[] ImageLoc) {
        // TODO Auto-generated constructor stub
        Names=prgmNameList;
        context=mainActivity;
        imageId=ImageLoc;
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
        Holder holder=new Holder();
        View rowView;

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

            if(imgFile.exists()){

                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                holder.img.setImageBitmap(myBitmap);


            }

        }
        rowView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(context, "You Clicked "+Names[position], Toast.LENGTH_LONG).show();
            }
        });

        return rowView;
    }

}