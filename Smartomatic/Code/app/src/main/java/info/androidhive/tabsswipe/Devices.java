package info.androidhive.tabsswipe;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;

import info.androidhive.tabsswipe.adapter.DeviceAdapter;

public class Devices extends Fragment {
    GridView gv;
    Context context;
    ArrayList prgmName;
    public static String [] prgmNameList={"Let Us C","c++","JAVA","Jsp","Microsoft .Net","Android","PHP","Jquery","JavaScript"};
    public static int [] prgmImages={R.drawable.ic_launcher,R.drawable.ic_launcher,
            R.drawable.ic_launcher,R.drawable.ic_launcher,R.drawable.ic_launcher,R.drawable.ic_launcher,
            R.drawable.ic_launcher,R.drawable.ic_launcher,R.drawable.ic_launcher};
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_devices, container, false);
        gv=(GridView) rootView.findViewById(R.id.gridView1);
        gv.setAdapter(new DeviceAdapter(getActivity(), prgmNameList,prgmImages));
		return rootView;
	}

}
