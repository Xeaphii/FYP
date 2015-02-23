package info.androidhive.tabsswipe;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import info.androidhive.tabsswipe.adapter.CustomRulesadapter;

public class Rules extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String color_names[] = {"red", "green", "blue", "yellow", "pink", "brown"};
        Integer image_id[] = {R.drawable.ic_launcher,
                R.drawable.ic_launcher, R.drawable.ic_launcher,
                R.drawable.ic_launcher, R.drawable.ic_launcher,
                R.drawable.ic_launcher};

        View rootView = inflater.inflate(R.layout.fragment_rules, container, false);
        CustomRulesadapter adapter = new CustomRulesadapter(getActivity(), image_id, color_names);
        ListView lv = (ListView) rootView.findViewById(R.id.listView);
        lv.setAdapter(adapter);
        return rootView;
    }
}
